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
                .filter(d -> Boolean.TRUE.equals(d.getAtivo()))
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Diagnostico> buscarCronicosPorPaciente(Long idPaciente) {
        return jpaRepository.buscarCronicosAtivosDoPaciente(idPaciente).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Diagnostico> buscarPorPaciente(Long idPaciente) {
        return jpaRepository.buscarAtivosDoPaciente(idPaciente).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Diagnostico> buscarAvulsos() {
        return jpaRepository.findByAgendamentoIsNull().stream()
                .filter(d -> Boolean.TRUE.equals(d.getAtivo()))
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deletar(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public void desativar(Long id) {
        jpaRepository.findById(id).ifPresent(entity -> {
            entity.setAtivo(false);
            jpaRepository.save(entity);
        });
    }
}