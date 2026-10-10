package br.com.healthwallet.web.dto;

import br.com.healthwallet.domain.model.Diagnostico;

public record DiagnosticoResponse(
        Long id,
        Long idAgendamento,
        Long idPaciente,
        String nome,
        String cid,
        String descricao,
        Boolean doencaCronica,
        Boolean ativo
) {
    public static DiagnosticoResponse from(Diagnostico diagnostico) {
        if (diagnostico == null) return null;

        return new DiagnosticoResponse(
                diagnostico.getId(),
                diagnostico.getIdAgendamento(),
                diagnostico.getIdPaciente(),
                diagnostico.getNome(),
                diagnostico.getCid(),
                diagnostico.getDescricao(),
                diagnostico.getDoencaCronica(),
                diagnostico.getAtivo()
        );
    }
}
