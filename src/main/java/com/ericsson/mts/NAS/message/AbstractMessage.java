package com.ericsson.mts.NAS.message;

import com.ericsson.mts.NAS.BitInputStream;
import com.ericsson.mts.NAS.exceptions.DecodingException;
import com.ericsson.mts.NAS.exceptions.DictionaryException;
import com.ericsson.mts.NAS.exceptions.NotHandledException;
import com.ericsson.mts.NAS.registry.Registry;
import com.ericsson.mts.NAS.writer.FormatWriter;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, defaultImpl = Message.class)
@JsonSubTypes({
        @JsonSubTypes.Type(value= L3Message.class, name = "L3 message wrapper")
})

public abstract class AbstractMessage {
    protected Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
    protected String name;
    protected List<InformationElementsContainer> informationElements;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<InformationElementsContainer> getInformationElements() {
        return informationElements;
    }

    public void setInformationElements(List<InformationElementsContainer> informationElements) {
        this.informationElements = informationElements;
    }

    public abstract void decode(Registry mainRegistry, BitInputStream bitInputStream, FormatWriter formatWriter) throws IOException, DecodingException, DictionaryException, NotHandledException;
}
