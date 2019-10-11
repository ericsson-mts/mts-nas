package com.ericsson.mts.nas.message;


import com.ericsson.mts.nas.BitInputStream;
import com.ericsson.mts.nas.exceptions.DecodingException;
import com.ericsson.mts.nas.exceptions.DictionaryException;
import com.ericsson.mts.nas.exceptions.NotHandledException;
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
            for (InformationElementsContainer c : additionnal) {
                decodeInformationElement(mainRegistry,s,w,c);
            }
        }

        w.leaveObject(this.name);
    }

    @Override
    public byte[] encode(Registry mainRegistry, XMLFormatReader r, StringBuilder binaryString) {

        StringBuilder hexaString = new StringBuilder();

        if(null != mandatory) {
            for (InformationElementsContainer c : mandatory) {
                if (null != mainRegistry.getInformationElement(c.type)) {
                    r.enterObject(c.name);
                    checkLength(r,hexaString);
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
                    hexaString.append(entry.getKey());
                    checkLength(r,hexaString);
                    mainRegistry.getInformationElement(entry.getValue().type).encode(mainRegistry,r,binaryString,hexaString);
                    r.leaveObject(name);
                }
            }
        }

        return DatatypeConverter.parseHexBinary(hexaString.toString());
    }

    @Override
    public byte[] encode(Registry mainRegistry, XMLFormatReader r){
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
        }
    }

    private void checkLength(XMLFormatReader r, StringBuilder hexaString){
        if(null != r.exist("Length")){
            String len = Integer.toHexString(r.intValue("Length").intValue());
            if(len.length() == 1){
                hexaString.append("0");
            }
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
                throw new RuntimeException("Unknown IEI " + iei);
            }
        }
    }

    private BitInputStream read(InformationElementsContainer c, BitInputStream s, FormatWriter w) throws IOException {
        byte[] buffer;
        int len;
        if (null != c.length && -1 != c.length) {
            len = c.length;
        } else if (null == c.length) {
            len = s.bigReadBits(8).intValueExact() *8;
            w.intValue("Length", BigInteger.valueOf(len/8));
        } else {
            return s;
        }
        logger.trace("reading {} bits", len);
        buffer = new byte[len / 8 + ((len % 8) > 0 ? 1 : 0)];
        int offset = 7;
        int index = 0;
        while (len > 0) {
            byte bitValue = (byte) s.readBit();

            buffer[index] = (byte) (buffer[index] | (bitValue << offset));
            offset--;
            if (-1 == offset) {
                index++;
                offset = 7;
            }
            len--;
        }
        logger.trace("return buffer 0x{}", bytesToHex(buffer));
        return new BitInputStream(new ByteArrayInputStream(buffer));
    }
}
