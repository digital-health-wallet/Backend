package br.com.healthwallet.web.controller;

import br.com.healthwallet.application.usecase.DiagnosticoUseCase;
import br.com.healthwallet.domain.model.Diagnostico;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diagnosticos")
@RequiredArgsConstructor
public class DiagnosticoController {

    private final DiagnosticoUseCase diagnosticoUseCase;

    @PostMapping
    public ResponseEntity<Diagnostico> salvar(@RequestBody Diagnostico diagnostico) {
        Diagnostico diagnosticoSalvo = diagnosticoUseCase.salvar(diagnostico);
        return ResponseEntity.status(HttpStatus.CREATED).body(diagnosticoSalvo);
    }

    @GetMapping("/agendamento/{idAgendamento}")
    public ResponseEntity<List<Diagnostico>> buscarPorAgendamento(@PathVariable Long idAgendamento) {
        List<Diagnostico> diagnosticos = diagnosticoUseCase.buscarPorAgendamento(idAgendamento);
        return ResponseEntity.ok(diagnosticos);
    }
}