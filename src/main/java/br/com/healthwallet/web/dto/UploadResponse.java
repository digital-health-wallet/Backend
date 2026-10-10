package br.com.healthwallet.web.dto;

import br.com.healthwallet.domain.model.Upload;

public record UploadResponse(Long id, String base64) {

    public static UploadResponse from(Upload upload) {
        if (upload == null) return null;
        return new UploadResponse(upload.getId(), upload.getBase64());
    }
}
