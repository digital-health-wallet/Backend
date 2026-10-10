package br.com.healthwallet.web.dto;

import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.List;

public record ReceitaRequest(
        Long idAgendamento,
        Long idPaciente,
        LocalDateTime dataEmissao,
        String orientacoesGerais,
        @Valid List<ItemReceitaRequest> itens,
        List<UploadRequest> uploads,
        Boolean origemProntuario
) {
}
