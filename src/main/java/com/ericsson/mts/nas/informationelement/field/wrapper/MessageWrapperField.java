package com.ericsson.mts.nas.informationelement.field.wrapper;

import com.ericsson.mts.nas.BitInputStream;
import com.ericsson.mts.nas.exceptions.DecodingException;
import com.ericsson.mts.nas.exceptions.DictionaryException;
import com.ericsson.mts.nas.exceptions.NotHandledException;
import com.ericsson.mts.nas.informationelement.field.AbstractTranslatorField;
import com.ericsson.mts.nas.reader.XMLFormatReader;
import com.ericsson.mts.nas.registry.Registry;
import com.ericsson.mts.nas.writer.FormatWriter;

import java.io.IOException;

import static com.ericsson.mts.nas.reader.XMLFormatReader.binaryToHex;
import static com.ericsson.mts.nas.writer.XMLFormatWriter.bytesToHex;

public class MessageWrapperField extends AbstractTranslatorField {


    @Override
    public int decode(Registry mainRegistry, BitInputStream bitInputStream, FormatWriter formatWriter) throws IOException, DecodingException, DictionaryException, NotHandledException {
        logger.trace("Enter field {}", name);
        int result = bitInputStream.readBits(length);
        if(namedValueMap.containsKey(result)) {
            String message = namedValueMap.get(result);
            logger.trace("Decode message {} (0x{})", message, String.format("%x", result));
            formatWriter.stringValue(name, message);
            if (null != mainRegistry.getMessage(message)) {
                mainRegistry.getMessage(message).decode(mainRegistry, bitInputStream, formatWriter);
            }
            return result;
        }
        throw new DecodingException(name + " can't decode " + result);
    }

    @Override
    public String encode(Registry mainRegistry, XMLFormatReader r, StringBuilder binaryString) throws DecodingException {

        StringBuilder hexaField = new StringBuilder();
        byte[] byteArray;

        r.enterObject(this.name);
        String value = r.stringValue(this.name);
        r.leaveObject(this.name);

        Integer key = namedValueMap.inverse().get(value);
        if(null !=  key) {
            String element = namedValueMap.get(key);
            logger.trace("Encode message {} (0x{})", element, String.format("%x", key));
            if (this.length < 8) {
                binaryString.append(String.format("%" + length + "s", Integer.toBinaryString(key.byteValue() & 0xFF)).replace(' ', '0'));
                binaryToHex(binaryString, hexaField, length);
            } else {
                hexaField.append(Integer.toHexString(key));
            }
            if (null != mainRegistry.getMessage(element)) {
                r.enterObject(element);
                byteArray = mainRegistry.getMessage(element).encode(mainRegistry, r, binaryString);
                r.leaveObject(element);

                hexaField.append(bytesToHex(byteArray));
            }
            return hexaField.toString();
        }

        throw new DecodingException("Unknow key for value : "+ value);
    }
}
