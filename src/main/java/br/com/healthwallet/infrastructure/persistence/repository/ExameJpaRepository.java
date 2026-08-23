package br.com.healthwallet.infrastructure.persistence.repository;

import br.com.healthwallet.infrastructure.persistence.entity.ExameEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ExameJpaRepository extends JpaRepository<ExameEntity, Long> {
    List<ExameEntity> findByAgendamentoId(Long agendamentoId);
    List<ExameEntity> findByAgendamentoIsNull(); // Para exames avulsos

    @Query("SELECT e FROM ExameEntity e WHERE e.idPaciente = :idPaciente AND e.ativo = true")
    List<ExameEntity> buscarAtivosDoPaciente(@Param("idPaciente") Long idPaciente);
}
