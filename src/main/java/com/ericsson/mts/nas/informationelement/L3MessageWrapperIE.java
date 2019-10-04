package com.ericsson.mts.nas.informationelement;

import com.ericsson.mts.nas.BitInputStream;
import com.ericsson.mts.nas.exceptions.DecodingException;
import com.ericsson.mts.nas.exceptions.DictionaryException;
import com.ericsson.mts.nas.reader.XMLFormatReader;
import com.ericsson.mts.nas.registry.Registry;
import com.ericsson.mts.nas.writer.FormatWriter;

import java.io.IOException;

public class L3MessageWrapperIE extends AbstractInformationElement {

    @Override
    public int decode(Registry mainRegistry, BitInputStream bitInputStream, FormatWriter formatWriter) throws IOException, DecodingException, DictionaryException {
        throw new RuntimeException("TODO");
    }

    @Override
    public void encode(Registry mainRegistry, XMLFormatReader r, StringBuilder binaryString, StringBuilder hexaString) {

    }
}
