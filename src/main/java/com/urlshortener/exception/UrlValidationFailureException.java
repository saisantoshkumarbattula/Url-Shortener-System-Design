package com.urlshortener.exception;

public class UrlValidationFailureException extends RuntimeException  {
    public UrlValidationFailureException(String message) {
        super(message);
    }
}
