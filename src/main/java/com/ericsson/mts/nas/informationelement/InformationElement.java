package com.ericsson.mts.nas.informationelement;

import com.ericsson.mts.nas.BitInputStream;
import com.ericsson.mts.nas.exceptions.DecodingException;
import com.ericsson.mts.nas.exceptions.DictionaryException;
import com.ericsson.mts.nas.exceptions.NotHandledException;
import com.ericsson.mts.nas.informationelement.field.AbstractField;
import com.ericsson.mts.nas.reader.XMLFormatReader;
import com.ericsson.mts.nas.registry.Registry;
import com.ericsson.mts.nas.writer.FormatWriter;

import java.io.IOException;

import static com.ericsson.mts.nas.reader.Reader.encodeFields;

public class InformationElement extends AbstractInformationElement {

    @Override
    public int decode(Registry mainRegistry, BitInputStream bitInputStream, FormatWriter formatWriter) throws IOException, DecodingException, DictionaryException, NotHandledException {
        logger.trace("Enter IE {}", name);
        int result = -1;
        formatWriter.enterObject(name);
        for (AbstractField abstractField : pdu) {
            if(bitInputStream.available() > 0){
                result = abstractField.decode(mainRegistry, bitInputStream, formatWriter);
            }
        }
        formatWriter.leaveObject(name);
        return result;
    }

    @Override
    public void encode(Registry mainRegistry, XMLFormatReader r, StringBuilder binaryString, StringBuilder hexaString) throws DecodingException {

        r.enterObject(name);
        encodeFields(pdu,mainRegistry,r,binaryString,hexaString);
        r.leaveObject(name);
    }
}
