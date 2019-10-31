package com.ericsson.mts.nas.registry;

import com.ericsson.mts.nas.informationelement.AbstractInformationElement;
import com.ericsson.mts.nas.informationelement.field.AbstractField;
import com.ericsson.mts.nas.informationelement.field.AbstractTranslatorField;
import com.ericsson.mts.nas.informationelement.field.FieldMapContainer;
import com.ericsson.mts.nas.informationelement.field.translator.MultipleField;
import com.ericsson.mts.nas.informationelement.field.wrapper.ChoiceField;
import com.ericsson.mts.nas.message.AbstractMessage;
import com.ericsson.mts.nas.message.InformationElementsContainer;
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
            message.setName(convertToCamelCase(message.name));

            if(null != message.mandatory) {
                for (InformationElementsContainer informationElementsContainer : message.mandatory) {
                    informationElementsContainer.setName(convertToCamelCase(informationElementsContainer.name()));
                    informationElementsContainer.setType(convertToCamelCase(informationElementsContainer.type));
                }
            }
            if (null != message.optional) {
                for (InformationElementsContainer informationElementsContainer : message.optional.values()) {
                    informationElementsContainer.setName(convertToCamelCase(informationElementsContainer.name()));
                    informationElementsContainer.setType(convertToCamelCase(informationElementsContainer.type));
                }
                for (String iei : message.optional.keySet()) {
                    message.optionalMap.put(message.optional.get(iei).name,iei);
                }
            }
            if(null != message.additionnal) {
                for (InformationElementsContainer informationElementsContainer : message.additionnal) {
                    informationElementsContainer.setName(convertToCamelCase(informationElementsContainer.name()));
                    informationElementsContainer.setType(convertToCamelCase(informationElementsContainer.type));
                }
            }

        }

        for (AbstractInformationElement informationElement : informationElements.getElements()) {
            informationElement.name =  convertToCamelCase(informationElement.name);
            for (AbstractField abstractField : informationElement.pdu) {
                initAbstractField(abstractField);
            }
        }


    }

    private void initAbstractField(AbstractField abstractField){
        abstractField.setName(convertToCamelCase(abstractField.getName()));
        if (abstractField instanceof AbstractTranslatorField) {
            AbstractTranslatorField abstractTranslatorField = (AbstractTranslatorField) abstractField;
            if (abstractTranslatorField.namedValue != null) {
                for (Integer key : abstractTranslatorField.namedValue.keySet()) {
                    String value = abstractTranslatorField.namedValue.get(key);
                    if (value != null) {
                        abstractTranslatorField.namedValueMap.put(key, convertToCamelCase(value));
                    }
                }
            }
            if(abstractTranslatorField.pdu != null){
                for(AbstractField abstractField1: abstractTranslatorField.pdu){
                    initAbstractField(abstractField1);
                }
            }
        }
        else if(abstractField instanceof ChoiceField){
            ChoiceField choiceField = (ChoiceField) abstractField;
            initAbstractField(choiceField.getField());
            for(FieldMapContainer fieldMapContainer : choiceField.getPdus()){
                for(AbstractField abstractField1 : fieldMapContainer.getPdu()){
                    initAbstractField(abstractField1);
                }
            }
        }
        else if (abstractField instanceof MultipleField) {
            MultipleField multipleField = (MultipleField) abstractField;
            for(AbstractField abstractField1: multipleField.pdu){
                initAbstractField(abstractField1);
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
        if (result.matches("^[1-9].*")) {
            result = "A" + result;
        }

        return result;
    }
}
