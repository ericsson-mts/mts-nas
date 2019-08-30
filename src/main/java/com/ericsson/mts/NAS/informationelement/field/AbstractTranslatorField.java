package com.ericsson.mts.NAS.informationelement.field;

import com.ericsson.mts.NAS.informationelement.field.AbstractField;

import java.util.Map;

public abstract class AbstractTranslatorField extends AbstractField {
    protected int length;
    protected Map<Integer, String> namedValue;

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public Map<Integer, String> getNamedValue() {
        return namedValue;
    }

    public void setNamedValue(Map<Integer, String> namedValue) {
        this.namedValue = namedValue;
    }
}