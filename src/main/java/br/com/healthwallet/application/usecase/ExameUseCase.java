package br.com.healthwallet.application.usecase;

import br.com.healthwallet.domain.model.Exame;
import br.com.healthwallet.domain.repository.AgendamentoRepository;
import br.com.healthwallet.domain.repository.ExameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExameUseCase {

    private final ExameRepository repository;
    private final AgendamentoRepository agendamentoRepository;

    public Exame salvar(Exame exame) {
        if (exame.getDataHoraExame() == null) {
            exame.setDataHoraExame(LocalDate.now());
        }

        // Todo documento precisa ficar vinculado a um paciente, senão apareceria na
        // ficha de todos os pacientes da conta.
        if (exame.getIdPaciente() == null && exame.getIdAgendamento() != null) {
            agendamentoRepository.buscarPorId(exame.getIdAgendamento())
                    .ifPresent(a -> exame.setIdPaciente(a.getIdPaciente()));
        }

        return repository.salvar(exame);
    }

    public List<Exame> buscarPorAgendamento(Long idAgendamento) {
        return repository.buscarPorAgendamento(idAgendamento);
    }

    public List<Exame> buscarAvulsos() {
        return repository.buscarAvulsos();
    }

    public List<Exame> buscarPorPaciente(Long idPaciente) {
        return repository.buscarPorPaciente(idPaciente);
    }

    public void desativar(Long id) {
        repository.desativar(id);
    }
}