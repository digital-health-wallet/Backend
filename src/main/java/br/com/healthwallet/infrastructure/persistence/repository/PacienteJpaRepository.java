package br.com.healthwallet.infrastructure.persistence.repository;

import br.com.healthwallet.infrastructure.persistence.entity.PacienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PacienteJpaRepository extends JpaRepository<PacienteEntity, Long> {
    List<PacienteEntity> findByUsuarioIdAndAtivoTrue(Long idUsuario);
    Optional<PacienteEntity> findByCodigoEmergencia(String codigoEmergencia);
}
