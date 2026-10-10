package br.com.healthwallet.application.usecase;

import br.com.healthwallet.domain.model.Alergia;
import br.com.healthwallet.domain.model.Paciente;
import br.com.healthwallet.domain.repository.AlergiaRepository;
import br.com.healthwallet.domain.repository.DiagnosticoRepository;
import br.com.healthwallet.domain.repository.PacienteRepository;
import br.com.healthwallet.domain.repository.ReceitaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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
     * Inclui as alergias cadastradas, para telas como "Gerenciar Paciente"
     * conseguirem exibir/editar sem uma segunda chamada.
     */
    public PacienteComAlergia buscarDetalhadoDoUsuario(Long idPaciente, Long idUsuario) {
        Paciente paciente = buscarDoUsuario(idPaciente, idUsuario);
        return new PacienteComAlergia(
                paciente,
                alergiaRepository.buscarPorPaciente(idPaciente),
                diagnosticoRepository.buscarCronicosPorPaciente(idPaciente),
                receitaRepository.buscarItensUsoContinuoPorPaciente(idPaciente));
    }

    @Transactional
    /**
     * @param dados    perfil com os campos já preenchidos pelo controlador
     * @param alergias lista que deve passar a valer; vazia remove todas as atuais
     */
    public PacienteComAlergia atualizar(Long idPaciente, Long idUsuario, Paciente dados,
                                         List<Alergia> alergias) {
        Paciente paciente = buscarDoUsuario(idPaciente, idUsuario);

        paciente.setNome(dados.getNome());
        if (dados.getCpf() != null) {
            paciente.setCpf(dados.getCpf());
        }
        paciente.setDataNascimento(dados.getDataNascimento());
        paciente.setTipoSanguineo(dados.getTipoSanguineo());
        paciente.setFichaEmergencialAtiva(dados.getFichaEmergencialAtiva());

        Paciente atualizado = pacienteRepository.salvar(paciente);

        List<Alergia> informadas = sincronizarAlergias(idPaciente, alergias);

        return new PacienteComAlergia(
                atualizado,
                informadas,
                diagnosticoRepository.buscarCronicosPorPaciente(idPaciente),
                receitaRepository.buscarItensUsoContinuoPorPaciente(idPaciente));
    }

    /**
     * RF10 - reconcilia o histórico de alergias do paciente: grava as informadas e
     * desativa as que foram removidas na tela. A desativação preserva o registro,
     * conforme a política de exclusão lógica adotada para dado clínico.
     */
    private List<Alergia> sincronizarAlergias(Long idPaciente, List<Alergia> informadas) {
        List<Alergia> existentes = alergiaRepository.buscarPorPaciente(idPaciente);
        List<Alergia> recebidas = informadas != null ? informadas : List.of();

        Set<Long> idsMantidos = recebidas.stream()
                .map(Alergia::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        existentes.stream()
                .filter(existente -> !idsMantidos.contains(existente.getId()))
                .forEach(removida -> alergiaRepository.desativar(removida.getId()));

        return recebidas.stream()
                .filter(recebida -> recebida.getDescricao() != null && !recebida.getDescricao().isBlank())
                .map(recebida -> {
                    recebida.setIdPaciente(idPaciente);
                    recebida.setAtivo(true);
                    return alergiaRepository.salvar(recebida);
                })
                .toList();
    }

    @Transactional
    public void inativar(Long idPaciente, Long idUsuario) {
        Paciente paciente = buscarDoUsuario(idPaciente, idUsuario);
        paciente.setAtivo(false);
        paciente.setFichaEmergencialAtiva(false);
        pacienteRepository.salvar(paciente);
    }

    private void exigirPropriedade(Paciente paciente, Long idUsuario) {
        if (!paciente.getIdUsuario().equals(idUsuario)) {
            throw new SecurityException("Paciente não pertence ao usuário autenticado.");
        }
    }
}
