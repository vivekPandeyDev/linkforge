package io.github.vivek.linkforge.api.advice;

import lombok.Getter;

@Getter
public class LinkNotFoundException extends RuntimeException {


    private final String code;

    public LinkNotFoundException(String code) {
        super("Link not found for code: " + code);
        this.code = code;
    }

}