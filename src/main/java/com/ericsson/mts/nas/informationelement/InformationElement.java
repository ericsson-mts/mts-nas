package com.ericsson.mts.nas.informationelement;

import com.ericsson.mts.nas.BitInputStream;
import com.ericsson.mts.nas.exceptions.DecodingException;
import com.ericsson.mts.nas.exceptions.DictionaryException;
import com.ericsson.mts.nas.exceptions.NotHandledException;
import com.ericsson.mts.nas.informationelement.field.AbstractField;
import com.ericsson.mts.nas.informationelement.field.translator.BinaryField;
import com.ericsson.mts.nas.informationelement.field.translator.HexadecimalField;
import com.ericsson.mts.nas.informationelement.field.wrapper.ChoiceField;
import com.ericsson.mts.nas.informationelement.field.wrapper.MessageWrapperField;
import com.ericsson.mts.nas.reader.XMLFormatReader;
import com.ericsson.mts.nas.registry.Registry;
import com.ericsson.mts.nas.writer.FormatWriter;

import java.io.IOException;


public class InformationElement extends AbstractInformationElement {

    @Override
    public int decode(Registry mainRegistry, BitInputStream bitInputStream, FormatWriter formatWriter) throws IOException, DecodingException, DictionaryException, NotHandledException {
        logger.trace("Enter IE {}", name);
        int result = -1;
        formatWriter.enterObject(name);
        for (AbstractField abstractField : pdu) {
            result = abstractField.decode(mainRegistry, bitInputStream, formatWriter);
        }
        formatWriter.leaveObject(name);
        return result;
    }

    @Override
    public void encode(Registry mainRegistry, XMLFormatReader r, StringBuilder binaryString, StringBuilder hexaString) {

        r.enterObject(name);
        for (AbstractField abstractField : pdu) {
            if (MessageWrapperField.class.isAssignableFrom(abstractField.getClass()) || BinaryField.class.isAssignableFrom(abstractField.getClass()) || ChoiceField.class.isAssignableFrom(abstractField.getClass()) || HexadecimalField.class.isAssignableFrom(abstractField.getClass()))
            {
                hexaString.append(abstractField.encode(mainRegistry, r, binaryString));
            } else {
                binaryString.append(abstractField.encode(mainRegistry, r, binaryString));
                if (binaryString.length() == 8) {
                    int decimal = Integer.parseInt(binaryString.toString(), 2);
                    String hexStr = Integer.toString(decimal, 16);
                    if (hexStr.length() == 1) {
                        hexaString.append("0");
                    }
                    hexaString.append(hexStr);
                    binaryString.setLength(0);
                }
            }

        }
        r.leaveObject(name);
    }
}
