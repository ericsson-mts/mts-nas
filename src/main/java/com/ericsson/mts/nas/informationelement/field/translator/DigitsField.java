package com.ericsson.mts.nas.informationelement.field.translator;

import com.ericsson.mts.nas.BitInputStream;
import com.ericsson.mts.nas.exceptions.DecodingException;
import com.ericsson.mts.nas.informationelement.field.AbstractTranslatorField;
import com.ericsson.mts.nas.reader.XMLFormatReader;
import com.ericsson.mts.nas.registry.Registry;
import com.ericsson.mts.nas.writer.FormatWriter;
import com.ericsson.mts.nas.writer.XMLFormatWriter;

import javax.xml.bind.DatatypeConverter;
import javax.xml.transform.TransformerException;
import java.io.IOException;

public class DigitsField extends AbstractTranslatorField {

    @Override
    public int decode(Registry mainRegistry, BitInputStream bitInputStream, FormatWriter formatWriter) throws IOException, DecodingException {
        logger.trace("Enter field {} with length {}", name, length);
        StringBuilder string = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            string.append((byte) bitInputStream.readBits(4));
        }
        logger.trace("Result : " + string);
        return 0;
    }

    @Override
    public String encode(Registry mainRegistry, XMLFormatReader r, StringBuilder binaryString) {
        return "";
    }
}
