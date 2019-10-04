package com.ericsson.mts.NAS.informationelement.field.wrapper;

import com.ericsson.mts.NAS.BitInputStream;
import com.ericsson.mts.NAS.exceptions.DecodingException;
import com.ericsson.mts.NAS.exceptions.DictionaryException;
import com.ericsson.mts.NAS.exceptions.NotHandledException;
import com.ericsson.mts.NAS.informationelement.field.AbstractTranslatorField;
import com.ericsson.mts.NAS.reader.XMLFormatReader;
import com.ericsson.mts.NAS.registry.Registry;
import com.ericsson.mts.NAS.writer.FormatWriter;

import java.io.IOException;
import java.util.List;

public class MessageWrapperField extends AbstractTranslatorField {


    @Override
    public int decode(Registry mainRegistry, BitInputStream bitInputStream, FormatWriter formatWriter) throws IOException, DecodingException, DictionaryException, NotHandledException {
        logger.trace("Enter field {}", name);
        int result = bitInputStream.readBits(length);
        for(Integer key : namedValue.keySet()){
            if(key == result){
                logger.trace("Decode message {} (0x{})", namedValue.get(key), String.format("%x", key));
                formatWriter.stringValue(name, namedValue.get(key));
                mainRegistry.getMessage(namedValue.get(key)).decode(mainRegistry, bitInputStream, formatWriter);
                return result;
            }
        }
        throw new DecodingException(name + " can't decode " + result);
    }

    @Override
    public String encode(Registry mainRegistry, XMLFormatReader r, StringBuilder binaryString) {

        StringBuilder hexaField = new StringBuilder();
        byte[] byteArray = new byte[0];

        r.enterObject(this.name);
        String value = r.stringValue(this.name);
        r.leaveObject(this.name);

        for (Integer key : namedValue.keySet()) {
            if (value.equals(namedValue.get(key))) {
                logger.trace("Encode message {} (0x{})", namedValue.get(key), String.format("%x", key));
                hexaField.append(Integer.toHexString(key));
                r.enterObject(namedValue.get(key));
                byteArray = mainRegistry.getMessage(namedValue.get(key)).encode(mainRegistry, r);
                r.leaveObject(namedValue.get(key));
            }
        }
        hexaField.append(bytesToHex(byteArray));
        return hexaField.toString();
    }

    private static String bytesToHex(byte[] bytes) {
        char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
}
