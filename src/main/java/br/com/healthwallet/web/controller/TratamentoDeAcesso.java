package br.com.healthwallet.web.controller;

import br.com.healthwallet.web.dto.ErroResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Converte a recusa de propriedade em 403 para todas as rotas autenticadas. A
 * ficha de emergência tem tratamento próprio, por ser rota pública.
 */
@RestControllerAdvice(basePackages = "br.com.healthwallet.web.controller",
        assignableTypes = {PacienteController.class, ExameController.class,
                ReceitaController.class, DiagnosticoController.class, AgendamentoController.class})
public class TratamentoDeAcesso {

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ErroResponse> tratarAcessoNegado(SecurityException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErroResponse(e.getMessage()));
    }
}
