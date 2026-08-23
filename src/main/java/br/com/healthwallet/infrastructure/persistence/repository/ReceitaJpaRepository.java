package br.com.healthwallet.infrastructure.persistence.repository;

import br.com.healthwallet.infrastructure.persistence.entity.ReceitaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReceitaJpaRepository extends JpaRepository<ReceitaEntity, Long> {
    List<ReceitaEntity> findByAgendamentoId(Long agendamentoId);
    List<ReceitaEntity> findByAgendamentoIsNull();

    // Receitas de origem do prontuário ficam de fora: elas existem só para registrar
    // medicamento de uso contínuo no perfil, não são documentos da consulta.
    @Query("SELECT r FROM ReceitaEntity r WHERE r.idPaciente = :idPaciente AND r.ativo = true AND r.origemProntuario = false")
    List<ReceitaEntity> buscarAtivasDoPaciente(@Param("idPaciente") Long idPaciente);
}
