package com.ericsson.mts.NAS.registry;

import com.ericsson.mts.NAS.informationelement.AbstractInformationElement;
import com.ericsson.mts.NAS.informationelement.InformationElement;
import com.ericsson.mts.NAS.informationelement.field.AbstractField;
import com.ericsson.mts.NAS.informationelement.field.AbstractTranslatorField;
import com.ericsson.mts.NAS.informationelement.field.FieldMapContainer;
import com.ericsson.mts.NAS.informationelement.field.wrapper.AdditionalField;
import com.ericsson.mts.NAS.informationelement.field.wrapper.ChoiceField;
import com.ericsson.mts.NAS.message.AbstractMessage;
import com.ericsson.mts.NAS.message.InformationElementsContainer;
import com.ericsson.mts.NAS.message.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.io.InputStream;

public class Registry {
    private ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    private Messages messages;
    private InformationElements informationElements;

    public void init() {
        for (AbstractMessage message : messages.getMessages()) {
            message.setName(convertToCamelCase(message.getName()));
            for (InformationElementsContainer informationElementsContainer : message.getInformationElements()) {
                informationElementsContainer.setName(convertToCamelCase(informationElementsContainer.getName()));
                informationElementsContainer.setType(convertToCamelCase(informationElementsContainer.getType()));
            }
        }

        for (AbstractInformationElement informationElement : informationElements.getElements()) {
            informationElement.setName(convertToCamelCase(informationElement.getName()));
            for (AbstractField abstractField : informationElement.getPdu()) {
                initAbstractField(abstractField);
            }
        }
    }

    private void initAbstractField(AbstractField abstractField){
        abstractField.setName(convertToCamelCase(abstractField.getName()));
        if (abstractField instanceof AbstractTranslatorField) {
            AbstractTranslatorField abstractTranslatorField = (AbstractTranslatorField) abstractField;
            if (abstractTranslatorField.getNamedValue() != null) {
                for (Integer key : abstractTranslatorField.getNamedValue().keySet()) {
                    String value = abstractTranslatorField.getNamedValue().get(key);
                    if (value != null) {
                        abstractTranslatorField.getNamedValue().put(key, convertToCamelCase(value));
                    }
                }
            }
        } else if (abstractField instanceof AdditionalField) {
            AdditionalField additionalField = (AdditionalField) abstractField;
            initAbstractField(additionalField.getField());
            initAbstractField(additionalField.getAdditionnalField());
        } else if(abstractField instanceof ChoiceField){
            ChoiceField choiceField = (ChoiceField) abstractField;
            initAbstractField(choiceField.getField());
            for(FieldMapContainer fieldMapContainer : choiceField.getPdus()){
                for(AbstractField abstractField1 : fieldMapContainer.getPdu()){
                    initAbstractField(abstractField1);
                }
            }
        }
    }

    public void loadMessages(InputStream inputStream) throws IOException {
        messages = mapper.readValue(inputStream, Messages.class);
    }

    public void loadInformationElements(InputStream inputStream) throws IOException {
        informationElements = mapper.readValue(inputStream, InformationElements.class);
    }

    public void mergeRegistry(Registry anotherRegistry) {
        for (AbstractMessage message : anotherRegistry.getMessages().getMessages()) {
            messages.addMessage(message);
        }

        for (AbstractInformationElement informationElement : anotherRegistry.getInformationElements().getElements()) {
            informationElements.addElement(informationElement);
        }
    }

    public AbstractMessage getMessage(String messageName) {
        return messages.getMessage(messageName);
    }

    public Messages getMessages() {
        return messages;
    }

    public AbstractInformationElement getInformationElement(String informationElementName) {
        return informationElements.getElement(informationElementName);
    }

    public InformationElements getInformationElements() {
        return informationElements;
    }

    public void setMessages(Messages messages) {
        this.messages = messages;
    }

    public void setInformationElements(InformationElements informationElements) {
        this.informationElements = informationElements;
    }

    private String convertToCamelCase(String text) {
        String result = "", result1 = "";
        for (int i = 0; i < text.length(); i++) {
            String next = text.substring(i, i + 1);
            if (i == 0) {
                result += next.toUpperCase();
            } else {
                result += next.toLowerCase();
            }
        }
        String[] splited = result.split("\\s+");
        String[] splited1 = new String[splited.length];

        for (int i = 0; i < splited.length; i++) {
            int l = splited[i].length();
            result1 = "";
            for (int j = 0; j < splited[i].length(); j++) {
                String next = splited[i].substring(j, j + 1);

                if (j == 0) {
                    result1 += next.toUpperCase();
                } else {
                    result1 += next.toLowerCase();
                }
            }
            splited1[i] = result1;
        }
        result = "";
        for (int i = 0; i < splited1.length; i++) {
            result += " " + splited1[i];
        }
        result = result.replaceAll(" ", "").replaceAll("-", "").replaceAll("/", "");
        if(result.matches("^[1-9].*")){
            result = "A" + result;
        }

        return result;
    }
}
