package br.com.healthwallet.application.usecase;

import br.com.healthwallet.domain.model.Medicamento;
import br.com.healthwallet.domain.model.Receita;
import br.com.healthwallet.domain.repository.AgendamentoRepository;
import br.com.healthwallet.domain.repository.MedicamentoRepository;
import br.com.healthwallet.domain.repository.ReceitaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReceitaUseCase {

    private final ReceitaRepository receitaRepository;
    private final MedicamentoRepository medicamentoRepository;
    private final AgendamentoRepository agendamentoRepository;
    private final AcessoPaciente acessoPaciente;

    public Receita salvar(Long idUsuario, Receita receita) {
        if (receita.getDataEmissao() == null) {
            receita.setDataEmissao(LocalDateTime.now());
        }

        // Todo documento precisa ficar vinculado a um paciente, senão apareceria na
        // ficha de todos os pacientes da conta.
        if (receita.getIdPaciente() == null && receita.getIdAgendamento() != null) {
            agendamentoRepository.buscarPorId(receita.getIdAgendamento())
                    .ifPresent(a -> receita.setIdPaciente(a.getIdPaciente()));
        }

        if (receita.getItens() != null) {
            receita.getItens().forEach(item -> {
                if (item.getMedicamento() != null && item.getMedicamento().getNomeMedicamento() != null) {

                    Optional<Medicamento> medExistente = medicamentoRepository
                            .buscarPorNome(item.getMedicamento().getNomeMedicamento());

                    if (medExistente.isPresent()) {
                        // Se o remédio já existe, atrelamos o ID. O Mapper vai usar getReferenceById
                        item.getMedicamento().setId(medExistente.get().getId());
                    }
                    // Se não existe, o ID continua nulo, e o Cascade do JPA vai criar um novo na tabela
                }
            });
        }

        acessoPaciente.exigirPropriedade(receita.getIdPaciente(), idUsuario);
        return receitaRepository.salvar(receita);
    }

    public List<Receita> buscarPorAgendamento(Long idUsuario, Long idAgendamento) {
        acessoPaciente.exigirPropriedadePorAgendamento(idAgendamento, idUsuario);
        return receitaRepository.buscarPorAgendamento(idAgendamento);
    }

    public List<Receita> buscarPorPaciente(Long idUsuario, Long idPaciente) {
        acessoPaciente.exigirPropriedade(idPaciente, idUsuario);
        return receitaRepository.buscarPorPaciente(idPaciente);
    }

    public void desativar(Long idUsuario, Long id) {
        Receita receita = receitaRepository.buscarPorId(id)
                .orElseThrow(() -> new SecurityException("Acesso negado."));
        acessoPaciente.exigirPropriedade(receita.getIdPaciente(), idUsuario);
        receitaRepository.desativar(id);
    }
}