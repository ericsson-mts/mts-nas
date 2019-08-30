package com.ericsson.mts.NAS.informationelement.field.wrapper;

import com.ericsson.mts.NAS.BitInputStream;
import com.ericsson.mts.NAS.exceptions.DecodingException;
import com.ericsson.mts.NAS.exceptions.DictionaryException;
import com.ericsson.mts.NAS.exceptions.NotHandledException;
import com.ericsson.mts.NAS.informationelement.field.AbstractTranslatorField;
import com.ericsson.mts.NAS.registry.Registry;
import com.ericsson.mts.NAS.writer.FormatWriter;

import java.io.IOException;

public class MessageWrapperField extends AbstractTranslatorField {


    @Override
    public int decode(Registry mainRegistry, BitInputStream bitInputStream, FormatWriter formatWriter) throws IOException, DecodingException, DictionaryException, NotHandledException {
        logger.trace("Enter field {}", name);
        int result = bitInputStream.readBits(length);
        for(Integer key : namedValue.keySet()){
            if(key == result){
                logger.trace("Decode message {} (0x{})", namedValue.get(key), String.format("%x", key));
                formatWriter.stringValue(name, namedValue.get(key));
                mainRegistry.getMessage(namedValue.get(key)).decode(mainRegistry, bitInputStream, formatWriter);
                return result;
            }
        }
        throw new DecodingException(name + " can't decode " + result);
    }
}
