package br.com.healthwallet.domain.model;

public class Upload {
    private Long id;
    private Long idExame;
    private Long idReceita;
    private String base64;

    public Upload() {}

    public Upload(Long id, Long idExame, Long idReceita, String base64) {
        this.id = id;
        this.idExame = idExame;
        this.idReceita = idReceita;
        this.base64 = base64;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getIdExame() { return idExame; }
    public void setIdExame(Long idExame) { this.idExame = idExame; }

    public Long getIdReceita() { return idReceita; }
    public void setIdReceita(Long idReceita) { this.idReceita = idReceita; }

    public String getBase64() { return base64; }
    public void setBase64(String base64) { this.base64 = base64; }
}