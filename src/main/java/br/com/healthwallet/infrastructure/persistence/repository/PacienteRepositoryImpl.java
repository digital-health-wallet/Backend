package br.com.healthwallet.infrastructure.persistence.repository;

import br.com.healthwallet.domain.model.Paciente;
import br.com.healthwallet.domain.repository.PacienteRepository;
import br.com.healthwallet.infrastructure.persistence.entity.PacienteEntity;
import br.com.healthwallet.infrastructure.persistence.mapper.PacienteMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PacienteRepositoryImpl implements PacienteRepository {

    private final PacienteJpaRepository jpaRepository;
    private final PacienteMapper mapper;

    @Override
    public List<Paciente> listarTodos() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<Paciente> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Paciente> buscarPorUsuario(Long idUsuario) {
        return jpaRepository.findByUsuarioIdAndAtivoTrue(idUsuario).stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<Paciente> buscarPorCodigoEmergencia(String codigoEmergencia) {
        return jpaRepository.findByCodigoEmergencia(codigoEmergencia).map(mapper::toDomain);
    }

    @Override
    public Paciente salvar(Paciente paciente) {
        PacienteEntity entity = mapper.toEntity(paciente);
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public void deletar(Long id) {
        jpaRepository.deleteById(id);
    }
}