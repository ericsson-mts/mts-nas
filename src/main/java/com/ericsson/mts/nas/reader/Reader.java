package com.ericsson.mts.nas.reader;

import com.ericsson.mts.nas.BitInputStream;
import org.slf4j.Logger;

import java.io.IOException;

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

    public static byte[] readByte(int len, BitInputStream s, Logger logger) throws IOException {
        byte[] buffer;

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
        return buffer;
    }
}
