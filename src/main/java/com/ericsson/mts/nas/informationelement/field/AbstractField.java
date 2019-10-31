package com.ericsson.mts.nas.informationelement.field;


import com.ericsson.mts.nas.BitInputStream;
import com.ericsson.mts.nas.exceptions.DecodingException;
import com.ericsson.mts.nas.exceptions.DictionaryException;
import com.ericsson.mts.nas.exceptions.NotHandledException;
import com.ericsson.mts.nas.informationelement.field.translator.*;
import com.ericsson.mts.nas.informationelement.field.wrapper.ChoiceField;
import com.ericsson.mts.nas.informationelement.field.wrapper.MessageWrapperField;
import com.ericsson.mts.nas.reader.XMLFormatReader;
import com.ericsson.mts.nas.registry.Registry;
import com.ericsson.mts.nas.writer.FormatWriter;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, defaultImpl = DecimalField.class)
@JsonSubTypes({
        @JsonSubTypes.Type(value= ChoiceField.class, name = "CHOICE"),
        @JsonSubTypes.Type(value= DecimalField.class, name = "DEC"),
        @JsonSubTypes.Type(value = MessageWrapperField.class, name = "MESSAGE_WRAPPER"),
        @JsonSubTypes.Type(value = SpareField.class, name = "SPARE"),
        @JsonSubTypes.Type(value = DigitsField.class, name = "DIGITS"),
        @JsonSubTypes.Type(value = HexadecimalField.class, name = "HEXA"),
        @JsonSubTypes.Type(value = BinaryField.class, name = "BIN"),
        @JsonSubTypes.Type(value = MultipleField.class, name = "MULTI"),
        @JsonSubTypes.Type(value = BinaryLengthField.class, name = "BINLENGTH")
})
public abstract class AbstractField {
    protected String name;
    protected Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public abstract int decode(Registry mainRegistry, BitInputStream bitInputStream, FormatWriter formatWriter) throws IOException, DecodingException, DictionaryException, NotHandledException;
    public abstract String encode(Registry mainRegistry, XMLFormatReader r, StringBuilder binaryString) throws DecodingException;
}
