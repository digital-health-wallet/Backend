package br.com.healthwallet.web.controller;

import br.com.healthwallet.application.usecase.CadastrarProntuarioUseCase;
import br.com.healthwallet.domain.model.Paciente;
import br.com.healthwallet.domain.repository.PacienteRepository;
import br.com.healthwallet.web.dto.CadastroProntuarioRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pacientes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PacienteController {

    private final CadastrarProntuarioUseCase cadastrarProntuarioUseCase;
    private final PacienteRepository pacienteRepository;

    @PostMapping("/prontuario")
    public ResponseEntity<Paciente> salvarProntuarioCompleto(@RequestBody @Valid CadastroProntuarioRequest request) {
        Paciente pacienteCriado = cadastrarProntuarioUseCase.executar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(pacienteCriado);
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Paciente>> buscarPorUsuario(@PathVariable Long usuarioId) {
        List<Paciente> pacientes = pacienteRepository.buscarPorUsuario(usuarioId);
        return ResponseEntity.ok(pacientes);
    }

    @GetMapping
    public ResponseEntity<List<Paciente>> listarTodos() {
        return ResponseEntity.ok(pacienteRepository.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Paciente> buscarPorId(@PathVariable Long id) {
        return pacienteRepository.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        pacienteRepository.deletar(id);
        return ResponseEntity.noContent().build();
    }
}