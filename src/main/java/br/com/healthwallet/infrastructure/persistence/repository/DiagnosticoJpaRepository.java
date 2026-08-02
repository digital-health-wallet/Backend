package br.com.healthwallet.infrastructure.persistence.repository;

import br.com.healthwallet.infrastructure.persistence.entity.DiagnosticoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DiagnosticoJpaRepository extends JpaRepository<DiagnosticoEntity, Long> {
    List<DiagnosticoEntity> findByAgendamentoId(Long agendamentoId);
    List<DiagnosticoEntity> findByAgendamento_Paciente_IdAndDoencaCronicaTrue(Long idPaciente);
}