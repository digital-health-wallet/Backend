package br.com.healthwallet.infrastructure.persistence.repository;

import br.com.healthwallet.infrastructure.persistence.entity.MedicamentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MedicamentoJpaRepository extends JpaRepository<MedicamentoEntity, Long> {

    Optional<MedicamentoEntity> findByNomeMedicamentoIgnoreCase(String nome);
}