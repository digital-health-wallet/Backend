package br.com.healthwallet.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "contatos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContatoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_contato")
    private Long id;

    @Column(name = "nome", length = 50)
    private String nome;

    @Column(name = "parentesco", length = 50)
    private String parentesco;

    @Column(name = "telefone", length = 20)
    private String telefone;

    @Column(name = "email", length = 50)
    private String email;
}
