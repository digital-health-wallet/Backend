package br.com.healthwallet.application.usecase;

import br.com.healthwallet.domain.model.ItemReceita;
import br.com.healthwallet.domain.model.Medicamento;
import br.com.healthwallet.domain.model.Receita;
import br.com.healthwallet.domain.repository.AgendamentoRepository;
import br.com.healthwallet.domain.repository.MedicamentoRepository;
import br.com.healthwallet.domain.repository.ReceitaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * RF09 - Uma receita comporta vários medicamentos, cada um com posologia própria
 * e marcação independente de uso contínuo.
 */
@ExtendWith(MockitoExtension.class)
class ReceitaUseCaseTest {

    @Mock
    private ReceitaRepository receitaRepository;
    @Mock
    private MedicamentoRepository medicamentoRepository;
    @Mock
    private AgendamentoRepository agendamentoRepository;

    @InjectMocks
    private ReceitaUseCase receitaUseCase;

    private ItemReceita item(String nome, String posologia, boolean usoContinuo) {
        Medicamento medicamento = new Medicamento();
        medicamento.setNomeMedicamento(nome);

        ItemReceita item = new ItemReceita();
        item.setMedicamento(medicamento);
        item.setPosologia(posologia);
        item.setUsoContinuo(usoContinuo);
        return item;
    }

    @Test
    @DisplayName("Persiste todos os medicamentos informados na receita")
    void devePersistirVariosMedicamentos() {
        Receita receita = new Receita();
        receita.setIdPaciente(1L);
        receita.setItens(List.of(
                item("Losartana 50mg", "1 comprimido pela manhã", true),
                item("Dipirona 500mg", "1 comprimido a cada 6 horas", false)));

        when(medicamentoRepository.buscarPorNome(any())).thenReturn(Optional.empty());
        when(receitaRepository.salvar(any())).thenAnswer(chamada -> chamada.getArgument(0));

        ArgumentCaptor<Receita> capturada = ArgumentCaptor.forClass(Receita.class);
        receitaUseCase.salvar(receita);
        org.mockito.Mockito.verify(receitaRepository).salvar(capturada.capture());

        assertThat(capturada.getValue().getItens())
                .hasSize(2)
                .extracting(i -> i.getMedicamento().getNomeMedicamento())
                .containsExactly("Losartana 50mg", "Dipirona 500mg");
    }

    @Test
    @DisplayName("Marcação de uso contínuo é independente por medicamento")
    void usoContinuoEIndependentePorItem() {
        Receita receita = new Receita();
        receita.setIdPaciente(1L);
        receita.setItens(List.of(
                item("Losartana 50mg", "1 pela manhã", true),
                item("Dipirona 500mg", "a cada 6 horas", false)));

        when(medicamentoRepository.buscarPorNome(any())).thenReturn(Optional.empty());
        when(receitaRepository.salvar(any())).thenAnswer(chamada -> chamada.getArgument(0));

        Receita salva = receitaUseCase.salvar(receita);

        assertThat(salva.getItens()).extracting(ItemReceita::getUsoContinuo)
                .containsExactly(true, false);
    }

    @Test
    @DisplayName("Reaproveita o medicamento já cadastrado em vez de duplicar no catálogo")
    void deveReaproveitarMedicamentoExistente() {
        Medicamento existente = new Medicamento();
        existente.setId(7L);
        existente.setNomeMedicamento("Losartana 50mg");

        Receita receita = new Receita();
        receita.setIdPaciente(1L);
        receita.setItens(List.of(item("Losartana 50mg", "1 pela manhã", true)));

        when(medicamentoRepository.buscarPorNome("Losartana 50mg")).thenReturn(Optional.of(existente));
        when(receitaRepository.salvar(any())).thenAnswer(chamada -> chamada.getArgument(0));

        Receita salva = receitaUseCase.salvar(receita);

        assertThat(salva.getItens().get(0).getMedicamento().getId()).isEqualTo(7L);
    }
}
