package br.com.wfsystems.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN)
public class InvalidJwtAuthenticationException extends AuthenticationException {
 
    public InvalidJwtAuthenticationException(String message) {
        super(message);
    }
}
