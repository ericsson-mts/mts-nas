package com.ericsson.mts.NAS.informationelement.field.translator;

import com.ericsson.mts.NAS.BitInputStream;
import com.ericsson.mts.NAS.exceptions.DecodingException;
import com.ericsson.mts.NAS.informationelement.field.AbstractField;
import com.ericsson.mts.NAS.registry.Registry;
import com.ericsson.mts.NAS.writer.FormatWriter;

import java.io.IOException;

public class LengthField extends AbstractField {
    private int length;

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    @Override
    public int decode(Registry mainRegistry, BitInputStream bitInputStream, FormatWriter formatWriter) throws IOException, DecodingException {
        logger.trace("Enter length field {}", name);
        int result = 0;
        if ("LengthOfEsmMessageContainerContents".equals(name)) {
            result = bitInputStream.readBits(length);
            logger.debug("Resultat : " + result);
//            for (int i = 0; i < 4; i++) {
//                result = bitInputStream.readBits(4);
//                logger.debug("Resultat : " + result);
//            }
        } else {
            result = bitInputStream.readBits(length);
        }
        logger.trace("Decode : {}", result);
        return result;
    }
}
