package br.com.healthwallet.web.dto;

import br.com.healthwallet.domain.model.Medicamento;

public record MedicamentoResponse(
        Long id,
        String nomeMedicamento,
        String laboratorio,
        String feedback
) {
    public static MedicamentoResponse from(Medicamento medicamento) {
        if (medicamento == null) return null;

        return new MedicamentoResponse(
                medicamento.getId(),
                medicamento.getNomeMedicamento(),
                medicamento.getLaboratorio(),
                medicamento.getFeedback()
        );
    }
}
