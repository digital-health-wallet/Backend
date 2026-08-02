package br.com.healthwallet.infrastructure.persistence.mapper;

import br.com.healthwallet.domain.model.Usuario;
import br.com.healthwallet.infrastructure.persistence.entity.UsuarioEntity;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public Usuario toDomain(UsuarioEntity entity) {
        if (entity == null) return null;

        Usuario domain = new Usuario();
        domain.setId(entity.getId());
        domain.setEmail(entity.getEmail());
        domain.setAtivo(entity.getAtivo());
        domain.setDataDesativacao(entity.getDataDesativacao());
        domain.setGoogleAccessToken(entity.getGoogleAccessToken());
        domain.setGoogleRefreshToken(entity.getGoogleRefreshToken());
        domain.setGoogleTokenExpiration(entity.getGoogleTokenExpiration());
        return domain;
    }

    public UsuarioEntity toEntity(Usuario domain) {
        if (domain == null) return null;

        return UsuarioEntity.builder()
                .id(domain.getId())
                .email(domain.getEmail())
                .ativo(domain.getAtivo())
                .dataDesativacao(domain.getDataDesativacao())
                .googleAccessToken(domain.getGoogleAccessToken())
                .googleRefreshToken(domain.getGoogleRefreshToken())
                .googleTokenExpiration(domain.getGoogleTokenExpiration())
                .build();
    }
}
