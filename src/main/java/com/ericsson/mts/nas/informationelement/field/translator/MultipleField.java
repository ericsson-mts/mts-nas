package com.ericsson.mts.nas.informationelement.field.translator;

import com.ericsson.mts.nas.BitInputStream;
import com.ericsson.mts.nas.exceptions.DecodingException;
import com.ericsson.mts.nas.exceptions.DictionaryException;
import com.ericsson.mts.nas.exceptions.NotHandledException;
import com.ericsson.mts.nas.informationelement.field.AbstractField;
import com.ericsson.mts.nas.informationelement.field.AbstractTranslatorField;
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
    public List<AbstractField> pdu;


    @Override
    public int decode(Registry mainRegistry, BitInputStream bitInputStream, FormatWriter formatWriter) throws IOException, DecodingException, DictionaryException, NotHandledException {

        if (contentLength.toLowerCase().contains("number")) {
            formatWriter.enterObject(pdu.get(0).getName());
            int result = bitInputStream.readBits(((DecimalField)pdu.get(0)).length);
            formatWriter.intValue(pdu.get(0).getName(), BigInteger.valueOf(result));
            formatWriter.leaveObject(pdu.get(0).getName());
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
            int result = bitInputStream.bigReadBits(16).intValueExact();
            formatWriter.intValue("Length", BigInteger.valueOf(result));

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
        return null;
    }

    private BitInputStream read(int len, BitInputStream s) throws IOException {

        len = len*8;
        byte[] buffer = Reader.readByte(len,s,logger);

        logger.trace("return buffer 0x{}", bytesToHex(buffer));
        return new BitInputStream(new ByteArrayInputStream(buffer));
    }
}
