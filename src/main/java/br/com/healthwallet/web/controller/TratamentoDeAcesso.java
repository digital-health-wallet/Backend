package br.com.healthwallet.web.controller;

import br.com.healthwallet.web.dto.ErroResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Converte a recusa de propriedade em 403, com a mensagem, para os controladores
 * da aplicação. A ficha de emergência declara o próprio tratamento e por isso não
 * é afetada: lá a recusa sai como 404, para não revelar se um código existe.
 */
@RestControllerAdvice(basePackages = "br.com.healthwallet.web.controller")
public class TratamentoDeAcesso {

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ErroResponse> tratarAcessoNegado(SecurityException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErroResponse(e.getMessage()));
    }
}
