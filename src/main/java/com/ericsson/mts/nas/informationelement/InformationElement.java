package com.ericsson.mts.nas.informationelement;

import com.ericsson.mts.nas.BitInputStream;
import com.ericsson.mts.nas.exceptions.DecodingException;
import com.ericsson.mts.nas.exceptions.DictionaryException;
import com.ericsson.mts.nas.exceptions.NotHandledException;
import com.ericsson.mts.nas.informationelement.field.AbstractField;
import com.ericsson.mts.nas.informationelement.field.AbstractTranslatorField;
import com.ericsson.mts.nas.informationelement.field.translator.*;
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
            if(bitInputStream.available() > 0){
                result = abstractField.decode(mainRegistry, bitInputStream, formatWriter);
            }
        }
        formatWriter.leaveObject(name);
        return result;
    }

    @Override
    public void encode(Registry mainRegistry, XMLFormatReader r, StringBuilder binaryString, StringBuilder hexaString) {

        r.enterObject(name);
        for (AbstractField abstractField : pdu) {
                if (abstractField instanceof MessageWrapperField || abstractField instanceof BinaryField || abstractField instanceof ChoiceField || abstractField instanceof HexadecimalField || abstractField instanceof MultipleField) {
                    hexaString.append(abstractField.encode(mainRegistry, r, binaryString));
                } else {
                    binaryString.append(abstractField.encode(mainRegistry, r, binaryString));
                    if(!binaryString.toString().equals("")) {
                        if (binaryString.length() % 8 == 0) {
                            if((((AbstractTranslatorField)abstractField).length)%8 == 0){
                                hexaString.append(String.format("%0"+(((AbstractTranslatorField)abstractField).length/4)+"X", Long.parseLong(binaryString.toString(),2)));
                            }else{
                                hexaString.append(String.format("%02X", Long.parseLong(binaryString.toString(),2)));
                            }
                            binaryString.setLength(0);
                        }
                    }
                }
        }
        r.leaveObject(name);
    }
}
