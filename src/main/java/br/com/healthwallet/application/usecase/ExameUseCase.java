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
    private final AcessoPaciente acessoPaciente;

    public Exame salvar(Long idUsuario, Exame exame) {
        if (exame.getDataHoraExame() == null) {
            exame.setDataHoraExame(LocalDate.now());
        }

        // Todo documento precisa ficar vinculado a um paciente, senão apareceria na
        // ficha de todos os pacientes da conta.
        if (exame.getIdPaciente() == null && exame.getIdAgendamento() != null) {
            agendamentoRepository.buscarPorId(exame.getIdAgendamento())
                    .ifPresent(a -> exame.setIdPaciente(a.getIdPaciente()));
        }

        acessoPaciente.exigirPropriedade(exame.getIdPaciente(), idUsuario);
        return repository.salvar(exame);
    }

    public List<Exame> buscarPorAgendamento(Long idUsuario, Long idAgendamento) {
        acessoPaciente.exigirPropriedadePorAgendamento(idAgendamento, idUsuario);
        return repository.buscarPorAgendamento(idAgendamento);
    }

    public List<Exame> buscarPorPaciente(Long idUsuario, Long idPaciente) {
        acessoPaciente.exigirPropriedade(idPaciente, idUsuario);
        return repository.buscarPorPaciente(idPaciente);
    }

    public void desativar(Long idUsuario, Long id) {
        Exame exame = repository.buscarPorId(id)
                .orElseThrow(() -> new SecurityException("Acesso negado."));
        acessoPaciente.exigirPropriedade(exame.getIdPaciente(), idUsuario);
        repository.desativar(id);
    }
}