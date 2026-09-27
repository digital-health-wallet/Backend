package br.com.healthwallet.infrastructure.persistence;

import br.com.healthwallet.domain.model.Alergia;
import br.com.healthwallet.domain.model.Paciente;
import br.com.healthwallet.domain.model.enums.TipoAlergia;
import br.com.healthwallet.domain.repository.AlergiaRepository;
import br.com.healthwallet.domain.repository.PacienteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Teste de integração contra o banco real (H2 com as migrações do Flyway aplicadas),
 * atestando por consulta SQL que a exclusão não remove a linha — a mesma verificação
 * que as especificações de caso de uso pedem para comprovar a auditoria do histórico.
 */
@SpringBootTest
class ExclusaoLogicaIntegracaoTest {

    @Autowired
    private PacienteRepository pacienteRepository;
    @Autowired
    private AlergiaRepository alergiaRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Paciente pacienteSalvo() {
        Paciente paciente = new Paciente();
        paciente.setIdUsuario(1L);
        paciente.setNome("Paciente de Teste");
        paciente.setCpf("999.888.777-66");
        paciente.setDataNascimento(LocalDate.of(1985, 3, 12));
        paciente.setTipoSanguineo("A+");
        paciente.setFichaEmergencialAtiva(true);
        paciente.setAtivo(true);
        paciente.setCodigoEmergencia("cod-teste-exclusao-logica");
        return pacienteRepository.salvar(paciente);
    }

    @Test
    @DisplayName("Alergia desativada continua existindo no banco, apenas com ativo = false")
    void alergiaDesativadaPermaneceNoBanco() {
        Paciente paciente = pacienteSalvo();

        Alergia alergia = new Alergia();
        alergia.setIdPaciente(paciente.getId());
        alergia.setTipo(TipoAlergia.M);
        alergia.setDescricao("Penicilina");
        Long idAlergia = alergiaRepository.salvar(alergia).getId();

        alergiaRepository.desativar(idAlergia);

        Integer linhas = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM alergias WHERE id_alergia = ?", Integer.class, idAlergia);
        Boolean ativo = jdbcTemplate.queryForObject(
                "SELECT ativo FROM alergias WHERE id_alergia = ?", Boolean.class, idAlergia);

        assertThat(linhas).as("a linha não pode ser apagada do banco").isEqualTo(1);
        assertThat(ativo).as("a linha precisa ficar marcada como inativa").isFalse();
    }

    @Test
    @DisplayName("Alergia desativada deixa de aparecer nas consultas do paciente")
    void alergiaDesativadaNaoAparecemNasConsultas() {
        Paciente paciente = pacienteSalvo();

        Alergia mantida = new Alergia();
        mantida.setIdPaciente(paciente.getId());
        mantida.setTipo(TipoAlergia.A);
        mantida.setDescricao("Lactose");
        alergiaRepository.salvar(mantida);

        Alergia removida = new Alergia();
        removida.setIdPaciente(paciente.getId());
        removida.setTipo(TipoAlergia.M);
        removida.setDescricao("Ibuprofeno");
        alergiaRepository.desativar(alergiaRepository.salvar(removida).getId());

        assertThat(alergiaRepository.buscarPorPaciente(paciente.getId()))
                .extracting(Alergia::getDescricao)
                .containsExactly("Lactose");
    }
}
