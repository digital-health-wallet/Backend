package br.com.healthwallet.web.controller;

import br.com.healthwallet.application.usecase.ExameUseCase;
import br.com.healthwallet.domain.model.Exame;
import br.com.healthwallet.domain.model.Upload;
import br.com.healthwallet.infrastructure.security.AuthenticatedUser;
import br.com.healthwallet.web.dto.ExameRequest;
import br.com.healthwallet.web.dto.ExameResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exames")
@RequiredArgsConstructor
public class ExameController {

    private final ExameUseCase exameUseCase;

    @PostMapping
    public ResponseEntity<ExameResponse> salvar(@Valid @RequestBody ExameRequest request) {
        Exame salvo = exameUseCase.salvar(AuthenticatedUser.idOuFalhar(), toModel(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(ExameResponse.from(salvo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExameResponse> atualizar(@PathVariable Long id,
                                                    @Valid @RequestBody ExameRequest request) {
        Exame exame = toModel(request);
        exame.setId(id);
        return ResponseEntity.ok(ExameResponse.from(
                exameUseCase.salvar(AuthenticatedUser.idOuFalhar(), exame)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        exameUseCase.desativar(AuthenticatedUser.idOuFalhar(), id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/agendamento/{idAgendamento}")
    public ResponseEntity<List<ExameResponse>> buscarPorAgendamento(@PathVariable Long idAgendamento) {
        return ResponseEntity.ok(
                exameUseCase.buscarPorAgendamento(AuthenticatedUser.idOuFalhar(), idAgendamento)
                        .stream().map(ExameResponse::from).toList());
    }

    @GetMapping("/paciente/{idPaciente}")
    public ResponseEntity<List<ExameResponse>> buscarPorPaciente(@PathVariable Long idPaciente) {
        return ResponseEntity.ok(
                exameUseCase.buscarPorPaciente(AuthenticatedUser.idOuFalhar(), idPaciente)
                        .stream().map(ExameResponse::from).toList());
    }

    private Exame toModel(ExameRequest request) {
        Exame exame = new Exame();
        exame.setIdAgendamento(request.idAgendamento());
        exame.setIdPaciente(request.idPaciente());
        exame.setNomeExame(request.nomeExame());
        exame.setDataHoraExame(request.dataHoraExame());
        exame.setObservacoes(request.observacoes());

        if (request.uploads() != null) {
            exame.setUploads(request.uploads().stream().map(u -> {
                Upload upload = new Upload();
                upload.setId(u.id());
                upload.setBase64(u.base64());
                return upload;
            }).toList());
        }

        return exame;
    }
}
