package br.com.healthwallet.infrastructure.persistence.mapper;


import br.com.healthwallet.domain.model.Endereco;
import br.com.healthwallet.domain.model.Profissional;
import br.com.healthwallet.infrastructure.persistence.entity.EnderecoEntity;
import br.com.healthwallet.infrastructure.persistence.entity.ProfissionalEntity;
import org.springframework.stereotype.Component;

@Component
public class ProfissionalMapper {

    public Profissional toDomain(ProfissionalEntity entity) {
        if (entity == null) return null;
        Profissional domain = new Profissional();
        domain.setId(entity.getId());
        domain.setNomeProfissional(entity.getNomeProfissional());
        domain.setNumeroIdentificacaoProfissional(entity.getNumeroIdentificacaoProfissional());
        domain.setContato(entity.getContato());
        domain.setNomeClinica(entity.getNomeClinica());
        domain.setEmail(entity.getEmail());
        domain.setEspecialidade(entity.getEspecialidade());

        if (entity.getEndereco() != null) {
            Endereco endereco = new Endereco();
            endereco.setId(entity.getEndereco().getId());
            endereco.setCep(entity.getEndereco().getCep());
            endereco.setLogradouro(entity.getEndereco().getLogradouro());
            endereco.setNumero(entity.getEndereco().getNumero());
            endereco.setBairro(entity.getEndereco().getBairro());
            endereco.setCidade(entity.getEndereco().getCidade());
            endereco.setEstado(entity.getEndereco().getEstado());
            endereco.setComplemento(entity.getEndereco().getComplemento());
            domain.setEndereco(endereco);
        }

        return domain;
    }

    public ProfissionalEntity toEntity(Profissional domain) {
        if (domain == null) return null;

        EnderecoEntity enderecoEntity = null;
        if (domain.getEndereco() != null) {
            enderecoEntity = EnderecoEntity.builder()
                    .id(domain.getEndereco().getId())
                    .cep(domain.getEndereco().getCep())
                    .logradouro(domain.getEndereco().getLogradouro())
                    .numero(domain.getEndereco().getNumero())
                    .bairro(domain.getEndereco().getBairro())
                    .cidade(domain.getEndereco().getCidade())
                    .estado(domain.getEndereco().getEstado())
                    .complemento(domain.getEndereco().getComplemento())
                    .build();
        }

        return ProfissionalEntity.builder()
                .id(domain.getId())
                .nomeProfissional(domain.getNomeProfissional())
                .numeroIdentificacaoProfissional(domain.getNumeroIdentificacaoProfissional())
                .contato(domain.getContato())
                .nomeClinica(domain.getNomeClinica())
                .email(domain.getEmail())
                .especialidade(domain.getEspecialidade())
                .endereco(enderecoEntity)
                .build();
    }
}