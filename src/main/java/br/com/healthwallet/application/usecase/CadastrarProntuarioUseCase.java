package br.com.healthwallet.application.usecase;


import br.com.healthwallet.domain.model.Alergia;
import br.com.healthwallet.domain.model.ItemReceita;
import br.com.healthwallet.domain.model.Medicamento;
import br.com.healthwallet.domain.model.Paciente;
import br.com.healthwallet.domain.model.Receita;
import br.com.healthwallet.domain.repository.AlergiaRepository;
import br.com.healthwallet.domain.repository.PacienteRepository;
import br.com.healthwallet.domain.repository.ReceitaRepository;
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

    /**
     * @param paciente    perfil já preenchido pelo controlador, sem id nem código
     * @param alergias    RF10 - alergias a registrar; pode vir vazia
     * @param medicamentos medicamentos de uso contínuo a registrar; pode vir vazia
     */
    @Transactional
    public Paciente executar(Paciente paciente, Long idUsuario,
                              List<Alergia> alergias, List<ItemReceita> medicamentos) {
        paciente.setIdUsuario(idUsuario);
        paciente.setAtivo(true);
        paciente.setCodigoEmergencia(UUID.randomUUID().toString());

        Paciente pacienteSalvo = pacienteRepository.salvar(paciente);

        if (alergias != null) {
            alergias.forEach(alergia -> {
                alergia.setIdPaciente(pacienteSalvo.getId());
                alergiaRepository.salvar(alergia);
            });
        }

        if (medicamentos != null && !medicamentos.isEmpty()) {
            adicionarMedicamentosContinuos(pacienteSalvo.getId(), medicamentos);
        }

        return pacienteSalvo;
    }

    /**
     * Registra medicamento(s) de uso contínuo direto no prontuário do paciente (não
     * vinculado a uma consulta/receita) — usado tanto no cadastro inicial quanto ao
     * editar um paciente já existente. Não aparece na listagem de Documentos.
     */
    @Transactional
    public void adicionarMedicamentosContinuos(Long idPaciente, List<ItemReceita> medicamentos) {
        List<ItemReceita> itens = medicamentos.stream()
                .peek(item -> item.setUsoContinuo(true))
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
