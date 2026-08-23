package br.com.healthwallet.domain.model;

import java.time.LocalDate;
import java.util.List;

public class Exame {
    private Long id;
    private Long idAgendamento; // Pode ser null para exames avulsos
    private Long idPaciente;
    private String nomeExame;
    private LocalDate dataHoraExame;
    private String observacoes;
    private List<Upload> uploads;
    private Boolean ativo;

    public Exame() {}

    public Exame(Long id, Long idAgendamento, String nomeExame, LocalDate dataHoraExame, String observacoes, List<Upload> uploads) {
        this.id = id;
        this.idAgendamento = idAgendamento;
        this.nomeExame = nomeExame;
        this.dataHoraExame = dataHoraExame;
        this.observacoes = observacoes;
        this.uploads = uploads;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getIdAgendamento() { return idAgendamento; }
    public void setIdAgendamento(Long idAgendamento) { this.idAgendamento = idAgendamento; }

    public String getNomeExame() { return nomeExame; }
    public void setNomeExame(String nomeExame) { this.nomeExame = nomeExame; }

    public LocalDate getDataHoraExame() { return dataHoraExame; }
    public void setDataHoraExame(LocalDate dataHoraExame) { this.dataHoraExame = dataHoraExame; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public List<Upload> getUploads() { return uploads; }
    public void setUploads(List<Upload> uploads) { this.uploads = uploads; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }

    public Long getIdPaciente() { return idPaciente; }
    public void setIdPaciente(Long idPaciente) { this.idPaciente = idPaciente; }
}