package br.com.healthwallet.application.usecase;

import br.com.healthwallet.domain.model.Exame;
import br.com.healthwallet.domain.repository.ExameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExameUseCase {

    private final ExameRepository repository;

    public Exame salvar(Exame exame) {
        if (exame.getDataHoraExame() == null) {
            exame.setDataHoraExame(LocalDate.now());
        }
        return repository.salvar(exame);
    }

    public List<Exame> buscarPorAgendamento(Long idAgendamento) {
        return repository.buscarPorAgendamento(idAgendamento);
    }

    public List<Exame> buscarAvulsos() {
        return repository.buscarAvulsos();
    }
}