package br.com.healthwallet.web.controller;


import br.com.healthwallet.application.usecase.AgendamentoUseCase;
import br.com.healthwallet.domain.model.Agendamento;
import br.com.healthwallet.domain.model.enums.StatusAgendamento;
import br.com.healthwallet.web.dto.AgendamentoRequest;
import br.com.healthwallet.web.dto.AgendamentoResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agendamentos")
@RequiredArgsConstructor
public class AgendamentoController {

    private final AgendamentoUseCase agendamentoUseCase;

    @PostMapping
    public ResponseEntity<AgendamentoResponse> criar(@Valid @RequestBody AgendamentoRequest request) {
        Agendamento agendamento = toModel(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(AgendamentoResponse.from(agendamentoUseCase.criar(agendamento)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgendamentoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(AgendamentoResponse.from(agendamentoUseCase.buscarPorId(id)));
    }

    @GetMapping("/paciente/{idPaciente}")
    public ResponseEntity<List<AgendamentoResponse>> listarAtivos(@PathVariable Long idPaciente) {
        return ResponseEntity.ok(agendamentoUseCase.listarAtivosPorPaciente(idPaciente)
                .stream().map(AgendamentoResponse::from).toList());
    }

    @GetMapping("/paciente/{idPaciente}/arquivados")
    public ResponseEntity<List<AgendamentoResponse>> listarArquivados(@PathVariable Long idPaciente) {
        return ResponseEntity.ok(agendamentoUseCase.listarArquivadosPorPaciente(idPaciente)
                .stream().map(AgendamentoResponse::from).toList());
    }

    @PutMapping("/{id}")
    public ResponseEntity<AgendamentoResponse> atualizar(@PathVariable Long id,
                                                         @Valid @RequestBody AgendamentoRequest request) {
        return ResponseEntity.ok(AgendamentoResponse.from(
                agendamentoUseCase.atualizar(id, toModel(request))));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<AgendamentoResponse> atualizarStatus(@PathVariable Long id,
                                                               @RequestParam StatusAgendamento status) {
        return ResponseEntity.ok(AgendamentoResponse.from(
                agendamentoUseCase.atualizarStatus(id, status)));
    }

    @PatchMapping("/{id}/arquivar")
    public ResponseEntity<AgendamentoResponse> arquivar(@PathVariable Long id) {
        return ResponseEntity.ok(AgendamentoResponse.from(agendamentoUseCase.arquivar(id)));
    }

    @PatchMapping("/{id}/favorito")
    public ResponseEntity<AgendamentoResponse> toggleFavorito(@PathVariable Long id) {
        return ResponseEntity.ok(AgendamentoResponse.from(agendamentoUseCase.toggleFavorito(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        agendamentoUseCase.deletar(id);
        return ResponseEntity.noContent().build();
    }

    private Agendamento toModel(AgendamentoRequest request) {
        Agendamento agendamento = new Agendamento();
        agendamento.setIdPaciente(request.idPaciente());
        agendamento.setEspecialidade(request.especialidade());
        agendamento.setNomeClinica(request.nomeClinica());
        agendamento.setMotivoConsulta(request.motivoConsulta());
        agendamento.setTipoConsulta(request.tipoConsulta());
        agendamento.setDataAgendamento(request.dataAgendamento());
        agendamento.setHoraAgendamento(request.horaAgendamento());
        agendamento.setHoraFim(request.horaFim());
        return agendamento;
    }
}
