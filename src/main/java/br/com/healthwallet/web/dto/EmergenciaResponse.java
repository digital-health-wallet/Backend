package br.com.healthwallet.web.dto;

import java.util.List;

/**
 * UC06 - Acessar Ficha via QR Code. Exibe estritamente dados vitais
 * de emergência (RF13): tipo sanguíneo, alergias, diagnósticos crônicos
 * e medicamentos de uso contínuo. Nenhum outro dado do paciente é exposto.
 */
public record EmergenciaResponse(
        String nome,
        String tipoSanguineo,
        List<AlergiaResumo> alergias,
        List<DiagnosticoResumo> diagnosticosCronicos,
        List<MedicamentoResumo> medicamentosUsoContinuo
) {
    public record AlergiaResumo(String tipo, String descricao) {
    }

    public record DiagnosticoResumo(String nome, String cid, String descricao) {
    }

    public record MedicamentoResumo(String nomeMedicamento, String posologia) {
    }
}
