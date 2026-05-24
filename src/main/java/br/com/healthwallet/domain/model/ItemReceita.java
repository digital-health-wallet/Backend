package br.com.healthwallet.domain.model;

public class ItemReceita {
    private Long id;
    private Long idReceita;
    private Medicamento medicamento;
    private String posologia;
    private Boolean usoContinuo;

    public ItemReceita() {}

    public ItemReceita(Long id, Long idReceita, Medicamento medicamento, String posologia, Boolean usoContinuo) {
        this.id = id;
        this.idReceita = idReceita;
        this.medicamento = medicamento;
        this.posologia = posologia;
        this.usoContinuo = usoContinuo;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getIdReceita() { return idReceita; }
    public void setIdReceita(Long idReceita) { this.idReceita = idReceita; }

    public Medicamento getMedicamento() { return medicamento; }
    public void setMedicamento(Medicamento medicamento) { this.medicamento = medicamento; }

    public String getPosologia() { return posologia; }
    public void setPosologia(String posologia) { this.posologia = posologia; }

    public Boolean getUsoContinuo() { return usoContinuo; }
    public void setUsoContinuo(Boolean usoContinuo) { this.usoContinuo = usoContinuo; }
}