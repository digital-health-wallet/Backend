package br.com.healthwallet.application.usecase;


import br.com.healthwallet.domain.model.Alergia;
import br.com.healthwallet.domain.model.ItemReceita;
import br.com.healthwallet.domain.model.Medicamento;
import br.com.healthwallet.domain.model.Paciente;
import br.com.healthwallet.domain.model.Receita;
import br.com.healthwallet.domain.repository.AlergiaRepository;
import br.com.healthwallet.domain.repository.PacienteRepository;
import br.com.healthwallet.domain.repository.ReceitaRepository;
import br.com.healthwallet.web.dto.CadastroProntuarioRequest;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class CadastrarProntuarioUseCase {
    private final PacienteRepository pacienteRepository;
    private final AlergiaRepository alergiaRepository;
    private final ReceitaRepository receitaRepository;

    public CadastrarProntuarioUseCase(PacienteRepository pacienteRepository,
                                       AlergiaRepository alergiaRepository,
                                       ReceitaRepository receitaRepository) {
        this.pacienteRepository = pacienteRepository;
        this.alergiaRepository = alergiaRepository;
        this.receitaRepository = receitaRepository;
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

        if (Boolean.TRUE.equals(request.usaMedicamentoContinuo())
                && request.medicamentosContinuos() != null
                && !request.medicamentosContinuos().isEmpty()) {
            adicionarMedicamentosContinuos(pacienteSalvo.getId(), request.medicamentosContinuos());
        }

        return pacienteSalvo;
    }

    /**
     * Registra medicamento(s) de uso contínuo direto no prontuário do paciente (não
     * vinculado a uma consulta/receita) — usado tanto no cadastro inicial quanto ao
     * editar um paciente já existente. Não aparece na listagem de Documentos.
     */
    @Transactional
    public void adicionarMedicamentosContinuos(Long idPaciente,
                                                List<CadastroProntuarioRequest.MedicamentoContinuoRequest> medicamentos) {
        List<ItemReceita> itens = medicamentos.stream()
                .map(med -> {
                    Medicamento medicamento = new Medicamento();
                    medicamento.setNomeMedicamento(med.nome());

                    ItemReceita item = new ItemReceita();
                    item.setMedicamento(medicamento);
                    item.setPosologia(med.posologia());
                    item.setUsoContinuo(true);
                    return item;
                })
                .collect(Collectors.toList());

        // Receita avulsa (sem agendamento) marcada como origem do prontuário: existe só
        // pra registrar o uso contínuo direto no perfil do paciente — não deve aparecer
        // na listagem de Documentos.
        Receita receita = new Receita();
        receita.setDataEmissao(LocalDateTime.now());
        receita.setItens(itens);
        receita.setOrigemProntuario(true);
        receita.setIdPaciente(idPaciente);

        receitaRepository.salvar(receita);
    }
}
