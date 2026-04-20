package br.com.healthwallet.infrastructure.persistence.repository;

import br.com.healthwallet.domain.model.Agendamento;
import br.com.healthwallet.domain.repository.AgendamentoRepository;
import br.com.healthwallet.infrastructure.persistence.mapper.AgendamentoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AgendamentoRepositoryImpl implements AgendamentoRepository {

    private final JpaAgendamentoRepository jpa;
    private final AgendamentoMapper mapper;

    @Override
    public Agendamento salvar(Agendamento agendamento) {
        return mapper.toDomain(jpa.save(mapper.toEntity(agendamento)));
    }

    @Override
    public Optional<Agendamento> buscarPorId(Long id) {
        return jpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Agendamento> buscarPorPaciente(Long idPaciente) {
        return jpa.findByPacienteId(idPaciente).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Agendamento> buscarAtivosPorPaciente(Long idPaciente) {
        return jpa.findByPacienteIdAndArquivadoFalse(idPaciente).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Agendamento> buscarArquivadosPorPaciente(Long idPaciente) {
        return jpa.findByPacienteIdAndArquivadoTrue(idPaciente).stream().map(mapper::toDomain).toList();
    }

    @Override
    public Agendamento atualizar(Agendamento agendamento) {
        return mapper.toDomain(jpa.save(mapper.toEntity(agendamento)));
    }

    @Override
    public void deletar(Long id) {
        jpa.deleteById(id);
    }

}
