package com.ericsson.mts.nas.registry;

import com.ericsson.mts.nas.informationelement.AbstractInformationElement;

import java.util.List;

public class InformationElements {
    private List<AbstractInformationElement> elements;

    public void addElement(AbstractInformationElement element) {
        elements.add(element);
    }

    public AbstractInformationElement getElement(String elementName) {
        for (AbstractInformationElement informationElement : elements) {
            if (elementName.equals(informationElement.name)) {
                return informationElement;
            }
        }
        return null;
    }

    public List<AbstractInformationElement> getElements() {
        return elements;
    }

    public void setElements(List<AbstractInformationElement> elements) {
        this.elements = elements;
    }
}
