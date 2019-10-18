package com.ericsson.mts.nas.informationelement.field.translator;

import com.ericsson.mts.nas.BitInputStream;
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
            if (namedValue != null) {
                for (Integer value : namedValue.keySet()) {
                    if (value == result) {
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

    @Override
    public String encode(Registry mainRegistry, XMLFormatReader r, StringBuilder binaryString) {

        String value;

        if(name.equals("Iei") || name.equals("Digit")){
            BigInteger iei = r.intValue(name);
            return String.format("%"+length+"s", Integer.toBinaryString(iei.byteValue() & 0xFF)).replace(' ', '0');
        } else if(name.equals("Length")){
            value = r.stringValue(name);
            logger.trace("length to byte value {}",value);
            return String.format("%"+length+"s", Integer.toBinaryString(Integer.valueOf(value).byteValue() & 0xFF)).replace(' ', '0');
        }else{
            value = r.stringValue(name);
        }

        for (Integer key : namedValue.keySet()) {
            if (value.equals(namedValue.get(key))){
                logger.trace("key : {} to byte value {}",key,key.byteValue());
                return String.format("%"+length+"s", Integer.toBinaryString(key.byteValue() & 0xFF)).replace(' ', '0');
            }
        }
        return "";
    }
}
