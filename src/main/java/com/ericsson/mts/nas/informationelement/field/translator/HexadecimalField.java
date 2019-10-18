package com.ericsson.mts.nas.informationelement.field.translator;

import com.ericsson.mts.nas.BitInputStream;
import com.ericsson.mts.nas.exceptions.DecodingException;
import com.ericsson.mts.nas.informationelement.field.AbstractTranslatorField;
import com.ericsson.mts.nas.reader.Reader;
import com.ericsson.mts.nas.reader.XMLFormatReader;
import com.ericsson.mts.nas.registry.Registry;
import com.ericsson.mts.nas.writer.FormatWriter;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;

import static com.ericsson.mts.nas.writer.XMLFormatWriter.bytesToHex;

public class HexadecimalField extends AbstractTranslatorField {

    @Override
    public int decode(Registry mainRegistry,  BitInputStream s, FormatWriter formatWriter) throws IOException, DecodingException {
        logger.trace("Enter field {} with length {}", name, length);
        byte[] buffer;
        int len;
        if(null == length){
            len = s.bigReadBits(8).intValueExact();
        } else {
            len = this.length;
        }
        formatWriter.intValue("Length", BigInteger.valueOf(len));

        buffer = Reader.readByte(len,s,logger);

        formatWriter.bytesValue(name, buffer);
        logger.trace("return buffer 0x{}", bytesToHex(buffer));
        return 0;
    }

    @Override
    public  String encode(Registry mainRegistry, XMLFormatReader r, StringBuilder binaryString) {
        return Reader.encodeHexaAndBinary(length,name,r);
    }
}
