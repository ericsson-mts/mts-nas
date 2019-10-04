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
            String classe = abstractField.getClass().toString();
            if (classe.contains("MessageWrapperField") || classe.contains("BinaryField") || classe.contains("ChoiceField")) {
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
