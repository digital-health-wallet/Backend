package br.com.healthwallet.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "item_receita")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemReceitaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_item_receita")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_receita", nullable = false)
    private ReceitaEntity receita;

    // CascadeType.PERSIST garante que se o medicamento for novo, ele entra no banco automaticamente
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "id_medicamento", nullable = false)
    private MedicamentoEntity medicamento;

    @Column(columnDefinition = "TEXT")
    private String posologia;

    @Column(name = "uso_continuo")
    private Boolean usoContinuo;
}