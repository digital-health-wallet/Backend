package br.com.healthwallet.domain.model;

import br.com.healthwallet.domain.model.enums.TipoAlergia;

public class Alergia {
    private Long id;
    private Long idPaciente;    // Referência ao paciente
    private Long idMedicamento; // Referência ao medicamento (opcional/futuro)
    private TipoAlergia tipo;
    private String descricao;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getIdPaciente() { return idPaciente; }
    public void setIdPaciente(Long idPaciente) { this.idPaciente = idPaciente; }

    public Long getIdMedicamento() { return idMedicamento; }
    public void setIdMedicamento(Long idMedicamento) { this.idMedicamento = idMedicamento; }

    public TipoAlergia getTipo() { return tipo; }
    public void setTipo(TipoAlergia tipo) { this.tipo = tipo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
}
