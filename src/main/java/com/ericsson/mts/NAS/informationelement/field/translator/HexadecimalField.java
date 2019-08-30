package com.ericsson.mts.NAS.informationelement.field.translator;

import com.ericsson.mts.NAS.BitInputStream;
import com.ericsson.mts.NAS.exceptions.DecodingException;
import com.ericsson.mts.NAS.informationelement.field.AbstractTranslatorField;
import com.ericsson.mts.NAS.registry.Registry;
import com.ericsson.mts.NAS.writer.FormatWriter;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;

public class HexadecimalField extends AbstractTranslatorField {

    @Override
    public int decode(Registry mainRegistry,  BitInputStream bitInputStream, FormatWriter formatWriter) throws IOException, DecodingException {
        logger.trace("Enter field {} with length {}", name, length);
        if(length > 0){
            int result = bitInputStream.bigReadBits(length).intValueExact();
            if(namedValue != null){
                for(Integer value : namedValue.keySet()){
                    if(value == result){
                        logger.trace("{}  result : {} (0x{})", name, namedValue.get(value), String.format("%x", value));
                        formatWriter.stringValue(name, namedValue.get(value));
                        return result;
                    }
                }
            }
            logger.trace("{}  result : (0x{})", name, String.format("%x", result));
            formatWriter.intValue(name, BigInteger.valueOf(result));
            return result;
        } else {
            throw new RuntimeException("TODO");
        }
    }
}
