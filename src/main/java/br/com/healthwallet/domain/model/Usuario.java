package br.com.healthwallet.domain.model;

import java.time.LocalDateTime;

public class Usuario {
    private Long id;
    private String email;
    private Boolean ativo;
    private LocalDateTime dataDesativacao;
    private String googleAccessToken;
    private String googleRefreshToken;
    private LocalDateTime googleTokenExpiration;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public LocalDateTime getDataDesativacao() {
        return dataDesativacao;
    }

    public void setDataDesativacao(LocalDateTime dataDesativacao) {
        this.dataDesativacao = dataDesativacao;
    }

    public String getGoogleAccessToken() {
        return googleAccessToken;
    }

    public void setGoogleAccessToken(String googleAccessToken) {
        this.googleAccessToken = googleAccessToken;
    }

    public String getGoogleRefreshToken() {
        return googleRefreshToken;
    }

    public void setGoogleRefreshToken(String googleRefreshToken) {
        this.googleRefreshToken = googleRefreshToken;
    }

    public LocalDateTime getGoogleTokenExpiration() {
        return googleTokenExpiration;
    }

    public void setGoogleTokenExpiration(LocalDateTime googleTokenExpiration) {
        this.googleTokenExpiration = googleTokenExpiration;
    }

    public boolean possuiCalendarConectado() {
        return googleRefreshToken != null && !googleRefreshToken.isBlank();
    }
}
