package br.com.healthwallet.domain.model;

public class Medicamento {
    private Long id;
    private String nomeMedicamento;
    private String laboratorio;
    private String feedback;

    public Medicamento() {}

    public Medicamento(Long id, String nomeMedicamento, String laboratorio, String feedback) {
        this.id = id;
        this.nomeMedicamento = nomeMedicamento;
        this.laboratorio = laboratorio;
        this.feedback = feedback;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNomeMedicamento() { return nomeMedicamento; }
    public void setNomeMedicamento(String nomeMedicamento) { this.nomeMedicamento = nomeMedicamento; }

    public String getLaboratorio() { return laboratorio; }
    public void setLaboratorio(String laboratorio) { this.laboratorio = laboratorio; }

    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }
}