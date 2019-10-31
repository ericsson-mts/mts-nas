package com.ericsson.mts.nas.informationelement.field.translator;

import com.ericsson.mts.nas.BitInputStream;
import com.ericsson.mts.nas.exceptions.DecodingException;
import com.ericsson.mts.nas.informationelement.field.AbstractField;
import com.ericsson.mts.nas.informationelement.field.AbstractTranslatorField;
import com.ericsson.mts.nas.reader.XMLFormatReader;
import com.ericsson.mts.nas.registry.Registry;
import com.ericsson.mts.nas.writer.FormatWriter;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

public class DecimalField extends AbstractTranslatorField {

    @Override
    public int decode(Registry mainRegistry, BitInputStream bitInputStream, FormatWriter formatWriter) throws IOException {
        logger.trace("Enter field {} with length {}", name, length);

        if(null == length){
            length = (bitInputStream.bigReadBits(8).intValueExact());
            logger.trace("Calcul length {}", length);
        }

        if (length > 0) {
            int result = bitInputStream.bigReadBits(length).intValueExact();
            if (namedValueMap != null) {
                for (Integer value : namedValueMap.keySet()) {
                    if (value == result) {
                        logger.trace("{}  result : {} (0x{})", name, namedValueMap.get(value), String.format("%x", value));
                        formatWriter.stringValue(name, namedValueMap.get(value));
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

    @Override
    public String encode(Registry mainRegistry, XMLFormatReader r, StringBuilder binaryString) throws DecodingException {

        if(r.exist(name) != null) {
            String value = r.stringValue(name);

            for (Integer key : namedValueMap.keySet()) {
                if (value.equals(namedValueMap.get(key))) {
                    logger.trace("key : {} to byte value {}", key, key.byteValue());
                    return String.format("%" + length + "s", Integer.toBinaryString(key.byteValue() & 0xFF)).replace(' ', '0');
                }
            }
        throw new DecodingException("Can't find key for the value "+ value);
        }
        return "";
    }
}
