package com.ericsson.mts.NAS.informationelement.field.translator;

import com.ericsson.mts.NAS.BitInputStream;
import com.ericsson.mts.NAS.exceptions.DecodingException;
import com.ericsson.mts.NAS.informationelement.field.AbstractTranslatorField;
import com.ericsson.mts.NAS.registry.Registry;
import com.ericsson.mts.NAS.writer.FormatWriter;
import com.ericsson.mts.NAS.writer.XMLFormatWriter;

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
}
