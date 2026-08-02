package br.com.healthwallet.web.controller;

import br.com.healthwallet.application.usecase.CadastrarProntuarioUseCase;
import br.com.healthwallet.application.usecase.PacienteComAlergia;
import br.com.healthwallet.application.usecase.PacienteUseCase;
import br.com.healthwallet.domain.model.Paciente;
import br.com.healthwallet.infrastructure.security.AuthenticatedUser;
import br.com.healthwallet.web.dto.CadastroProntuarioRequest;
import br.com.healthwallet.web.dto.PacienteResponse;
import br.com.healthwallet.web.dto.PacienteUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * UC07 - Cadastrar e Gerenciar Perfil de Paciente. Todas as operações são
 * restritas ao Usuário autenticado (modo cuidador: um Usuário pode gerenciar
 * vários Pacientes - o próprio e/ou dependentes).
 */
@RestController
@RequestMapping("/api/pacientes")
@RequiredArgsConstructor
public class PacienteController {

    private final CadastrarProntuarioUseCase cadastrarProntuarioUseCase;
    private final PacienteUseCase pacienteUseCase;

    @PostMapping("/prontuario")
    public ResponseEntity<PacienteResponse> salvarProntuarioCompleto(@RequestBody @Valid CadastroProntuarioRequest request) {
        Paciente pacienteCriado = cadastrarProntuarioUseCase.executar(request, AuthenticatedUser.idOuFalhar());
        PacienteComAlergia detalhado = pacienteUseCase.buscarDetalhadoDoUsuario(pacienteCriado.getId(), AuthenticatedUser.idOuFalhar());
        return ResponseEntity.status(HttpStatus.CREATED).body(PacienteResponse.from(detalhado));
    }

    @GetMapping("/meus")
    public ResponseEntity<List<Paciente>> listarMeusPacientes() {
        return ResponseEntity.ok(pacienteUseCase.listarDoUsuario(AuthenticatedUser.idOuFalhar()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PacienteResponse> buscarPorId(@PathVariable Long id) {
        PacienteComAlergia detalhado = pacienteUseCase.buscarDetalhadoDoUsuario(id, AuthenticatedUser.idOuFalhar());
        return ResponseEntity.ok(PacienteResponse.from(detalhado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PacienteResponse> atualizar(@PathVariable Long id, @Valid @RequestBody PacienteUpdateRequest request) {
        PacienteComAlergia detalhado = pacienteUseCase.atualizar(id, AuthenticatedUser.idOuFalhar(), request);
        return ResponseEntity.ok(PacienteResponse.from(detalhado));
    }

    @PatchMapping("/{id}/inativar")
    public ResponseEntity<Void> inativar(@PathVariable Long id) {
        pacienteUseCase.inativar(id, AuthenticatedUser.idOuFalhar());
        return ResponseEntity.noContent().build();
    }
}
