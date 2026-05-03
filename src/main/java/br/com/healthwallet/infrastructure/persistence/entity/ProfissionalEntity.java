package br.com.healthwallet.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "profissionais")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfissionalEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_profissional")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "id_endereco")
    private EnderecoEntity endereco;

    @Column(name = "nome_profissional", length = 50)
    private String nomeProfissional;

    @Column(name = "numero_identificacao_profissional", length = 20)
    private String numeroIdentificacaoProfissional;

    @Column(name = "contato", length = 20)
    private String contato;

    @Column(name = "nome_clinica", length = 30)
    private String nomeClinica;

    @Column(name = "email", length = 50)
    private String email;

    @Column(name = "especialidade", length = 70)
    private String especialidade;
}