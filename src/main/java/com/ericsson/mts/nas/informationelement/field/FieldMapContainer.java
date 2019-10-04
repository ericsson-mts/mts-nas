package com.ericsson.mts.nas.informationelement.field;

import java.util.List;

public class FieldMapContainer {
    private List<Integer> keys;
    private List<AbstractField> pdu;

    public List<Integer> getKeys() {
        return keys;
    }

    public void setKeys(List<Integer> keys) {
        this.keys = keys;
    }

    public List<AbstractField> getPdu() {
        return pdu;
    }

    public void setPdu(List<AbstractField> pdu) {
        this.pdu = pdu;
    }
}
