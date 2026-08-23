package br.com.healthwallet.application.usecase;

import br.com.healthwallet.domain.model.Alergia;
import br.com.healthwallet.domain.model.Paciente;
import br.com.healthwallet.domain.repository.AlergiaRepository;
import br.com.healthwallet.domain.repository.DiagnosticoRepository;
import br.com.healthwallet.domain.repository.PacienteRepository;
import br.com.healthwallet.domain.repository.ReceitaRepository;
import br.com.healthwallet.web.dto.PacienteUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * UC07 - Cadastrar e Gerenciar Perfil de Paciente: leitura, edição e
 * inativação lógica (RF02), sempre restritas ao Usuário dono do perfil
 * (modo cuidador: um Usuário pode gerenciar vários Pacientes).
 */
@Service
@RequiredArgsConstructor
public class PacienteUseCase {

    private final PacienteRepository pacienteRepository;
    private final AlergiaRepository alergiaRepository;
    private final DiagnosticoRepository diagnosticoRepository;
    private final ReceitaRepository receitaRepository;

    public List<Paciente> listarDoUsuario(Long idUsuario) {
        return pacienteRepository.buscarPorUsuario(idUsuario);
    }

    public Paciente buscarDoUsuario(Long idPaciente, Long idUsuario) {
        Paciente paciente = pacienteRepository.buscarPorId(idPaciente)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado: " + idPaciente));

        exigirPropriedade(paciente, idUsuario);
        return paciente;
    }

    /**
     * Inclui a alergia atualmente cadastrada (se houver), para telas como
     * "Gerenciar Paciente" conseguirem exibir/editar sem uma segunda chamada.
     */
    public PacienteComAlergia buscarDetalhadoDoUsuario(Long idPaciente, Long idUsuario) {
        Paciente paciente = buscarDoUsuario(idPaciente, idUsuario);
        Alergia alergia = buscarAlergiaAtual(idPaciente).orElse(null);
        return new PacienteComAlergia(
                paciente,
                alergia,
                diagnosticoRepository.buscarCronicosPorPaciente(idPaciente),
                receitaRepository.buscarItensUsoContinuoPorPaciente(idPaciente));
    }

    @Transactional
    public PacienteComAlergia atualizar(Long idPaciente, Long idUsuario, PacienteUpdateRequest dados) {
        Paciente paciente = buscarDoUsuario(idPaciente, idUsuario);

        paciente.setNome(dados.nome());
        if (dados.cpf() != null) {
            paciente.setCpf(dados.cpf());
        }
        paciente.setDataNascimento(dados.dataNascimento());
        paciente.setTipoSanguineo(dados.tipoSanguineo());
        paciente.setFichaEmergencialAtiva(dados.fichaEmergencialAtiva());

        Paciente atualizado = pacienteRepository.salvar(paciente);
        Optional<Alergia> alergiaExistente = buscarAlergiaAtual(idPaciente);

        Alergia alergiaAtual = null;
        if (Boolean.TRUE.equals(dados.possuiAlergia())) {
            Alergia alergia = alergiaExistente.orElseGet(Alergia::new);
            alergia.setIdPaciente(idPaciente);
            alergia.setTipo(dados.tipoAlergia());
            alergia.setDescricao(dados.descricaoAlergia());
            alergiaAtual = alergiaRepository.salvar(alergia);
        } else if (alergiaExistente.isPresent()) {
            // possuiAlergia=false: o paciente não tem mais alergia -> apaga o registro existente
            // em vez de simplesmente ignorar (senão a alergia antiga nunca some).
            alergiaRepository.deletar(alergiaExistente.get().getId());
        }

        return new PacienteComAlergia(
                atualizado,
                alergiaAtual,
                diagnosticoRepository.buscarCronicosPorPaciente(idPaciente),
                receitaRepository.buscarItensUsoContinuoPorPaciente(idPaciente));
    }

    @Transactional
    public void inativar(Long idPaciente, Long idUsuario) {
        Paciente paciente = buscarDoUsuario(idPaciente, idUsuario);
        paciente.setAtivo(false);
        paciente.setFichaEmergencialAtiva(false);
        pacienteRepository.salvar(paciente);
    }

    private Optional<Alergia> buscarAlergiaAtual(Long idPaciente) {
        return alergiaRepository.buscarPorPaciente(idPaciente).stream().findFirst();
    }

    private void exigirPropriedade(Paciente paciente, Long idUsuario) {
        if (!paciente.getIdUsuario().equals(idUsuario)) {
            throw new SecurityException("Paciente não pertence ao usuário autenticado.");
        }
    }
}
