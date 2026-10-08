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
    private final AcessoPaciente acessoPaciente;

    public Diagnostico salvar(Long idUsuario, Diagnostico diagnostico) {
        // Todo documento precisa ficar vinculado a um paciente, senão apareceria na
        // ficha de todos os pacientes da conta.
        if (diagnostico.getIdPaciente() == null && diagnostico.getIdAgendamento() != null) {
            agendamentoRepository.buscarPorId(diagnostico.getIdAgendamento())
                    .ifPresent(a -> diagnostico.setIdPaciente(a.getIdPaciente()));
        }

        acessoPaciente.exigirPropriedade(diagnostico.getIdPaciente(), idUsuario);
        return repository.salvar(diagnostico);
    }

    public List<Diagnostico> buscarPorAgendamento(Long idUsuario, Long idAgendamento) {
        acessoPaciente.exigirPropriedadePorAgendamento(idAgendamento, idUsuario);
        return repository.buscarPorAgendamento(idAgendamento);
    }

    public List<Diagnostico> buscarPorPaciente(Long idUsuario, Long idPaciente) {
        acessoPaciente.exigirPropriedade(idPaciente, idUsuario);
        return repository.buscarPorPaciente(idPaciente);
    }

    public void desativar(Long idUsuario, Long id) {
        Diagnostico diagnostico = repository.buscarPorId(id)
                .orElseThrow(() -> new SecurityException("Acesso negado."));
        acessoPaciente.exigirPropriedade(diagnostico.getIdPaciente(), idUsuario);
        repository.desativar(id);
    }
}