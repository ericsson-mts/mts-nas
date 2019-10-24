package com.ericsson.mts.nas.informationelement.field.translator;

import com.ericsson.mts.nas.BitInputStream;
import com.ericsson.mts.nas.exceptions.DecodingException;
import com.ericsson.mts.nas.exceptions.DictionaryException;
import com.ericsson.mts.nas.exceptions.NotHandledException;
import com.ericsson.mts.nas.informationelement.field.AbstractField;
import com.ericsson.mts.nas.informationelement.field.AbstractTranslatorField;
import com.ericsson.mts.nas.informationelement.field.wrapper.ChoiceField;
import com.ericsson.mts.nas.informationelement.field.wrapper.MessageWrapperField;
import com.ericsson.mts.nas.message.InformationElementsContainer;
import com.ericsson.mts.nas.reader.Reader;
import com.ericsson.mts.nas.reader.XMLFormatReader;
import com.ericsson.mts.nas.registry.Registry;
import com.ericsson.mts.nas.writer.FormatWriter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import static com.ericsson.mts.nas.writer.XMLFormatWriter.bytesToHex;

public class MultipleField extends AbstractField {

    public String contentLength;
    public Integer nBit = 8;
    public List<AbstractField> pdu;


    @Override
    public int decode(Registry mainRegistry, BitInputStream bitInputStream, FormatWriter formatWriter) throws IOException, DecodingException, DictionaryException, NotHandledException {

        if (contentLength.toLowerCase().contains("number")) {
            int result = bitInputStream.readBits(((DigitsField)pdu.get(0)).length);
            formatWriter.intValue(pdu.get(0).getName(), BigInteger.valueOf(result));
            logger.trace("Number element " + result);

            for (int i = 0; i < result; i++) {
                logger.trace("Enter in field " + name + (i+1));
                formatWriter.enterObject(name + (i+1));
                for (AbstractField abstractField : pdu.subList(1, pdu.size())) {
                    abstractField.decode(mainRegistry, bitInputStream, formatWriter);
                }
                formatWriter.leaveObject(name + i+1);
            }
        }

        if(contentLength.toLowerCase().contains("length")){
            int i = 0;
            int result = bitInputStream.bigReadBits(nBit).intValueExact();
            formatWriter.intValue("LengthMultipleField", BigInteger.valueOf(result));

            BitInputStream buffer = read(result,bitInputStream);

            while(buffer.available() > 0){
                logger.trace("\n\nEnter in field " + name + (i+1));
                formatWriter.enterObject(name + (i+1));
                for (AbstractField abstractField : pdu) {
                    abstractField.decode(mainRegistry, buffer, formatWriter);
                }
                formatWriter.leaveObject(name + i+1);
                i++;
            }
        }
        return 0;
    }

    @Override
    public String encode(Registry mainRegistry, XMLFormatReader r, StringBuilder binaryString) {

        StringBuilder hexaString = new StringBuilder();
        int i = 1;

        if(r.exist("LengthObject") != null){
            binaryToHex(binaryString.append(String.format("%"+nBit+"s", Integer.toBinaryString(Integer.valueOf(r.stringValue("LengthObject")).byteValue() & 0xFF)).replace(' ', '0')),hexaString);
        }
        else{
            binaryToHex(binaryString.append(String.format("%"+((AbstractTranslatorField)pdu.get(0)).length+"s", Integer.toBinaryString(Integer.valueOf(r.stringValue(pdu.get(0).getName())).byteValue() & 0xFF)).replace(' ', '0')),hexaString);
            pdu =  pdu.subList(1, pdu.size());
        }


        while(r.exist(name + i) != null){
            logger.trace("Enter field {}", name+i);
            r.enterObject(name+i);
            for(AbstractField abstractField: pdu){
                if (abstractField instanceof MessageWrapperField || abstractField instanceof  BinaryField ||  abstractField instanceof  ChoiceField || abstractField instanceof HexadecimalField || abstractField instanceof BinaryLengthField) {
                    hexaString.append(abstractField.encode(mainRegistry, r, binaryString));
                } else {
                    binaryString.append(abstractField.encode(mainRegistry, r, binaryString));
                    binaryToHex(binaryString, hexaString);
                }
            }
            r.leaveObject(name+i);
            i++;
        }

        return hexaString.toString();
    }

    private BitInputStream read(int len, BitInputStream s) throws IOException {

        len = len*8;
        byte[] buffer = Reader.readByte(len,s,logger);

        logger.trace("return buffer 0x{}", bytesToHex(buffer));
        return new BitInputStream(new ByteArrayInputStream(buffer));
    }

    private void binaryToHex(StringBuilder binary, StringBuilder hexaString) {

        if (binary.length() >= 8) {
            int res = Integer.parseInt(binary.toString(), 2);
            String hexStr = Integer.toString(res, 16);
            if (hexStr.length() == 1) {
                if(binary.length() == 16){
                    hexaString.append("000");
                }else{
                    hexaString.append("0");
                }
            }
            hexaString.append(hexStr);
            binary.setLength(0);
        }
    }
}
