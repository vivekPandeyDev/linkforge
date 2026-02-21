package io.github.vivek.linkforge.api.advice;

import lombok.Getter;

@Getter
public class UserNotFoundException extends RuntimeException {


    private final String email;

    public UserNotFoundException(String email) {
        super("User found for email: " + email);
        this.email = email;
    }

}