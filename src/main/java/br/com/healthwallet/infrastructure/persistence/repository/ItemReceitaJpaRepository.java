package br.com.healthwallet.infrastructure.persistence.repository;

import br.com.healthwallet.infrastructure.persistence.entity.ItemReceitaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemReceitaJpaRepository extends JpaRepository<ItemReceitaEntity, Long> {

    @Query("SELECT i FROM ItemReceitaEntity i " +
            "WHERE i.receita.agendamento.paciente.id = :idPaciente AND i.usoContinuo = true")
    List<ItemReceitaEntity> findUsoContinuoPorPaciente(@Param("idPaciente") Long idPaciente);
}
