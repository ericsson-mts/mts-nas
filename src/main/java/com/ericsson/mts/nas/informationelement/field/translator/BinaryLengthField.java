package com.ericsson.mts.nas.informationelement.field.translator;

import com.ericsson.mts.nas.BitInputStream;
import com.ericsson.mts.nas.exceptions.DecodingException;
import com.ericsson.mts.nas.exceptions.DictionaryException;
import com.ericsson.mts.nas.exceptions.NotHandledException;
import com.ericsson.mts.nas.informationelement.field.AbstractField;
import com.ericsson.mts.nas.informationelement.field.AbstractTranslatorField;
import com.ericsson.mts.nas.informationelement.field.wrapper.ChoiceField;
import com.ericsson.mts.nas.informationelement.field.wrapper.MessageWrapperField;
import com.ericsson.mts.nas.reader.Reader;
import com.ericsson.mts.nas.reader.XMLFormatReader;
import com.ericsson.mts.nas.registry.Registry;
import com.ericsson.mts.nas.writer.FormatWriter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import static com.ericsson.mts.nas.writer.XMLFormatWriter.bytesToHex;

public class BinaryLengthField extends AbstractTranslatorField {



    @Override
    public int decode(Registry mainRegistry, BitInputStream bitInputStream, FormatWriter formatWriter) throws IOException, DecodingException, DictionaryException, NotHandledException {

        formatWriter.enterObject(name);
        int result = bitInputStream.readBits(((DigitsField)pdu.get(0)).length);
        formatWriter.intValue("Length", BigInteger.valueOf(result));

        BitInputStream buffer = read(result,bitInputStream);

        for (AbstractField abstractField : pdu.subList( 1, pdu.size())) {
            abstractField.decode(mainRegistry, buffer, formatWriter);
        }
        formatWriter.leaveObject(name);
        return 0;
    }

    @Override
    public String encode(Registry mainRegistry, XMLFormatReader r, StringBuilder binaryString) {

        StringBuilder hexaString = new StringBuilder();

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
        return hexaString.toString();
    }

    private BitInputStream read(int len, BitInputStream s) throws IOException {

        len = len*8;
        byte[] buffer = Reader.readByte(len,s,logger);

        logger.trace("return buffer 0x{}", bytesToHex(buffer));
        return new BitInputStream(new ByteArrayInputStream(buffer));
    }
}
