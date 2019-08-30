package com.ericsson.mts.NAS.informationelement.field.wrapper;

import com.ericsson.mts.NAS.BitInputStream;
import com.ericsson.mts.NAS.exceptions.DecodingException;
import com.ericsson.mts.NAS.exceptions.DictionaryException;
import com.ericsson.mts.NAS.exceptions.NotHandledException;
import com.ericsson.mts.NAS.informationelement.field.AbstractField;
import com.ericsson.mts.NAS.informationelement.field.FieldMapContainer;
import com.ericsson.mts.NAS.registry.Registry;
import com.ericsson.mts.NAS.writer.FormatWriter;

import java.io.IOException;
import java.util.List;

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
        for(FieldMapContainer fieldMapContainer : pdus){
            if(fieldMapContainer.getKeys().contains(result)){
                for(AbstractField abstractField : fieldMapContainer.getPdu()){
                    abstractField.decode(mainRegistry, bitInputStream, formatWriter);
                }
                return result;
            }
        }
        throw new DecodingException("Can't find key associated to " + result);
    }
}
