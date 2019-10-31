package com.ericsson.mts.nas.informationelement.field.translator;

import com.ericsson.mts.nas.BitInputStream;
import com.ericsson.mts.nas.exceptions.DecodingException;
import com.ericsson.mts.nas.informationelement.field.AbstractTranslatorField;
import com.ericsson.mts.nas.reader.XMLFormatReader;
import com.ericsson.mts.nas.registry.Registry;
import com.ericsson.mts.nas.writer.FormatWriter;

import java.io.IOException;

public class SpareField extends AbstractTranslatorField {
    private int value;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public int decode(Registry mainRegistry, BitInputStream bitInputStream, FormatWriter formatWriter) throws IOException, DecodingException {
        logger.trace("Enter spare field with value " + value);
        if(length != null){
            int result = bitInputStream.readBits(length);
            if(result != value){
                throw new DecodingException("Found " + result + " instead of " + value);
            }
        } else {
            throw new RuntimeException("TODO");
        }
        return 0;
    }

    @Override
    public String encode(Registry mainRegistry, XMLFormatReader r, StringBuilder binaryString) {

        String res = Integer.toHexString(value);
        logger.trace("Spare value : {} to hex value 0x{}", this.value, res);
        return String.format("%"+length+"s", Integer.toBinaryString(((Integer)value).byteValue() & 0xFF)).replace(' ', '0');

    }
}
