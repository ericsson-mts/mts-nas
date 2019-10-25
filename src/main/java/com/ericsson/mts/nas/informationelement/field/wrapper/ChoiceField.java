package com.ericsson.mts.nas.informationelement.field.wrapper;

import com.ericsson.mts.nas.BitInputStream;
import com.ericsson.mts.nas.exceptions.DecodingException;
import com.ericsson.mts.nas.exceptions.DictionaryException;
import com.ericsson.mts.nas.exceptions.NotHandledException;
import com.ericsson.mts.nas.informationelement.field.AbstractField;
import com.ericsson.mts.nas.informationelement.field.AbstractTranslatorField;
import com.ericsson.mts.nas.informationelement.field.FieldMapContainer;
import com.ericsson.mts.nas.reader.XMLFormatReader;
import com.ericsson.mts.nas.registry.Registry;
import com.ericsson.mts.nas.writer.FormatWriter;

import java.io.IOException;
import java.util.List;

import static com.ericsson.mts.nas.reader.Reader.encodeFields;
import static com.ericsson.mts.nas.reader.XMLFormatReader.binaryToHex;

public class ChoiceField extends AbstractField {
    private AbstractField field;
    private List<FieldMapContainer> pdus;

    public AbstractField getField() {
        return field;
    }

    public void setField(AbstractField field) {
        this.field = field;
    }

    public List<FieldMapContainer> getPdus() {
        return pdus;
    }

    public void setPdus(List<FieldMapContainer> pdus) {
        this.pdus = pdus;
    }

    @Override
    public int decode(Registry mainRegistry, BitInputStream bitInputStream, FormatWriter formatWriter) throws IOException, DecodingException, DictionaryException, NotHandledException {
        logger.trace("Enter field {}", name);
        int result = field.decode(mainRegistry, bitInputStream, formatWriter);
        for (FieldMapContainer fieldMapContainer : pdus) {
            if (fieldMapContainer.getKeys().contains(result)) {
                for (AbstractField abstractField : fieldMapContainer.getPdu()) {
                    abstractField.decode(mainRegistry, bitInputStream, formatWriter);
                }
                return result;
            }
        }
        throw new DecodingException("Can't find key associated to " + result);
    }

    @Override
    public String encode(Registry mainRegistry, XMLFormatReader r, StringBuilder binaryString) throws DecodingException {
        logger.trace("Enter field {}", name);

        StringBuilder hexaString = new StringBuilder();
        StringBuilder result = new StringBuilder();

        result.append(field.encode(mainRegistry, r, binaryString));
        binaryString.append(result);
        int decimal = Integer.parseInt(result.toString(), 2);
        binaryToHex(binaryString, hexaString, ((AbstractTranslatorField) field).length);

        for (FieldMapContainer fieldMapContainer : pdus) {
            if (fieldMapContainer.getKeys().contains(decimal)) {
                encodeFields(fieldMapContainer.getPdu(),mainRegistry,r,binaryString,hexaString);
                return hexaString.toString();
            }
        }
        throw new DecodingException("Can't decode key "+decimal+ " for CHOICE field");
    }
}

