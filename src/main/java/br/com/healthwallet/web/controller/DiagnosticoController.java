package br.com.healthwallet.web.controller;

import br.com.healthwallet.application.usecase.DiagnosticoUseCase;
import br.com.healthwallet.domain.model.Diagnostico;
import br.com.healthwallet.infrastructure.security.AuthenticatedUser;
import br.com.healthwallet.web.dto.DiagnosticoRequest;
import br.com.healthwallet.web.dto.DiagnosticoResponse;
import jakarta.validation.Valid;
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
    public ResponseEntity<DiagnosticoResponse> salvar(@Valid @RequestBody DiagnosticoRequest request) {
        Diagnostico salvo = diagnosticoUseCase.salvar(AuthenticatedUser.idOuFalhar(), toModel(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(DiagnosticoResponse.from(salvo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DiagnosticoResponse> atualizar(@PathVariable Long id,
                                                          @Valid @RequestBody DiagnosticoRequest request) {
        Diagnostico diagnostico = toModel(request);
        diagnostico.setId(id);
        return ResponseEntity.ok(DiagnosticoResponse.from(
                diagnosticoUseCase.salvar(AuthenticatedUser.idOuFalhar(), diagnostico)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        diagnosticoUseCase.desativar(AuthenticatedUser.idOuFalhar(), id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/agendamento/{idAgendamento}")
    public ResponseEntity<List<DiagnosticoResponse>> buscarPorAgendamento(@PathVariable Long idAgendamento) {
        return ResponseEntity.ok(
                diagnosticoUseCase.buscarPorAgendamento(AuthenticatedUser.idOuFalhar(), idAgendamento)
                        .stream().map(DiagnosticoResponse::from).toList());
    }

    @GetMapping("/paciente/{idPaciente}")
    public ResponseEntity<List<DiagnosticoResponse>> buscarPorPaciente(@PathVariable Long idPaciente) {
        return ResponseEntity.ok(
                diagnosticoUseCase.buscarPorPaciente(AuthenticatedUser.idOuFalhar(), idPaciente)
                        .stream().map(DiagnosticoResponse::from).toList());
    }

    private Diagnostico toModel(DiagnosticoRequest request) {
        Diagnostico diagnostico = new Diagnostico();
        diagnostico.setIdAgendamento(request.idAgendamento());
        diagnostico.setIdPaciente(request.idPaciente());
        diagnostico.setNome(request.nome());
        diagnostico.setCid(request.cid());
        diagnostico.setDescricao(request.descricao());
        diagnostico.setDoencaCronica(request.doencaCronica());
        return diagnostico;
    }
}
