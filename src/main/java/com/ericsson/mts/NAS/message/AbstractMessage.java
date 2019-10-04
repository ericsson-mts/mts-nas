package com.ericsson.mts.NAS.message;

import com.ericsson.mts.NAS.BitInputStream;
import com.ericsson.mts.NAS.exceptions.DecodingException;
import com.ericsson.mts.NAS.exceptions.DictionaryException;
import com.ericsson.mts.NAS.exceptions.NotHandledException;
import com.ericsson.mts.NAS.reader.XMLFormatReader;
import com.ericsson.mts.NAS.registry.Registry;
import com.ericsson.mts.NAS.writer.FormatWriter;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, defaultImpl = Message.class)
@JsonSubTypes({
        @JsonSubTypes.Type(value= Message.class, name = "L3 message wrapper")
})

public abstract class AbstractMessage {
    protected Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    public String name;
    public List<InformationElementsContainer> mandatory;
    public Map<String, InformationElementsContainer> optional;

    public void setName(String name) {
        this.name = name;
    }

    public abstract void decode(Registry mainRegistry, BitInputStream bitInputStream, FormatWriter formatWriter) throws IOException, DecodingException, DictionaryException, NotHandledException;
    public abstract byte[] encode(Registry mainRegistry, XMLFormatReader r);
}
