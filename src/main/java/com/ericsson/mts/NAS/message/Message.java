package com.ericsson.mts.NAS.message;


import com.ericsson.mts.NAS.BitInputStream;
import com.ericsson.mts.NAS.exceptions.DecodingException;
import com.ericsson.mts.NAS.exceptions.DictionaryException;
import com.ericsson.mts.NAS.exceptions.NotHandledException;
import com.ericsson.mts.NAS.informationelement.AbstractInformationElement;
import com.ericsson.mts.NAS.informationelement.field.translator.LengthField;
import com.ericsson.mts.NAS.registry.Registry;
import com.ericsson.mts.NAS.writer.FormatWriter;

import java.io.IOException;
import java.util.*;

public class Message extends AbstractMessage {

    public void decode(Registry mainRegistry, BitInputStream bitInputStream, FormatWriter formatWriter) throws IOException, DecodingException, DictionaryException, NotHandledException {
        logger.trace("Enter message {}", name);
        formatWriter.enterObject(name);
        Map<Integer, InformationElementContainer> optionnalInformationElementsLength4 = new HashMap<>();
        Map<Integer, InformationElementContainer> optionnalInformationElementsLength4Minus = new HashMap<>();
        Map<Integer, InformationElementContainer> optionnalInformationElementsLength8 = new HashMap<>();
        Map<Integer, InformationElementContainer> optionnalInformationElementsLength16 = new HashMap<>();

        for (InformationElementsContainer informationElementsContainer : informationElements) {
            AbstractInformationElement informationElement = mainRegistry.getInformationElement(informationElementsContainer.getType());
            if(informationElement == null){
                throw new DictionaryException("Can't find information element " + informationElementsContainer.getType());
            }
            if (informationElementsContainer.getPresence().equals(PresenceEnum.M)) {
                logger.trace("Decode field {}", informationElementsContainer.getName());
                if(informationElementsContainer.getFormat() == FormatEnum.TLVE ||
                        informationElementsContainer.getFormat() == FormatEnum.TLV ||
                        informationElementsContainer.getFormat() == FormatEnum.LVE ||
                        informationElementsContainer.getFormat() == FormatEnum.LV){
                    LengthField lengthField = informationElement.getLengthField();
                    informationElement.setLength(lengthField.decode(mainRegistry, bitInputStream, formatWriter) * 8);
                }
                informationElement.decode(mainRegistry, bitInputStream, formatWriter);
            } else if (informationElementsContainer.getPresence().equals(PresenceEnum.O)) {
                if(informationElementsContainer.getFormat() != FormatEnum.T && informationElementsContainer.getFormat() != FormatEnum.TV && informationElementsContainer.getFormat() != FormatEnum.TLV && informationElementsContainer.getFormat() != FormatEnum.TLVE){
                    throw new DictionaryException(informationElementsContainer.getName() + " need to be one of the following format (T, TV, TLV or TLV-E");
                }

                if (informationElement.getIeiLength() == 4) {
                    if(informationElementsContainer.getIei().endsWith("-")){
                        optionnalInformationElementsLength4Minus.put(Integer.valueOf(informationElementsContainer.getIei().replace("-",""), 16), new InformationElementContainer(informationElement, informationElementsContainer));
                    } else {
                        optionnalInformationElementsLength4.put(Integer.valueOf(informationElementsContainer.getIei(), 16), new InformationElementContainer(informationElement, informationElementsContainer));
                    }
                } else if (informationElement.getIeiLength() == 8) {
                    optionnalInformationElementsLength8.put(Integer.valueOf(informationElementsContainer.getIei(), 16), new InformationElementContainer(informationElement, informationElementsContainer));
                } else if (informationElement.getIeiLength() == 16) {
                    optionnalInformationElementsLength16.put(Integer.valueOf(informationElementsContainer.getIei(), 16), new InformationElementContainer(informationElement, informationElementsContainer));
                } else {
                    throw new DictionaryException("Unknown length for information element " + informationElement.getName());
                }
            } else {
                throw new NotHandledException("TODO : handle presence " + informationElementsContainer.getPresence());
            }
        }
        int iei;
        while (bitInputStream.available() > 0) {
            iei = bitInputStream.readBits(4);
            if(optionnalInformationElementsLength4.containsKey(iei)){
                InformationElementContainer informationElementContainer = optionnalInformationElementsLength4.get(iei);
                if(informationElementContainer.getInformationElementsContainer().getFormat() == FormatEnum.TLVE || informationElementContainer.getInformationElementsContainer().getFormat() == FormatEnum.TLV){
                    LengthField lengthField = informationElementContainer.getAbstractInformationElement().getLengthField();
                    int length = lengthField.decode(mainRegistry, bitInputStream, formatWriter);

                    informationElementContainer.getAbstractInformationElement().decode(mainRegistry, bitInputStream, formatWriter);
                }
            } else {

            }
        }

        formatWriter.leaveObject(name);
    }

    private class InformationElementContainer{
        private AbstractInformationElement abstractInformationElement;
        private InformationElementsContainer informationElementsContainer;

        public InformationElementContainer(AbstractInformationElement abstractInformationElement, InformationElementsContainer informationElementsContainer) {
            this.abstractInformationElement = abstractInformationElement;
            this.informationElementsContainer = informationElementsContainer;
        }

        public AbstractInformationElement getAbstractInformationElement() {
            return abstractInformationElement;
        }

        public void setAbstractInformationElement(AbstractInformationElement abstractInformationElement) {
            this.abstractInformationElement = abstractInformationElement;
        }

        public InformationElementsContainer getInformationElementsContainer() {
            return informationElementsContainer;
        }

        public void setInformationElementsContainer(InformationElementsContainer informationElementsContainer) {
            this.informationElementsContainer = informationElementsContainer;
        }
    }
}
