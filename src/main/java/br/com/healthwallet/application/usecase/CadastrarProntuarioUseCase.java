package br.com.healthwallet.application.usecase;


import br.com.healthwallet.domain.model.Alergia;
import br.com.healthwallet.domain.model.Paciente;
import br.com.healthwallet.domain.repository.AlergiaRepository;
import br.com.healthwallet.domain.repository.PacienteRepository;
import br.com.healthwallet.web.dto.CadastroProntuarioRequest;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CadastrarProntuarioUseCase {
    private final PacienteRepository pacienteRepository;
    private final AlergiaRepository alergiaRepository;

    public CadastrarProntuarioUseCase(PacienteRepository pacienteRepository, AlergiaRepository alergiaRepository) {
        this.pacienteRepository = pacienteRepository;
        this.alergiaRepository = alergiaRepository;
    }

    @Transactional
    public Paciente executar(CadastroProntuarioRequest request, Long idUsuario) {
        Paciente paciente = new Paciente();
        paciente.setIdUsuario(idUsuario);
        paciente.setCpf(request.cpf());
        paciente.setNome(request.nome());
        paciente.setDataNascimento(request.dataNascimento());
        paciente.setTipoSanguineo(request.tipoSanguineo());
        paciente.setFichaEmergencialAtiva(request.fichaEmergencialAtiva());
        paciente.setAtivo(true);

        paciente.setCodigoEmergencia(UUID.randomUUID().toString());

        Paciente pacienteSalvo = pacienteRepository.salvar(paciente);

        if (Boolean.TRUE.equals(request.possuiAlergia())) {
            Alergia alergia = new Alergia();
            alergia.setIdPaciente(pacienteSalvo.getId());
            alergia.setTipo(request.tipoAlergia());
            alergia.setDescricao(request.descricaoAlergia());

            alergiaRepository.salvar(alergia);
        }

        return pacienteSalvo;
    }
}
