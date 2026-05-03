package br.com.healthwallet.web.controller;

import br.com.healthwallet.application.usecase.GerenciarProfissionalUseCase;
import br.com.healthwallet.domain.model.Endereco;
import br.com.healthwallet.domain.model.Profissional;
import br.com.healthwallet.web.dto.ProfissionalRequest;
import br.com.healthwallet.web.dto.ProfissionalResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/profissionais")
@RequiredArgsConstructor
public class ProfissionalController {
    private final GerenciarProfissionalUseCase profissionalUseCase;

    @PostMapping
    public ResponseEntity<ProfissionalResponse> criar(@Valid @RequestBody ProfissionalRequest request) {
        Profissional profissional = toModel(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ProfissionalResponse.from(profissionalUseCase.salvarOuAtualizar(profissional)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfissionalResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ProfissionalResponse.from(profissionalUseCase.buscarPorId(id)));
    }

    @GetMapping
    public ResponseEntity<List<ProfissionalResponse>> listarTodos() {
        return ResponseEntity.ok(profissionalUseCase.listarTodos()
                .stream().map(ProfissionalResponse::from).toList());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProfissionalResponse> atualizar(@PathVariable Long id,
                                                          @Valid @RequestBody ProfissionalRequest request) {
        Profissional profissional = toModel(request);
        profissional.setId(id);
        return ResponseEntity.ok(ProfissionalResponse.from(
                profissionalUseCase.salvarOuAtualizar(profissional)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        profissionalUseCase.deletar(id);
        return ResponseEntity.noContent().build();
    }

    private Profissional toModel(ProfissionalRequest request) {
        Profissional profissional = new Profissional();
        profissional.setNomeProfissional(request.nomeProfissional());
        profissional.setNumeroIdentificacaoProfissional(request.numeroIdentificacaoProfissional());
        profissional.setContato(request.contato());
        profissional.setNomeClinica(request.nomeClinica());
        profissional.setEmail(request.email());
        profissional.setEspecialidade(request.especialidade());

        if (request.endereco() != null) {
            Endereco endereco = new Endereco();
            endereco.setCep(request.endereco().cep());
            endereco.setLogradouro(request.endereco().logradouro());
            endereco.setNumero(request.endereco().numero());
            endereco.setBairro(request.endereco().bairro());
            endereco.setCidade(request.endereco().cidade());
            endereco.setEstado(request.endereco().estado());
            endereco.setComplemento(request.endereco().complemento());
            profissional.setEndereco(endereco);
        }

        return profissional;
    }
}