package br.com.healthwallet.web.dto;

import br.com.healthwallet.domain.model.Exame;

import java.time.LocalDate;
import java.util.List;

public record ExameResponse(
        Long id,
        Long idAgendamento,
        Long idPaciente,
        String nomeExame,
        LocalDate dataHoraExame,
        String observacoes,
        List<UploadResponse> uploads,
        Boolean ativo
) {
    public static ExameResponse from(Exame exame) {
        if (exame == null) return null;

        return new ExameResponse(
                exame.getId(),
                exame.getIdAgendamento(),
                exame.getIdPaciente(),
                exame.getNomeExame(),
                exame.getDataHoraExame(),
                exame.getObservacoes(),
                exame.getUploads() == null ? List.of()
                        : exame.getUploads().stream().map(UploadResponse::from).toList(),
                exame.getAtivo()
        );
    }
}
