package com.ericsson.mts.NAS.informationelement.field.translator;

import com.ericsson.mts.NAS.BitInputStream;
import com.ericsson.mts.NAS.informationelement.field.AbstractField;
import com.ericsson.mts.NAS.informationelement.field.AbstractTranslatorField;
import com.ericsson.mts.NAS.registry.Registry;
import com.ericsson.mts.NAS.writer.FormatWriter;

public class DecimalField extends AbstractTranslatorField {

    @Override
    public int decode(Registry mainRegistry, BitInputStream bitInputStream, FormatWriter formatWriter) {
        throw new RuntimeException("TODO");
    }
}
