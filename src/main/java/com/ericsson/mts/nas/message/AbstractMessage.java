package com.ericsson.mts.nas.message;

import com.ericsson.mts.nas.BitInputStream;
import com.ericsson.mts.nas.exceptions.DecodingException;
import com.ericsson.mts.nas.exceptions.DictionaryException;
import com.ericsson.mts.nas.exceptions.NotHandledException;
import com.ericsson.mts.nas.reader.XMLFormatReader;
import com.ericsson.mts.nas.registry.Registry;
import com.ericsson.mts.nas.writer.FormatWriter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.common.collect.HashBiMap;
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
    public List<InformationElementsContainer> additionnal;
    public List<InformationElementsContainer> mandatory;
    public Map<String, InformationElementsContainer> optional;
    @JsonIgnore
    public HashBiMap<String, String> optionalMap = HashBiMap.create(1);

    public void setName(String name) {
        this.name = name;
    }

    public abstract void decode(Registry mainRegistry, BitInputStream bitInputStream, FormatWriter formatWriter) throws IOException, DecodingException, DictionaryException, NotHandledException;
    public abstract byte[] encode(Registry mainRegistry, XMLFormatReader r, StringBuilder binaryString) throws DecodingException;
    public abstract byte[] encode(Registry mainRegistry, XMLFormatReader r) throws DecodingException;
}
