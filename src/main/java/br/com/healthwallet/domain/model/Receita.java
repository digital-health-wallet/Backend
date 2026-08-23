package br.com.healthwallet.domain.model;

import java.time.LocalDateTime;
import java.util.List;

public class Receita {
    private Long id;
    private Long idAgendamento; // Pode ser null para receitas avulsas
    private Long idPaciente;
    private LocalDateTime dataEmissao;
    private String orientacoesGerais;
    private List<ItemReceita> itens;
    private List<Upload> uploads;
    private Boolean origemProntuario;
    private Boolean ativo;

    public Receita() {}

    public Receita(Long id, Long idAgendamento, LocalDateTime dataEmissao, String orientacoesGerais, List<ItemReceita> itens, List<Upload> uploads) {
        this.id = id;
        this.idAgendamento = idAgendamento;
        this.dataEmissao = dataEmissao;
        this.orientacoesGerais = orientacoesGerais;
        this.itens = itens;
        this.uploads = uploads;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getIdAgendamento() { return idAgendamento; }
    public void setIdAgendamento(Long idAgendamento) { this.idAgendamento = idAgendamento; }

    public LocalDateTime getDataEmissao() { return dataEmissao; }
    public void setDataEmissao(LocalDateTime dataEmissao) { this.dataEmissao = dataEmissao; }

    public String getOrientacoesGerais() { return orientacoesGerais; }
    public void setOrientacoesGerais(String orientacoesGerais) { this.orientacoesGerais = orientacoesGerais; }

    public List<ItemReceita> getItens() { return itens; }
    public void setItens(List<ItemReceita> itens) { this.itens = itens; }

    public List<Upload> getUploads() { return uploads; }
    public void setUploads(List<Upload> uploads) { this.uploads = uploads; }

    public Boolean getOrigemProntuario() { return origemProntuario; }
    public void setOrigemProntuario(Boolean origemProntuario) { this.origemProntuario = origemProntuario; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }

    public Long getIdPaciente() { return idPaciente; }
    public void setIdPaciente(Long idPaciente) { this.idPaciente = idPaciente; }
}