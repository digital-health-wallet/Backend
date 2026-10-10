package br.com.healthwallet.web.dto;

import br.com.healthwallet.domain.model.Receita;

import java.time.LocalDateTime;
import java.util.List;

public record ReceitaResponse(
        Long id,
        Long idAgendamento,
        Long idPaciente,
        LocalDateTime dataEmissao,
        String orientacoesGerais,
        List<ItemReceitaResponse> itens,
        List<UploadResponse> uploads,
        Boolean origemProntuario,
        Boolean ativo
) {
    public static ReceitaResponse from(Receita receita) {
        if (receita == null) return null;

        return new ReceitaResponse(
                receita.getId(),
                receita.getIdAgendamento(),
                receita.getIdPaciente(),
                receita.getDataEmissao(),
                receita.getOrientacoesGerais(),
                receita.getItens() == null ? List.of()
                        : receita.getItens().stream().map(ItemReceitaResponse::from).toList(),
                receita.getUploads() == null ? List.of()
                        : receita.getUploads().stream().map(UploadResponse::from).toList(),
                receita.getOrigemProntuario(),
                receita.getAtivo()
        );
    }
}
