package br.com.healthwallet.web.controller;

import br.com.healthwallet.application.usecase.ExameUseCase;
import br.com.healthwallet.domain.model.Exame;
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
    public ResponseEntity<Exame> salvar(@RequestBody Exame exame) {
        Exame exameSalvo = exameUseCase.salvar(exame);
        return ResponseEntity.status(HttpStatus.CREATED).body(exameSalvo);
    }

    @GetMapping("/agendamento/{idAgendamento}")
    public ResponseEntity<List<Exame>> buscarPorAgendamento(@PathVariable Long idAgendamento) {
        List<Exame> exames = exameUseCase.buscarPorAgendamento(idAgendamento);
        return ResponseEntity.ok(exames);
    }

    @GetMapping("/avulsos")
    public ResponseEntity<List<Exame>> buscarAvulsos() {
        List<Exame> exames = exameUseCase.buscarAvulsos();
        return ResponseEntity.ok(exames);
    }
}