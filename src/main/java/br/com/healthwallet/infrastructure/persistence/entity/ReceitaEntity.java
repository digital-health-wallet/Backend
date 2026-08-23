package br.com.healthwallet.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "receitas")
@Getter
@Setter
@Builder // Adicionado
@NoArgsConstructor // Adicionado para o JPA não quebrar
@AllArgsConstructor
public class ReceitaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_receita")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_agendamento", nullable = true)
    private AgendamentoEntity agendamento;

    // Vínculo direto com o paciente: preenchido sempre, inclusive quando a receita
    // é avulsa (sem agendamento).
    @Column(name = "id_paciente")
    private Long idPaciente;

    @Column(name = "data_emissao")
    private LocalDateTime dataEmissao;

    @Column(name = "orientacoes_gerais", columnDefinition = "TEXT")
    private String orientacoesGerais;

    // Receita criada automaticamente pelo cadastro do prontuário (medicamento de uso
    // contínuo informado ali) — não deve aparecer na listagem de Documentos.
    @Builder.Default
    @Column(name = "origem_prontuario")
    private Boolean origemProntuario = false;

    @Builder.Default
    @Column(name = "ativo")
    private Boolean ativo = true;

    @Builder.Default
    @OneToMany(mappedBy = "receita", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemReceitaEntity> itens = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "receita", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UploadEntity> uploads = new ArrayList<>();
}