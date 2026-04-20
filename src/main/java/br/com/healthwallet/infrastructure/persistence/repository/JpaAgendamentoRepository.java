package br.com.healthwallet.infrastructure.persistence.repository;


import br.com.healthwallet.infrastructure.persistence.entity.AgendamentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaAgendamentoRepository extends JpaRepository<AgendamentoEntity, Long> {
    List<AgendamentoEntity> findByPacienteId(Long idPaciente);
    List<AgendamentoEntity> findByPacienteIdAndArquivadoFalse(Long idPaciente);
    List<AgendamentoEntity> findByPacienteIdAndArquivadoTrue(Long idPaciente);
}
