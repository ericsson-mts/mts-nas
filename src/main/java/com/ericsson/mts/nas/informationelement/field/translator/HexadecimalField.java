package com.ericsson.mts.nas.informationelement.field.translator;

import com.ericsson.mts.nas.BitInputStream;
import com.ericsson.mts.nas.informationelement.field.AbstractTranslatorField;
import com.ericsson.mts.nas.reader.Reader;
import com.ericsson.mts.nas.reader.XMLFormatReader;
import com.ericsson.mts.nas.registry.Registry;
import com.ericsson.mts.nas.writer.FormatWriter;

import java.io.IOException;
import java.math.BigInteger;

public class HexadecimalField extends AbstractTranslatorField {

    @Override
    public int decode(Registry mainRegistry,  BitInputStream s, FormatWriter formatWriter) throws IOException {
        logger.trace("Enter field {} with length {}", name, length);

        if(null == length){
            length = s.bigReadBits(nBitLength).intValueExact();
        }

        formatWriter.intValue("Length", BigInteger.valueOf(length));
        formatWriter.bytesValue(name, Reader.readByte(length, nBitLength,s,logger, formatWriter));
        return 0;
    }

    @Override
    public  String encode(Registry mainRegistry, XMLFormatReader r, StringBuilder binaryString) {
        return Reader.encodeHexaAndBinary(length,name,r);
    }
}
