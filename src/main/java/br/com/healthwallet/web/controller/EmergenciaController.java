package br.com.healthwallet.web.controller;

import br.com.healthwallet.application.usecase.EmergenciaUseCase;
import br.com.healthwallet.web.dto.EmergenciaResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * UC06 - Rota pública (não autenticada), acessada por socorristas via QR Code.
 */
@RestController
@RequestMapping("/api/emergencia")
@RequiredArgsConstructor
public class EmergenciaController {

    private final EmergenciaUseCase emergenciaUseCase;

    @GetMapping("/{codigo}")
    public ResponseEntity<EmergenciaResponse> buscarFicha(@PathVariable String codigo) {
        return ResponseEntity.ok(emergenciaUseCase.buscarFichaPublica(codigo));
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<Void> tratarAcessoNegado() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
