package br.com.healthwallet.web.controller;

import br.com.healthwallet.application.usecase.GerenciarContatoUseCase;
import br.com.healthwallet.domain.model.Contato;
import br.com.healthwallet.web.dto.ContatoRequest;
import br.com.healthwallet.web.dto.ContatoResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contatos")
@RequiredArgsConstructor
public class ContatoController {
    private final GerenciarContatoUseCase contatoUseCase;

    @PostMapping
    public ResponseEntity<ContatoResponse> criar(@Valid @RequestBody ContatoRequest request) {
        Contato contato = toModel(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ContatoResponse.from(contatoUseCase.salvarOuAtualizar(contato)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContatoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ContatoResponse.from(contatoUseCase.buscarPorId(id)));
    }

    @GetMapping
    public ResponseEntity<List<ContatoResponse>> listarTodos() {
        return ResponseEntity.ok(contatoUseCase.listarTodos()
                .stream().map(ContatoResponse::from).toList());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContatoResponse> atualizar(@PathVariable Long id,
                                                      @Valid @RequestBody ContatoRequest request) {
        Contato contato = toModel(request);
        contato.setId(id);
        return ResponseEntity.ok(ContatoResponse.from(
                contatoUseCase.salvarOuAtualizar(contato)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        contatoUseCase.deletar(id);
        return ResponseEntity.noContent().build();
    }

    private Contato toModel(ContatoRequest request) {
        Contato contato = new Contato();
        contato.setNome(request.nome());
        contato.setParentesco(request.parentesco());
        contato.setTelefone(request.telefone());
        contato.setEmail(request.email());
        return contato;
    }
}
