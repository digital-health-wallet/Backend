package br.com.healthwallet.domain.model;

import java.time.LocalDate;

public class Paciente {
    private Long id;
    private Long idUsuario;
    private String nome;
    private String cpf;
    private LocalDate dataNascimento;
    private String tipoSanguineo;
    private String codigoEmergencia;
    private Boolean fichaEmergencialAtiva;
    private Boolean ativo;
    private String googleCalendarId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getTipoSanguineo() {
        return tipoSanguineo;
    }

    public void setTipoSanguineo(String tipoSanguineo) {
        this.tipoSanguineo = tipoSanguineo;
    }

    public String getCodigoEmergencia() {
        return codigoEmergencia;
    }

    public void setCodigoEmergencia(String codigoEmergencia) {
        this.codigoEmergencia = codigoEmergencia;
    }

    public Boolean getFichaEmergencialAtiva() {
        return fichaEmergencialAtiva;
    }

    public void setFichaEmergencialAtiva(Boolean fichaEmergencialAtiva) {
        this.fichaEmergencialAtiva = fichaEmergencialAtiva;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public String getGoogleCalendarId() {
        return googleCalendarId;
    }

    public void setGoogleCalendarId(String googleCalendarId) {
        this.googleCalendarId = googleCalendarId;
    }
}
