package br.com.healthwallet.infrastructure.persistence.repository;

import br.com.healthwallet.infrastructure.persistence.entity.ReceitaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReceitaJpaRepository extends JpaRepository<ReceitaEntity, Long> {
    List<ReceitaEntity> findByAgendamentoId(Long agendamentoId);
    List<ReceitaEntity> findByAgendamentoIsNull();
}