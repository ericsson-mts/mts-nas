package com.ericsson.mts.NAS.message;

import com.ericsson.mts.NAS.BitInputStream;
import com.ericsson.mts.NAS.exceptions.DecodingException;
import com.ericsson.mts.NAS.exceptions.DictionaryException;
import com.ericsson.mts.NAS.exceptions.NotHandledException;
import com.ericsson.mts.NAS.informationelement.AbstractInformationElement;
import com.ericsson.mts.NAS.registry.Registry;
import com.ericsson.mts.NAS.writer.FormatWriter;

import java.io.IOException;

public class L3Message extends AbstractMessage {

    @Override
    public void decode(Registry mainRegistry, BitInputStream bitInputStream, FormatWriter formatWriter) throws IOException, DecodingException, DictionaryException, NotHandledException {
        logger.trace("Enter message {}", name);
        int protocolDiscriminateurId = -1;
        formatWriter.enterObject(name);
        for (InformationElementsContainer informationElementsContainer : informationElements) {
            logger.trace("Decode field {}", informationElementsContainer.getName());
            if ("ProtocolDiscriminator".equals(informationElementsContainer.getName())){
                AbstractInformationElement informationElement = mainRegistry.getInformationElement(informationElementsContainer.getType());
                protocolDiscriminateurId = informationElement.decode(mainRegistry, bitInputStream, formatWriter);
            } else if("ProcedureTransactionIdentity".equals(informationElementsContainer.getName()) && protocolDiscriminateurId == 2){
                AbstractInformationElement informationElement = mainRegistry.getInformationElement(informationElementsContainer.getType());
                protocolDiscriminateurId = informationElement.decode(mainRegistry, bitInputStream, formatWriter);
            } else {
                if(informationElementsContainer.getPresence().equals(PresenceEnum.M)){
                    AbstractInformationElement informationElement = mainRegistry.getInformationElement(informationElementsContainer.getType());
                    informationElement.decode(mainRegistry, bitInputStream, formatWriter);
                }
            }
        }
        formatWriter.leaveObject(name);
    }
}
