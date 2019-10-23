package com.ericsson.mts.nas.message;

public class InformationElementsContainer {
    public String name;
    public String type;
    public Integer length;
    public Integer nBitLength = 8;

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public String name(){
        return name != null ? name : type;
    }
}
