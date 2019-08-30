package com.ericsson.mts.NAS.informationelement.field.wrapper;

import com.ericsson.mts.NAS.BitInputStream;
import com.ericsson.mts.NAS.exceptions.DecodingException;
import com.ericsson.mts.NAS.exceptions.DictionaryException;
import com.ericsson.mts.NAS.exceptions.NotHandledException;
import com.ericsson.mts.NAS.informationelement.field.AbstractField;
import com.ericsson.mts.NAS.registry.Registry;
import com.ericsson.mts.NAS.writer.FormatWriter;

import java.io.IOException;
import java.util.List;

public class AdditionalField extends AbstractField {
    private AbstractField field;
    private List<Integer> keys;
    private AbstractField additionnalField;

    public AbstractField getField() {
        return field;
    }

    public void setField(AbstractField field) {
        this.field = field;
    }

    public List<Integer> getKeys() {
        return keys;
    }

    public void setKeys(List<Integer> keys) {
        this.keys = keys;
    }

    public AbstractField getAdditionnalField() {
        return additionnalField;
    }

    public void setAdditionnalField(AbstractField additionnalField) {
        this.additionnalField = additionnalField;
    }

    @Override
    public int decode(Registry mainRegistry, BitInputStream bitInputStream, FormatWriter formatWriter) throws IOException, DecodingException, DictionaryException, NotHandledException {
        logger.trace("Enter field {}", name);
        int result = field.decode(mainRegistry, bitInputStream, formatWriter);
        for (Integer key : keys) {
            if (result == key) {
                return result + additionnalField.decode(mainRegistry, bitInputStream, formatWriter);
            }
        }
        return result;
    }
}
