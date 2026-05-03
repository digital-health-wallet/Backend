package br.com.healthwallet.infrastructure.persistence.repository;

import br.com.healthwallet.infrastructure.persistence.entity.ProfissionalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfissionalJpaRepository extends JpaRepository<ProfissionalEntity, Long> {
    Optional<ProfissionalEntity> findByNomeProfissionalIgnoreCase(String nomeProfissional);
}