package com.ericsson.mts.nas.informationelement.field;

import com.ericsson.mts.nas.informationelement.field.AbstractField;

import java.math.BigInteger;
import java.util.Map;

public abstract class AbstractTranslatorField extends AbstractField {
    public Integer length;
    public Map<Integer, String> namedValue;
}