package com.ericsson.mts.nas.reader;

import com.ericsson.mts.nas.BitInputStream;
import com.ericsson.mts.nas.exceptions.DecodingException;
import com.ericsson.mts.nas.informationelement.field.AbstractField;
import com.ericsson.mts.nas.informationelement.field.AbstractTranslatorField;
import com.ericsson.mts.nas.informationelement.field.translator.DecimalField;
import com.ericsson.mts.nas.informationelement.field.translator.DigitsField;
import com.ericsson.mts.nas.informationelement.field.translator.SpareField;
import com.ericsson.mts.nas.registry.Registry;
import com.ericsson.mts.nas.writer.FormatWriter;
import org.slf4j.Logger;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import static com.ericsson.mts.nas.reader.XMLFormatReader.binaryToHex;
import static com.ericsson.mts.nas.writer.XMLFormatWriter.bytesToHex;

public class Reader {

    public static String encodeHexaAndBinary(Integer length, String name, XMLFormatReader r){
        StringBuilder res = new StringBuilder();
        if(null == length){
            if(null != r.exist("Length")){
                String len = Integer.toHexString(r.intValue("Length").intValue());
                if(len.length() == 1){
                    res.append("0");
                }
                res.append(len);
            }
        }
        return res.append(r.bytesValue(name)).toString();
    }

    public static byte[] readByte(Integer length, Integer nBitLength , BitInputStream s, Logger logger, FormatWriter w) throws IOException {
        byte[] buffer;

        int len;

        if (null != length && -1 != length) {
            len = length;
        } else if (null == length) {
            len = s.bigReadBits(nBitLength).intValueExact() *8;
            w.intValue("Length", BigInteger.valueOf(len/8));
        } else {
            len = s.available();
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
        return buffer;
    }

    public static void encodeFields(List<AbstractField> pdu, Registry mainRegistry, XMLFormatReader r, StringBuilder binaryString, StringBuilder hexaString) throws DecodingException {
        try {
            for (AbstractField abstractField : pdu) {
                if (abstractField instanceof DecimalField || abstractField instanceof DigitsField || abstractField instanceof SpareField) {
                    binaryString.append(abstractField.encode(mainRegistry, r, binaryString));
                    if (!binaryString.toString().equals("") && binaryString.length() % 8 == 0) {
                        binaryToHex(binaryString, hexaString, ((AbstractTranslatorField) abstractField).length);
                    }
                } else {
                    hexaString.append(abstractField.encode(mainRegistry, r, binaryString));
                }
            }
        }catch (DecodingException e){
            throw new DecodingException(e.toString());
        }
    }
}
