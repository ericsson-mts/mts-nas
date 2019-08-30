package com.ericsson.mts.NAS.message;

public class InformationElementsContainer {
    private String iei;
    private String name;
    private String type;
    private PresenceEnum presence;
    private FormatEnum format;
    private Length length;

    public String getIei() {
        return iei;
    }

    public void setIei(String iei) {
        this.iei = iei;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public PresenceEnum getPresence() {
        return presence;
    }

    public void setPresence(PresenceEnum presence) {
        this.presence = presence;
    }

    public FormatEnum getFormat() {
        return format;
    }

    public void setFormat(FormatEnum format) {
        this.format = format;
    }

    public Length getLength() {
        return length;
    }

    public void setLength(Length length) {
        this.length = length;
    }
}
