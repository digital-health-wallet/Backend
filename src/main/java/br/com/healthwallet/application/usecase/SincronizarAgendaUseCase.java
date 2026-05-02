package br.com.healthwallet.application.usecase;

import br.com.healthwallet.domain.model.Agendamento;
import br.com.healthwallet.domain.repository.AgendamentoRepository;
import br.com.healthwallet.domain.repository.GoogleCalendarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SincronizarAgendaUseCase {

    private final GoogleCalendarRepository googleRepository;
    private final AgendamentoRepository localRepository;

    public List<Agendamento> buscarSugestoes(Long idPaciente, String accessToken) {
        List<Agendamento> eventosGoogle = googleRepository.buscarEventosDaAgenda(accessToken);

        return eventosGoogle.stream()
                .filter(this::pareceSerEventoSaude)
                .peek(agendamento -> agendamento.setIdPaciente(idPaciente))
                .collect(Collectors.toList());
    }

    @Transactional
    public void salvarSelecionados(List<Agendamento> selecionados) {
        selecionados.forEach(localRepository::salvar);
    }

    /**
     * Regra de Negócio: Whitelist. Só deixa passar o que for da área da saúde!
     */
    private boolean pareceSerEventoSaude(Agendamento a) {
        String texto = "";
        if (a.getEspecialidade() != null) texto += a.getEspecialidade().toLowerCase() + " ";
        if (a.getNomeClinica() != null) texto += a.getNomeClinica().toLowerCase();

        if (texto.contains("parabéns") || texto.contains("aniversário") || texto.contains("feriado")) {
            return false;
        }

        boolean temPalavraMedica = texto.contains("exame") ||
                texto.contains("consulta") ||
                texto.contains("médic") ||
                texto.contains("dr") ||
                texto.contains("retorno") ||
                texto.contains("dentista") ||
                texto.contains("terapia") ||
                texto.contains("sangue") ||
                texto.contains("clínica") ||
                texto.contains("hospital");

        return temPalavraMedica;
    }
}