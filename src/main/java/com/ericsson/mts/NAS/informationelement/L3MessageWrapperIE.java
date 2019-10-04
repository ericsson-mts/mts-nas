package com.ericsson.mts.NAS.informationelement;

import com.ericsson.mts.NAS.BitInputStream;
import com.ericsson.mts.NAS.exceptions.DecodingException;
import com.ericsson.mts.NAS.exceptions.DictionaryException;
import com.ericsson.mts.NAS.reader.XMLFormatReader;
import com.ericsson.mts.NAS.registry.Registry;
import com.ericsson.mts.NAS.writer.FormatWriter;

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
