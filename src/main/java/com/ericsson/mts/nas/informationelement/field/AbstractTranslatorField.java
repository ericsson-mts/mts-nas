package com.ericsson.mts.nas.informationelement.field;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.HashBiMap;

import java.util.List;
import java.util.Map;


public abstract class AbstractTranslatorField extends AbstractField {
    public Integer length;
    public Map<Integer, String> namedValue;
    @JsonIgnore
    public HashBiMap<Integer,String> namedValueMap = HashBiMap.create(1);
    public Integer nBitLength = 8;
    public List<AbstractField> pdu;
}