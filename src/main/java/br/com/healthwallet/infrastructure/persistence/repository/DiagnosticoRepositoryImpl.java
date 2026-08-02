package br.com.healthwallet.infrastructure.persistence.repository;

import br.com.healthwallet.domain.model.Diagnostico;
import br.com.healthwallet.domain.repository.DiagnosticoRepository;
import br.com.healthwallet.infrastructure.persistence.entity.DiagnosticoEntity;
import br.com.healthwallet.infrastructure.persistence.mapper.DiagnosticoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DiagnosticoRepositoryImpl implements DiagnosticoRepository {

    private final DiagnosticoJpaRepository jpaRepository;
    private final DiagnosticoMapper mapper;

    @Override
    public Diagnostico salvar(Diagnostico diagnostico) {
        DiagnosticoEntity entity = mapper.toEntity(diagnostico);
        DiagnosticoEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Diagnostico> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Diagnostico> buscarPorAgendamento(Long idAgendamento) {
        return jpaRepository.findByAgendamentoId(idAgendamento).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Diagnostico> buscarCronicosPorPaciente(Long idPaciente) {
        return jpaRepository.findByAgendamento_Paciente_IdAndDoencaCronicaTrue(idPaciente).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deletar(Long id) {
        jpaRepository.deleteById(id);
    }
}