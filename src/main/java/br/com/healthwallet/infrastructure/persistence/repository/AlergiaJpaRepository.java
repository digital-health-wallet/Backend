package br.com.healthwallet.infrastructure.persistence.repository;

import br.com.healthwallet.infrastructure.persistence.entity.AlergiaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlergiaJpaRepository extends JpaRepository <AlergiaEntity, Long> {
    List<AlergiaEntity> findByPacienteId(Long idPaciente);
}
