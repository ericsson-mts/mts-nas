package com.ericsson.mts.nas.message;


import com.ericsson.mts.nas.BitInputStream;
import com.ericsson.mts.nas.exceptions.DecodingException;
import com.ericsson.mts.nas.exceptions.DictionaryException;
import com.ericsson.mts.nas.exceptions.NotHandledException;
import com.ericsson.mts.nas.reader.Reader;
import com.ericsson.mts.nas.reader.XMLFormatReader;
import com.ericsson.mts.nas.registry.Registry;
import com.ericsson.mts.nas.writer.FormatWriter;

import javax.xml.bind.DatatypeConverter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Map;

import static com.ericsson.mts.nas.writer.XMLFormatWriter.bytesToHex;

public class Message extends AbstractMessage {

    public void decode(Registry mainRegistry, BitInputStream s, FormatWriter w) throws IOException, DecodingException, DictionaryException, NotHandledException {

        w.enterObject(this.name);

        if (null != mandatory) {
            for (InformationElementsContainer c : mandatory) {
                decodeInformationElement(mainRegistry,s,w,c);
            }
        }

        while (s.available() > 0 && null != optional) {
            readOptionnal(mainRegistry, s, w);
        }

        if (null != additionnal && s.available() > 0) {
            w.enterObject("PlainNas5GsMessage");
            for (InformationElementsContainer c : additionnal) {
                decodeInformationElement(mainRegistry,s,w,c);
            }
            w.leaveObject("PlainNas5GsMessage");
        }

        w.leaveObject(this.name);
    }

    @Override
    public byte[] encode(Registry mainRegistry, XMLFormatReader r, StringBuilder binaryString) throws DecodingException {

        StringBuilder hexaString = new StringBuilder();

        if(null != mandatory) {
            for (InformationElementsContainer c : mandatory) {
                if (null != mainRegistry.getInformationElement(c.type)) {
                    r.enterObject(c.name);
                    checkLength(r,hexaString, c.nBitLength);
                    mainRegistry.getInformationElement(c.type).encode(mainRegistry, r, binaryString, hexaString);
                    r.leaveObject(c.name);
                }
            }
        }

        if(null != optional){
            for (Map.Entry<String, InformationElementsContainer> entry : optional.entrySet()){
                String name = entry.getValue().name;
                if(null != r.exist(name)){
                    r.enterObject(name);
                    if(entry.getKey().length() < 2){
                        binaryString.append(String.format("%4s", new BigInteger(entry.getKey(),16).toString(2)));
                    } else{
                        hexaString.append(entry.getKey());
                    }
                    checkLength(r,hexaString, entry.getValue().nBitLength);
                    mainRegistry.getInformationElement(entry.getValue().type).encode(mainRegistry,r,binaryString,hexaString);
                    r.leaveObject(name);
                }
            }
        }

        if(null != additionnal && null != r.exist("PlainNas5GsMessage")){
            r.enterObject("PlainNas5GsMessage");
            for (InformationElementsContainer c : additionnal){
                if(null != r.exist(c.name)){
                    r.enterObject(c.name);
                    checkLength(r,hexaString, c.nBitLength);
                    mainRegistry.getInformationElement(c.type).encode(mainRegistry,r,binaryString,hexaString);
                    r.leaveObject(c.name);
                }
            }
            r.leaveObject("PlainNas5GsMessage");
        }

        return DatatypeConverter.parseHexBinary(hexaString.toString());
    }

    @Override
    public byte[] encode(Registry mainRegistry, XMLFormatReader r) throws DecodingException {
        StringBuilder binaryString = new StringBuilder();
        return encode(mainRegistry,r,binaryString);
    }

    private void decodeInformationElement(Registry mainRegistry, BitInputStream s, FormatWriter w, InformationElementsContainer c) throws IOException, DecodingException, DictionaryException, NotHandledException {
        if (null != mainRegistry.getInformationElement(c.type)) {
            w.enterObject(c.name());
            mainRegistry.getInformationElement(c.type).decode(mainRegistry, read(c, s, w), w);
            w.leaveObject(c.name());
        } else {
            logger.error("Impossible de trouver l'élement {}", c.type);
            throw new DecodingException("Can't find element "+ c.type);
        }
    }

    private void checkLength(XMLFormatReader r, StringBuilder hexaString, Integer nbits){
        if(null != r.exist("Length")){
            String len = String.format("%"+nbits/4+"s",Integer.toHexString(r.intValue("Length").intValue())).replace(' ', '0');
            hexaString.append(len);
        }
    }


    private void readOptionnal(Registry mainRegistry, BitInputStream s, FormatWriter w) throws IOException, DecodingException, DictionaryException, NotHandledException {
        String iei = Integer.toHexString(s.readBits(4)).toUpperCase();
        if(optional.containsKey(iei)){
            logger.trace("decode IEI : 0x{}",iei);
            w.enterObject(optional.get(iei).name());
            w.stringValue("IEI", iei);
            mainRegistry.getInformationElement(optional.get(iei).type).decode(mainRegistry, read(optional.get(iei), s,w), w);
            w.leaveObject(optional.get(iei).name());
        } else {
            iei += Integer.toHexString(s.readBits(4)).toUpperCase();
            if (optional.containsKey(iei)) {
                logger.trace("decode IEI : 0x{}",iei);
                w.enterObject(optional.get(iei).name());
                w.stringValue("IEI", iei);
                mainRegistry.getInformationElement(optional.get(iei).type).decode(mainRegistry, read(optional.get(iei), s,w), w);
                w.leaveObject(optional.get(iei).name());
            } else {
                logger.error("Unknown IEI " + iei);
                throw new DecodingException("Unknown IEI " + iei);
            }
        }
    }

    private BitInputStream read(InformationElementsContainer c, BitInputStream s, FormatWriter w) throws IOException {
        byte[] buffer;

        if(null == c.length || -1 != c.length){
            buffer = Reader.readByte(c.length, c.nBitLength, s, logger,w);
        }else{
            return s;
        }
        logger.trace("return buffer 0x{}", bytesToHex(buffer));
        return new BitInputStream(new ByteArrayInputStream(buffer));
    }
}
