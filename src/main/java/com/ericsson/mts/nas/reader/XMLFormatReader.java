/*
 * Copyright 2019 Ericsson, https://www.ericsson.com/en
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.ericsson.mts.nas.reader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class XMLFormatReader implements FormatReader {
    private static String IS_ARRAY = "isArray";
    private Logger logger = LoggerFactory.getLogger(XMLFormatReader.class.getSimpleName());
    private String ignoredObject;
    private Element currentNode;
    private Stack<Element> arrayStack = new Stack<Element>();

    private boolean elementExist = true;

    public XMLFormatReader(Element rootNode, String type) {
        currentNode = rootNode;
        ignoredObject = type;
    }

    public XMLFormatReader(File file, String type) throws Exception {
        this(new FileInputStream(file), type);
    }

    public XMLFormatReader(InputStream inputStream, String type) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        dbf.setXIncludeAware(false);
        dbf.setExpandEntityReferences(false);
        this.currentNode = dbf.newDocumentBuilder().parse(inputStream).getDocumentElement();
        print(this.currentNode);
        ignoredObject = type;
    }

    public void enterObject(String name) {
        if (!ignoredObject.equals(name)) {
            elementExist = true;
            if (name != null) {
                logger.trace("Enter object {}", name);

                currentNode = getChildNode(getFromStack(currentNode), name);
            } else {
                if (!currentNode.getAttribute(IS_ARRAY).equals("")) {
                    if ("".equals(currentNode.getAttribute(IS_ARRAY))) {
                        throw new RuntimeException();
                    }
                    logger.trace("Enter array field ");
                    currentNode = getFromStack(currentNode);
                } else {
                    throw new RuntimeException();
                }
            }
        }
    }

    public void leaveObject(String name) {
        if (!ignoredObject.equals(name)) {
            if (name != null) {
                logger.trace("Leave object {}", name);
                currentNode = (Element) currentNode.getParentNode();
            } else {
                currentNode = (Element) currentNode.getParentNode();
                if (currentNode == null || currentNode.getAttribute(IS_ARRAY).equals("")) {
                    throw new RuntimeException();
                }
                logger.trace("Leave array field");
            }
        }
    }

    public int enterArray(String name) {
        if (name != null) {
            //Get parent node
            currentNode = getChildNode(currentNode, name);
        } else {
            throw new RuntimeException();
        }
        currentNode.setAttribute(IS_ARRAY, IS_ARRAY);
        arrayStack.push(getChildNode(currentNode, currentNode.getNodeName()));

        int n = 0;
        NodeList nodeList = currentNode.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            if (nodeList.item(i).getNodeType() != Node.TEXT_NODE) {
                n++;
            }
        }
        logger.trace("Enter array {}, size={}", name, n);
        return n;
    }

    public void leaveArray(String name) {
        logger.trace("Leave array {}", name);
        if (currentNode.getAttribute(IS_ARRAY).equals("")) {
            throw new RuntimeException();
        }
        currentNode.removeAttribute(IS_ARRAY);
        currentNode = (Element) currentNode.getParentNode();
        if (currentNode == null) {
            throw new RuntimeException();
        }
    }

    public boolean booleanValue(String name) {
        if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
            String value = getChildNode(getFromStack(currentNode), name).getTextContent().trim();
            if ("true".equals(value)) {
                return true;
            } else if ("false".equals(value)) {
                return false;
            } else {
                throw new RuntimeException();
            }
        }
        throw new RuntimeException(String.valueOf(currentNode.getNodeType()));
    }

    public String bitsValue(String name) {
        if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
            return getChildNode(getFromStack(currentNode), name).getTextContent().trim();
        }
        throw new RuntimeException(String.valueOf(currentNode.getNodeType()));
    }

    public String bytesValue(String name) {
        if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
            return getChildNode(getFromStack(currentNode), name).getTextContent().replaceAll("[\\t\\n\\r ]", "");
        }
        throw new RuntimeException(String.valueOf(currentNode.getNodeType()));
    }

    public BigInteger intValue(String name) {
        if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
            return new BigInteger(getChildNode(getFromStack(currentNode), name).getTextContent().trim());
        }
        throw new RuntimeException(String.valueOf(currentNode.getNodeType()));
    }

    public List<String> fieldsValue() {
        List<String> stringList = new ArrayList<String>();
        NodeList nodeList = currentNode.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            if (nodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
                stringList.add(nodeList.item(i).getNodeName());
            }
        }
        return stringList;
    }

    private void print(Node node) {
        Transformer transformer = null;
        try {
            transformer = TransformerFactory.newInstance().newTransformer();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        }
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        //initialize StreamResult with File object to save to file
        StreamResult result = new StreamResult(new StringWriter());
        DOMSource source = new DOMSource(currentNode.getParentNode());
        try {
            transformer.transform(source, result);
        } catch (TransformerException e) {
            e.printStackTrace();
        }
//        String xmlString = result.getWriter().toString();
//        System.out.println(xmlString);
//        System.out.println("--------------------------------------------------------------------------------------------");
    }
    public String stringValue(String name) {
        if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
            return getChildNode(getFromStack(currentNode), name).getTextContent().trim();
        }
        throw new RuntimeException(String.valueOf(currentNode.getNodeType()));
    }

    public String printCurrentnode() {
        return currentNode.getTagName() + " : " + currentNode.getChildNodes().toString();
    }

    private Element getChildNode(Element node, String name) {

        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node current = nodeList.item(i);
            if (current.getNodeName().equals(name)) {
                elementExist = true;
                return (Element) current;
            }
        }
        elementExist = false;
        return node;
    }

    private Element getFromStack(Element node) {
        if (!"".equals(currentNode.getAttribute(IS_ARRAY))) {
            Element node1 = arrayStack.pop();
            Node node2 = node1.getNextSibling();
            while (node2 != null && node2.getNodeType() != Node.ELEMENT_NODE) {
                node2 = node2.getNextSibling();
            }
            if (node2 != null) {
                arrayStack.push((Element) node2);
            }
            return node1;
        }
        return node;
    }

    public Element exist(String name) {

        Element node = getFromStack(currentNode);

        NodeList nodeList = node.getChildNodes();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node current = nodeList.item(i);
            if (current.getNodeName().equals(name)) {
                return (Element) current;
            }
        }
        return null;
    }
    public static void binaryToHex(StringBuilder binaryString, StringBuilder hexaString, Integer length){

        if(binaryString.length()%8 ==0){
            if(length%8 == 0){
                hexaString.append(String.format("%0"+(length/4)+"X", Long.parseLong(binaryString.toString(),2)));
                binaryString.setLength(0);
            }else{
                hexaString.append(String.format("%02X", Long.parseLong(binaryString.toString(),2)));
            }
            binaryString.setLength(0);
        }
    }

    public boolean isElementExist() {
        return elementExist;
    }
}
