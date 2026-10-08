package br.com.healthwallet.web.controller;

import br.com.healthwallet.application.usecase.DiagnosticoUseCase;
import br.com.healthwallet.domain.model.Diagnostico;
import br.com.healthwallet.infrastructure.security.AuthenticatedUser;
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
        Diagnostico diagnosticoSalvo = diagnosticoUseCase.salvar(AuthenticatedUser.idOuFalhar(), diagnostico);
        return ResponseEntity.status(HttpStatus.CREATED).body(diagnosticoSalvo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Diagnostico> atualizar(@PathVariable Long id, @RequestBody Diagnostico diagnostico) {
        diagnostico.setId(id);
        return ResponseEntity.ok(diagnosticoUseCase.salvar(AuthenticatedUser.idOuFalhar(), diagnostico));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        diagnosticoUseCase.desativar(AuthenticatedUser.idOuFalhar(), id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/agendamento/{idAgendamento}")
    public ResponseEntity<List<Diagnostico>> buscarPorAgendamento(@PathVariable Long idAgendamento) {
        List<Diagnostico> diagnosticos = diagnosticoUseCase.buscarPorAgendamento(AuthenticatedUser.idOuFalhar(), idAgendamento);
        return ResponseEntity.ok(diagnosticos);
    }

    @GetMapping("/paciente/{idPaciente}")
    public ResponseEntity<List<Diagnostico>> buscarPorPaciente(@PathVariable Long idPaciente) {
        List<Diagnostico> diagnosticos = diagnosticoUseCase.buscarPorPaciente(AuthenticatedUser.idOuFalhar(), idPaciente);
        return ResponseEntity.ok(diagnosticos);
    }
}