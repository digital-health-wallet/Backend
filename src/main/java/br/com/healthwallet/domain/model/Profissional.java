package br.com.healthwallet.domain.model;

public class Profissional {
    private Long id;
    private Endereco endereco;
    private String nomeProfissional;
    private String numeroIdentificacaoProfissional;
    private String contato;
    private String nomeClinica;
    private String email;
    private String especialidade;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Endereco getEndereco() { return endereco; }
    public void setEndereco(Endereco endereco) { this.endereco = endereco; }

    public String getNomeProfissional() { return nomeProfissional; }
    public void setNomeProfissional(String nomeProfissional) { this.nomeProfissional = nomeProfissional; }

    public String getNumeroIdentificacaoProfissional() { return numeroIdentificacaoProfissional; }
    public void setNumeroIdentificacaoProfissional(String numeroIdentificacaoProfissional) { this.numeroIdentificacaoProfissional = numeroIdentificacaoProfissional; }

    public String getContato() { return contato; }
    public void setContato(String contato) { this.contato = contato; }

    public String getNomeClinica() { return nomeClinica; }
    public void setNomeClinica(String nomeClinica) { this.nomeClinica = nomeClinica; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getEspecialidade() { return especialidade; }
    public void setEspecialidade(String especialidade) { this.especialidade = especialidade; }
}
