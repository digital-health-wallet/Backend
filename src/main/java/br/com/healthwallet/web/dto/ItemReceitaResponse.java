package br.com.healthwallet.web.dto;

import br.com.healthwallet.domain.model.ItemReceita;

public record ItemReceitaResponse(
        Long id,
        Long idReceita,
        MedicamentoResponse medicamento,
        String posologia,
        Boolean usoContinuo
) {
    public static ItemReceitaResponse from(ItemReceita item) {
        if (item == null) return null;

        return new ItemReceitaResponse(
                item.getId(),
                item.getIdReceita(),
                MedicamentoResponse.from(item.getMedicamento()),
                item.getPosologia(),
                item.getUsoContinuo()
        );
    }
}
