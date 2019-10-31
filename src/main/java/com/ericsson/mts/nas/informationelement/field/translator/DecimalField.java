package com.ericsson.mts.nas.informationelement.field.translator;

import com.ericsson.mts.nas.BitInputStream;
import com.ericsson.mts.nas.exceptions.DecodingException;
import com.ericsson.mts.nas.informationelement.field.AbstractTranslatorField;
import com.ericsson.mts.nas.reader.XMLFormatReader;
import com.ericsson.mts.nas.registry.Registry;
import com.ericsson.mts.nas.writer.FormatWriter;

import java.io.IOException;
import java.math.BigInteger;

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
                String value = namedValueMap.get(result);
                if (!value.isEmpty()) {
                    logger.trace("{}  result : {} (0x{})", name, value, String.format("%x", result));
                    formatWriter.stringValue(name, value);
                    return result;
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

        if (r.exist(name) != null) {
            String value = r.stringValue(name);

            Integer key = namedValueMap.inverse().get(value);
            if (null != key) {
                logger.trace("key : {} to byte value {}", key, key.byteValue());
                return String.format("%" + length + "s", Integer.toBinaryString(key.byteValue() & 0xFF)).replace(' ', '0');
            }

            throw new DecodingException("Can't find key for the value " + value);
        }
        return "";
    }
}
