package br.com.wfsystems.controllers.docs;

import org.springframework.http.ResponseEntity;

import br.com.wfsystems.data.dto.security.AccountCredentialsDTO;
import io.swagger.v3.oas.annotations.Operation;

public interface AuthControllerDocs {

    @Operation(summary = "Authenticates an user and returns a token")
    ResponseEntity<?> signIn(AccountCredentialsDTO credentials);

    @Operation(summary = "Refresh Token for authenticated user and returns a token")
    ResponseEntity<?> refreshToken(String username, String refreshToken);

    AccountCredentialsDTO create(AccountCredentialsDTO credentials);

}