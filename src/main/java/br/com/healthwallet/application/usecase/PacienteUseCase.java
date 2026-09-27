package br.com.healthwallet.application.usecase;

import br.com.healthwallet.domain.model.Alergia;
import br.com.healthwallet.domain.model.Paciente;
import br.com.healthwallet.domain.repository.AlergiaRepository;
import br.com.healthwallet.domain.repository.DiagnosticoRepository;
import br.com.healthwallet.domain.repository.PacienteRepository;
import br.com.healthwallet.domain.repository.ReceitaRepository;
import br.com.healthwallet.web.dto.AlergiaRequest;
import br.com.healthwallet.web.dto.PacienteUpdateRequest;
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

        List<Alergia> informadas = Boolean.TRUE.equals(dados.possuiAlergia())
                ? sincronizarAlergias(idPaciente, dados.alergias())
                : sincronizarAlergias(idPaciente, List.of());

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
    private List<Alergia> sincronizarAlergias(Long idPaciente, List<AlergiaRequest> informadas) {
        List<Alergia> existentes = alergiaRepository.buscarPorPaciente(idPaciente);
        List<AlergiaRequest> recebidas = informadas != null ? informadas : List.of();

        Set<Long> idsMantidos = recebidas.stream()
                .map(AlergiaRequest::id)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        existentes.stream()
                .filter(existente -> !idsMantidos.contains(existente.getId()))
                .forEach(removida -> alergiaRepository.desativar(removida.getId()));

        return recebidas.stream()
                .filter(recebida -> recebida.descricao() != null && !recebida.descricao().isBlank())
                .map(recebida -> {
                    Alergia alergia = new Alergia();
                    alergia.setId(recebida.id());
                    alergia.setIdPaciente(idPaciente);
                    alergia.setTipo(recebida.tipo());
                    alergia.setDescricao(recebida.descricao());
                    alergia.setAtivo(true);
                    return alergiaRepository.salvar(alergia);
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
