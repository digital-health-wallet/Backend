package br.com.healthwallet.web.dto;

/** Anexo digitalizado de um exame ou receita, em Base64 (RF11). */
public record UploadRequest(Long id, String base64) {
}
