package br.com.healthwallet.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "medicamentos")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicamentoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_medicamento")
    private Long id;

    @Column(name = "nome_medicamento", length = 50, nullable = false)
    private String nomeMedicamento;

    @Column(length = 50)
    private String laboratorio;

    @Column(columnDefinition = "TEXT")
    private String feedback;
}