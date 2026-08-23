package br.com.healthwallet.infrastructure.persistence.repository;

import br.com.healthwallet.infrastructure.persistence.entity.DiagnosticoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DiagnosticoJpaRepository extends JpaRepository<DiagnosticoEntity, Long> {
    List<DiagnosticoEntity> findByAgendamentoId(Long agendamentoId);
    List<DiagnosticoEntity> findByAgendamentoIsNull();

    @Query("SELECT d FROM DiagnosticoEntity d WHERE d.idPaciente = :idPaciente AND d.ativo = true")
    List<DiagnosticoEntity> buscarAtivosDoPaciente(@Param("idPaciente") Long idPaciente);

    @Query("SELECT d FROM DiagnosticoEntity d WHERE d.idPaciente = :idPaciente AND d.ativo = true AND d.doencaCronica = true")
    List<DiagnosticoEntity> buscarCronicosAtivosDoPaciente(@Param("idPaciente") Long idPaciente);
}
