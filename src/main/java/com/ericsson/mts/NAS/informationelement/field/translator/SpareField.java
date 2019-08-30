package com.ericsson.mts.NAS.informationelement.field.translator;

import com.ericsson.mts.NAS.BitInputStream;
import com.ericsson.mts.NAS.exceptions.DecodingException;
import com.ericsson.mts.NAS.informationelement.field.AbstractField;
import com.ericsson.mts.NAS.registry.Registry;
import com.ericsson.mts.NAS.writer.FormatWriter;

import java.io.IOException;

public class SpareField extends AbstractField {
    private int value;
    private Integer length;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
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
}
