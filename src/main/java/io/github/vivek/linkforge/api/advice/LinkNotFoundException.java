package io.github.vivek.linkforge.api.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;

public class LinkNotFoundException extends ErrorResponseException {


    public LinkNotFoundException(String code) {
        super(HttpStatus.NOT_FOUND, buildProblem(code), null);
    }

    private static ProblemDetail buildProblem(String code) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        pd.setTitle("Link Not Found");
        pd.setDetail("Link not found for code: " + code);
        pd.setProperty("shortCode", code);
        return pd;
    }

}