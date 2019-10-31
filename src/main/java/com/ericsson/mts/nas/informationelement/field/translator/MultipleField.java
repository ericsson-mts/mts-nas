package com.ericsson.mts.nas.informationelement.field.translator;

import com.ericsson.mts.nas.BitInputStream;
import com.ericsson.mts.nas.exceptions.DecodingException;
import com.ericsson.mts.nas.exceptions.DictionaryException;
import com.ericsson.mts.nas.exceptions.NotHandledException;
import com.ericsson.mts.nas.informationelement.field.AbstractField;
import com.ericsson.mts.nas.informationelement.field.AbstractTranslatorField;
import com.ericsson.mts.nas.reader.Reader;
import com.ericsson.mts.nas.reader.XMLFormatReader;
import com.ericsson.mts.nas.registry.Registry;
import com.ericsson.mts.nas.writer.FormatWriter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import static com.ericsson.mts.nas.reader.Reader.encodeFields;
import static com.ericsson.mts.nas.reader.XMLFormatReader.binaryToHex;

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
            formatWriter.intValue("LengthObject", BigInteger.valueOf(result));

            BitInputStream buffer = new BitInputStream(new ByteArrayInputStream(Reader.readByte(result*8,nBit,bitInputStream,logger, formatWriter)));

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
    public String encode(Registry mainRegistry, XMLFormatReader r, StringBuilder binaryString) throws DecodingException {

        StringBuilder hexaString = new StringBuilder();
        int i = 1;

        if(r.exist("LengthObject") != null){
            binaryToHex(binaryString.append(String.format("%"+nBit+"s", Integer.toBinaryString(Integer.valueOf(r.stringValue("LengthObject")).byteValue() & 0xFF)).replace(' ', '0')),hexaString,nBit);
        } else{
            binaryToHex(binaryString.append(String.format("%"+((AbstractTranslatorField)pdu.get(0)).length+"s", Integer.toBinaryString(Integer.valueOf(r.stringValue(pdu.get(0).getName())).byteValue() & 0xFF)).replace(' ', '0')),hexaString, ((AbstractTranslatorField)pdu.get(0)).length);
            pdu =  pdu.subList(1, pdu.size());
        }

        while(r.isElementExist()){
            logger.trace("Enter field {}", name+i);
            r.enterObject(name+i);
            if(r.isElementExist()) {
                encodeFields(pdu, mainRegistry, r, binaryString, hexaString);
                r.leaveObject(name + i);
                i++;
            }
        }

        return hexaString.toString();
    }
}
