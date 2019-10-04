package com.ericsson.mts.NAS.informationelement.field.translator;

import com.ericsson.mts.NAS.BitInputStream;
import com.ericsson.mts.NAS.exceptions.DecodingException;
import com.ericsson.mts.NAS.informationelement.field.AbstractTranslatorField;
import com.ericsson.mts.NAS.reader.XMLFormatReader;
import com.ericsson.mts.NAS.registry.Registry;
import com.ericsson.mts.NAS.writer.FormatWriter;

import java.io.IOException;
import java.math.BigInteger;

import static com.ericsson.mts.NAS.writer.XMLFormatWriter.bytesToHex;

public class BinaryField extends AbstractTranslatorField {

    @Override
    public int decode(Registry mainRegistry,  BitInputStream s, FormatWriter formatWriter) throws IOException, DecodingException {
        logger.trace("Enter field {} with length {}", name, length);
        byte[] buffer;
        int len;
        if(null == length){
            len = s.bigReadBits(8).intValueExact() *8;
            formatWriter.intValue("Length", BigInteger.valueOf(len/8));
        } else {
            len = this.length;
        }

//        formatWriter.intValue("Length", BigInteger.valueOf(len/8));
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

        formatWriter.bytesValue(name, buffer);
        logger.trace("return buffer 0x{}", bytesToHex(buffer));
        return 0;
    }

    @Override
    public String encode(Registry mainRegistry, XMLFormatReader r, StringBuilder binaryString) {

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
}
