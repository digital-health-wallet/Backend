package br.com.healthwallet.web.controller;

import br.com.healthwallet.web.dto.ErroResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "br.com.healthwallet.web.controller")
public class TratamentoDeAcesso {

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ErroResponse> tratarAcessoNegado(SecurityException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErroResponse(e.getMessage()));
    }
}
