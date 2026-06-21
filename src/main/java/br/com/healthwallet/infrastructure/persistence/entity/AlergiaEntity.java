package br.com.healthwallet.infrastructure.persistence.entity;


import br.com.healthwallet.domain.model.enums.TipoAlergia;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "alergias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlergiaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_alergia")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_paciente", nullable = false)
    private PacienteEntity paciente;

    // Deixei comentado caso ainda venha a implementar a entidade MedicamentoEntity futuramente
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "id_medicamento")
    // private MedicamentoEntity medicamento;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", length = 1, nullable = false)
    private TipoAlergia tipo;

    @Column(name = "descricao", length = 50)
    private String descricao;
}
