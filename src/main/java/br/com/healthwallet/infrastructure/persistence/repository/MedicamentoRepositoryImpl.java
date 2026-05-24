package br.com.healthwallet.infrastructure.persistence.repository;

import br.com.healthwallet.domain.model.Medicamento;
import br.com.healthwallet.domain.repository.MedicamentoRepository;
import br.com.healthwallet.infrastructure.persistence.entity.MedicamentoEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MedicamentoRepositoryImpl implements MedicamentoRepository {

    private final MedicamentoJpaRepository jpaRepository;

    @Override
    public Medicamento salvar(Medicamento medicamento) {
        MedicamentoEntity entity = new MedicamentoEntity();
        entity.setNomeMedicamento(medicamento.getNomeMedicamento());
        entity.setLaboratorio(medicamento.getLaboratorio());
        entity.setFeedback(medicamento.getFeedback());

        MedicamentoEntity saved = jpaRepository.save(entity);
        medicamento.setId(saved.getId());
        return medicamento;
    }

    @Override
    public Optional<Medicamento> buscarPorNome(String nome) {
        return jpaRepository.findByNomeMedicamentoIgnoreCase(nome).map(entity -> {
            Medicamento domain = new Medicamento();
            domain.setId(entity.getId());
            domain.setNomeMedicamento(entity.getNomeMedicamento());
            domain.setLaboratorio(entity.getLaboratorio());
            domain.setFeedback(entity.getFeedback());
            return domain;
        });
    }
}