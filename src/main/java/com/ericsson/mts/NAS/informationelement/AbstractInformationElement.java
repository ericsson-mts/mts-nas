package com.ericsson.mts.NAS.informationelement;

import com.ericsson.mts.NAS.BitInputStream;
import com.ericsson.mts.NAS.exceptions.DecodingException;
import com.ericsson.mts.NAS.exceptions.DictionaryException;
import com.ericsson.mts.NAS.exceptions.NotHandledException;
import com.ericsson.mts.NAS.informationelement.field.AbstractField;
import com.ericsson.mts.NAS.informationelement.field.translator.LengthField;
import com.ericsson.mts.NAS.registry.Registry;
import com.ericsson.mts.NAS.writer.FormatWriter;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, defaultImpl = InformationElement.class)
@JsonSubTypes({
        @JsonSubTypes.Type(value= L3MessageWrapperIE.class, name = "L3 message wrapper"),
        @JsonSubTypes.Type(value = VariableLengthIE.class, name = "VARIABLE LENGTH")
})
public abstract class AbstractInformationElement {
    protected Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
    protected String name;
    protected int ieiLength;
    protected LengthField lengthField;
    protected int length;
    protected List<AbstractField> pdu;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIeiLength() {
        return ieiLength;
    }

    public void setIeiLength(int ieiLength) {
        this.ieiLength = ieiLength;
    }

    public List<AbstractField> getPdu() {
        return pdu;
    }

    public void setPdu(List<AbstractField> pdu) {
        this.pdu = pdu;
    }

    public LengthField getLengthField() {
        return lengthField;
    }

    public void setLengthField(LengthField lengthField) {
        this.lengthField = lengthField;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public abstract int decode(Registry mainRegistry, BitInputStream bitInputStream, FormatWriter formatWriter) throws IOException, DecodingException, DictionaryException, NotHandledException;
}
