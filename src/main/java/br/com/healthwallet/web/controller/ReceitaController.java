package br.com.healthwallet.web.controller;

import br.com.healthwallet.application.usecase.ReceitaUseCase;
import br.com.healthwallet.domain.model.Receita;
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
    public ResponseEntity<Receita> salvar(@RequestBody Receita receita) {
        Receita receitaSalva = receitaUseCase.salvar(receita);
        return ResponseEntity.status(HttpStatus.CREATED).body(receitaSalva);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Receita> atualizar(@PathVariable Long id, @RequestBody Receita receita) {
        receita.setId(id);
        return ResponseEntity.ok(receitaUseCase.salvar(receita));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        receitaUseCase.desativar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/agendamento/{idAgendamento}")
    public ResponseEntity<List<Receita>> buscarPorAgendamento(@PathVariable Long idAgendamento) {
        List<Receita> receitas = receitaUseCase.buscarPorAgendamento(idAgendamento);
        return ResponseEntity.ok(receitas);
    }

    @GetMapping("/avulsas")
    public ResponseEntity<List<Receita>> buscarAvulsas() {
        List<Receita> receitas = receitaUseCase.buscarAvulsas();
        return ResponseEntity.ok(receitas);
    }

    @GetMapping("/paciente/{idPaciente}")
    public ResponseEntity<List<Receita>> buscarPorPaciente(@PathVariable Long idPaciente) {
        List<Receita> receitas = receitaUseCase.buscarPorPaciente(idPaciente);
        return ResponseEntity.ok(receitas);
    }
}
