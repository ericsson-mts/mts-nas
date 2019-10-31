/*
 * Copyright 2019 Ericsson, https://www.ericsson.com/en
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.ericsson.mts.nas.writer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Generate XML
 */

public class XMLFormatWriter implements FormatWriter {
    private Document document;
    private Element currentNode;

    public XMLFormatWriter() throws ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        dbf.setXIncludeAware(false);
        dbf.setExpandEntityReferences(false);
        document = dbf.newDocumentBuilder().newDocument();
        currentNode = null;
    }

    /**
     * Create a new XML Element
     * @param name name of the Element
     * @param isValueElement
     * @return XML Element
     */
    private Element createNewElement(String name, boolean isValueElement) {
        Element newElement;
        if (null == name) {
            newElement = document.createElement(currentNode.getTagName());
        } else {
            newElement = document.createElement(name);
        }

        if (null == currentNode) {
            document.appendChild(newElement);
        } else {
            currentNode.appendChild(newElement);
        }

        if (!isValueElement) {
            currentNode = newElement;
        }

        return newElement;
    }

    public void enterObject(String name) {
        createNewElement(name, false);
    }

    public void leaveObject(String name) {
        Node parent = currentNode.getParentNode();
        if (parent instanceof Element) {
            currentNode = (Element) parent;
        } else {
            currentNode = null;
        }
    }

    public void enterArray(String name) {
        createNewElement(name, false);
    }

    public void leaveArray(String name) {
        Node parent = currentNode.getParentNode();
        if (parent instanceof Element) {
            currentNode = (Element) parent;
        } else {
            currentNode = null;
        }
    }

    public void stringValue(String name, String value) {
        createNewElement(name, true).setTextContent(value);
    }

    public void booleanValue(String name, boolean value) {
        createNewElement(name, true).setTextContent(Boolean.toString(value));
    }

    public void intValue(String name, BigInteger value) {
        createNewElement(name, true).setTextContent(value.toString());
    }

    public void realValue(String name, BigDecimal value) {
        createNewElement(name, true).setTextContent(value.toString());
    }

    public void bytesValue(String name, byte[] value) {
        createNewElement(name, true).setTextContent(bytesToHex(value));
    }

    public void bitsValue(String name, String value) {
        createNewElement(name, true).setTextContent(value);
    }

    public void nullValue(String name) {
        createNewElement(name, true).setAttribute("isNull", "true");
    }

    /**
     * Return result
     * @return String containing XML
     * @throws TransformerException When it is not
     * possible to create a <code>Transformer</code> instance or when errors appear during transformation
     */
    public String getResult() throws TransformerException {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(document.getDocumentElement()), new StreamResult(writer));
        return writer.toString();
    }

    public Element getResultElement(){
        return document.getDocumentElement();
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
}
