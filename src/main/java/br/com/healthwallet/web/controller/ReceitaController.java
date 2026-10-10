package br.com.healthwallet.web.controller;

import br.com.healthwallet.application.usecase.ReceitaUseCase;
import br.com.healthwallet.domain.model.ItemReceita;
import br.com.healthwallet.domain.model.Medicamento;
import br.com.healthwallet.domain.model.Receita;
import br.com.healthwallet.domain.model.Upload;
import br.com.healthwallet.infrastructure.security.AuthenticatedUser;
import br.com.healthwallet.web.dto.ReceitaRequest;
import br.com.healthwallet.web.dto.ReceitaResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/receitas")
@RequiredArgsConstructor
public class ReceitaController {

    private final ReceitaUseCase receitaUseCase;

    @PostMapping
    public ResponseEntity<ReceitaResponse> salvar(@Valid @RequestBody ReceitaRequest request) {
        Receita salva = receitaUseCase.salvar(AuthenticatedUser.idOuFalhar(), toModel(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(ReceitaResponse.from(salva));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReceitaResponse> atualizar(@PathVariable Long id,
                                                      @Valid @RequestBody ReceitaRequest request) {
        Receita receita = toModel(request);
        receita.setId(id);
        return ResponseEntity.ok(ReceitaResponse.from(
                receitaUseCase.salvar(AuthenticatedUser.idOuFalhar(), receita)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        receitaUseCase.desativar(AuthenticatedUser.idOuFalhar(), id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/agendamento/{idAgendamento}")
    public ResponseEntity<List<ReceitaResponse>> buscarPorAgendamento(@PathVariable Long idAgendamento) {
        return ResponseEntity.ok(
                receitaUseCase.buscarPorAgendamento(AuthenticatedUser.idOuFalhar(), idAgendamento)
                        .stream().map(ReceitaResponse::from).toList());
    }

    @GetMapping("/paciente/{idPaciente}")
    public ResponseEntity<List<ReceitaResponse>> buscarPorPaciente(@PathVariable Long idPaciente) {
        return ResponseEntity.ok(
                receitaUseCase.buscarPorPaciente(AuthenticatedUser.idOuFalhar(), idPaciente)
                        .stream().map(ReceitaResponse::from).toList());
    }

    private Receita toModel(ReceitaRequest request) {
        Receita receita = new Receita();
        receita.setIdAgendamento(request.idAgendamento());
        receita.setIdPaciente(request.idPaciente());
        receita.setDataEmissao(request.dataEmissao());
        receita.setOrientacoesGerais(request.orientacoesGerais());
        receita.setOrigemProntuario(request.origemProntuario());

        if (request.itens() != null) {
            receita.setItens(request.itens().stream().map(i -> {
                Medicamento medicamento = new Medicamento();
                if (i.medicamento() != null) {
                    medicamento.setId(i.medicamento().id());
                    medicamento.setNomeMedicamento(i.medicamento().nomeMedicamento());
                    medicamento.setLaboratorio(i.medicamento().laboratorio());
                    medicamento.setFeedback(i.medicamento().feedback());
                }

                ItemReceita item = new ItemReceita();
                item.setId(i.id());
                item.setMedicamento(medicamento);
                item.setPosologia(i.posologia());
                item.setUsoContinuo(i.usoContinuo());
                return item;
            }).toList());
        }

        if (request.uploads() != null) {
            receita.setUploads(request.uploads().stream().map(u -> {
                Upload upload = new Upload();
                upload.setId(u.id());
                upload.setBase64(u.base64());
                return upload;
            }).toList());
        }

        return receita;
    }
}
