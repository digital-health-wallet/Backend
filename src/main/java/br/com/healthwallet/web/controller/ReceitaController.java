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
}
