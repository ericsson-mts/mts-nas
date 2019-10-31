package com.ericsson.mts.nas.informationelement;

import com.ericsson.mts.nas.BitInputStream;
import com.ericsson.mts.nas.exceptions.DecodingException;
import com.ericsson.mts.nas.exceptions.DictionaryException;
import com.ericsson.mts.nas.exceptions.NotHandledException;
import com.ericsson.mts.nas.informationelement.field.AbstractField;
import com.ericsson.mts.nas.reader.XMLFormatReader;
import com.ericsson.mts.nas.registry.Registry;
import com.ericsson.mts.nas.writer.FormatWriter;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, defaultImpl = InformationElement.class)
//@JsonSubTypes({
//        @JsonSubTypes.Type(value= L3MessageWrapperIE.class, name = "L3 message wrapper"),
//        @JsonSubTypes.Type(value = VariableLengthIE.class, name = "VARIABLE LENGTH")
//})
public abstract class AbstractInformationElement {
    protected Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
    public String name;
    public List<AbstractField> pdu;

    public abstract int decode(Registry mainRegistry, BitInputStream bitInputStream, FormatWriter formatWriter) throws IOException, DecodingException, DictionaryException, NotHandledException;
    public abstract void encode(Registry mainRegistry, XMLFormatReader r, StringBuilder binaryString, StringBuilder hexaString) throws DecodingException;
}
