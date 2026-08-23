package br.com.healthwallet.application.usecase;

import br.com.healthwallet.domain.model.Diagnostico;
import br.com.healthwallet.domain.repository.AgendamentoRepository;
import br.com.healthwallet.domain.repository.DiagnosticoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DiagnosticoUseCase {

    private final DiagnosticoRepository repository;
    private final AgendamentoRepository agendamentoRepository;

    public Diagnostico salvar(Diagnostico diagnostico) {
        // Todo documento precisa ficar vinculado a um paciente, senão apareceria na
        // ficha de todos os pacientes da conta.
        if (diagnostico.getIdPaciente() == null && diagnostico.getIdAgendamento() != null) {
            agendamentoRepository.buscarPorId(diagnostico.getIdAgendamento())
                    .ifPresent(a -> diagnostico.setIdPaciente(a.getIdPaciente()));
        }

        return repository.salvar(diagnostico);
    }

    public List<Diagnostico> buscarPorAgendamento(Long idAgendamento) {
        return repository.buscarPorAgendamento(idAgendamento);
    }

    public List<Diagnostico> buscarPorPaciente(Long idPaciente) {
        return repository.buscarPorPaciente(idPaciente);
    }

    public void desativar(Long id) {
        repository.desativar(id);
    }
}