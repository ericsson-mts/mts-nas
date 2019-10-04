package com.ericsson.mts.NAS.informationelement;

import com.ericsson.mts.NAS.BitInputStream;
import com.ericsson.mts.NAS.exceptions.DecodingException;
import com.ericsson.mts.NAS.exceptions.DictionaryException;
import com.ericsson.mts.NAS.exceptions.NotHandledException;
import com.ericsson.mts.NAS.informationelement.field.AbstractField;
import com.ericsson.mts.NAS.reader.XMLFormatReader;
import com.ericsson.mts.NAS.registry.Registry;
import com.ericsson.mts.NAS.writer.FormatWriter;

import java.io.IOException;

public class VariableLengthIE extends AbstractInformationElement {

    @Override
    public int decode(Registry mainRegistry, BitInputStream bitInputStream, FormatWriter formatWriter) throws IOException, DecodingException, DictionaryException, NotHandledException {
        int usedLength = 0;
        logger.trace("Enter IE {}", name);
        formatWriter.enterObject(name);
        int result = -1;
        for (AbstractField abstractField : pdu) {
            if (bitInputStream.available() > 0) {
                result = abstractField.decode(mainRegistry, bitInputStream, formatWriter);
            } else {
                break;
            }
        }
        formatWriter.leaveObject(name);
        return result;
    }

    @Override
    public void encode(Registry mainRegistry, XMLFormatReader r, StringBuilder binaryString,StringBuilder hexaString) {

    }
}
