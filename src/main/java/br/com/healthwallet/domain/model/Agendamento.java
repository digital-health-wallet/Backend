package br.com.healthwallet.domain.model;

import br.com.healthwallet.domain.model.enums.StatusAgendamento;
import br.com.healthwallet.domain.model.enums.TipoConsulta;

import java.time.LocalDate;
import java.time.LocalTime;

public class Agendamento {
    private Long id;
    private Long idPaciente;
    private String especialidade;
    private String nomeClinica;
    private String motivoConsulta;
    private TipoConsulta tipoConsulta;
    private LocalDate dataAgendamento;
    private LocalTime horaAgendamento;
    private LocalTime horaFim;
    private StatusAgendamento status;
    private Boolean favorito;
    private Boolean arquivado;
    private String googleEventId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdPaciente() {
        return idPaciente;
    }

    public void setIdPaciente(Long idPaciente) {
        this.idPaciente = idPaciente;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }

    public String getNomeClinica() {
        return nomeClinica;
    }

    public void setNomeClinica(String nomeClinica) {
        this.nomeClinica = nomeClinica;
    }

    public String getMotivoConsulta() {
        return motivoConsulta;
    }

    public void setMotivoConsulta(String motivoConsulta) {
        this.motivoConsulta = motivoConsulta;
    }

    public TipoConsulta getTipoConsulta() {
        return tipoConsulta;
    }

    public void setTipoConsulta(TipoConsulta tipoConsulta) {
        this.tipoConsulta = tipoConsulta;
    }

    public LocalDate getDataAgendamento() {
        return dataAgendamento;
    }

    public void setDataAgendamento(LocalDate dataAgendamento) {
        this.dataAgendamento = dataAgendamento;
    }

    public LocalTime getHoraAgendamento() {
        return horaAgendamento;
    }

    public void setHoraAgendamento(LocalTime horaAgendamento) {
        this.horaAgendamento = horaAgendamento;
    }

    public LocalTime getHoraFim() {
        return horaFim;
    }

    public void setHoraFim(LocalTime horaFim) {
        this.horaFim = horaFim;
    }

    public StatusAgendamento getStatus() {
        return status;
    }

    public void setStatus(StatusAgendamento status) {
        this.status = status;
    }

    public Boolean getFavorito() {
        return favorito;
    }

    public void setFavorito(Boolean favorito) {
        this.favorito = favorito;
    }

    public Boolean getArquivado() {
        return arquivado;
    }

    public void setArquivado(Boolean arquivado) {
        this.arquivado = arquivado;
    }

    public String getGoogleEventId() {
        return googleEventId;
    }

    public void setGoogleEventId(String googleEventId) {
        this.googleEventId = googleEventId;
    }

    public void arquivar() {
        this.arquivado = true;
    }

    public void alterarStatus(StatusAgendamento novoStatus){
        if (this.status == StatusAgendamento.FINALIZADO){
            throw new IllegalStateException("Não é possível alterar o status de uma consulta finalizada.");
        }
        this.status = novoStatus;
    }

    public void toggleFavorito(){
        this.favorito = !this.favorito;
    }
}
