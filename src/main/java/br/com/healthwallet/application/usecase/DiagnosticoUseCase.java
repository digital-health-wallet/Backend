package br.com.healthwallet.application.usecase;

import br.com.healthwallet.domain.model.Diagnostico;
import br.com.healthwallet.domain.repository.DiagnosticoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DiagnosticoUseCase {

    private final DiagnosticoRepository repository;

    public Diagnostico salvar(Diagnostico diagnostico) {
        // Regras de negócio podem entrar aqui (ex: validar se o CID existe)
        return repository.salvar(diagnostico);
    }

    public List<Diagnostico> buscarPorAgendamento(Long idAgendamento) {
        return repository.buscarPorAgendamento(idAgendamento);
    }
}