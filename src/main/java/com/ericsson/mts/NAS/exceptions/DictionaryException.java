package com.ericsson.mts.NAS.exceptions;

public class DictionaryException extends Exception {
    public DictionaryException(String message) {
        super(message);
    }

    public DictionaryException(String message, Throwable cause) {
        super(message, cause);
    }
}
