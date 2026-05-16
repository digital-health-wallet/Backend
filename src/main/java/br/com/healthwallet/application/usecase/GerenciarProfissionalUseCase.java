package br.com.healthwallet.application.usecase;

import br.com.healthwallet.domain.model.Endereco;
import br.com.healthwallet.domain.model.Profissional;
import br.com.healthwallet.domain.repository.ProfissionalRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GerenciarProfissionalUseCase {

    private final ProfissionalRepository profissionalRepository;

    public List<Profissional> listarTodos() {
        return profissionalRepository.listarTodos();
    }

    public Profissional buscarPorId(Long id) {
        return profissionalRepository.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Profissional não encontrado com id: " + id));
    }

    @Transactional
    public void deletar(Long id) {
        buscarPorId(id);
        profissionalRepository.deletar(id);
    }

    @Transactional
    public Profissional salvarOuAtualizar(Profissional profissional) {
        if (profissional.getId() != null) {
            Profissional existente = profissionalRepository.buscarPorId(profissional.getId())
                    .orElseThrow(() -> new RuntimeException("Profissional não encontrado com id: " + profissional.getId()));

            existente.setNomeProfissional(profissional.getNomeProfissional());
            existente.setEspecialidade(profissional.getEspecialidade());
            existente.setContato(profissional.getContato());
            existente.setEmail(profissional.getEmail());
            existente.setNomeClinica(profissional.getNomeClinica());
            existente.setNumeroIdentificacaoProfissional(profissional.getNumeroIdentificacaoProfissional());

            if (profissional.getEndereco() != null) {
                if (existente.getEndereco() == null) {
                    existente.setEndereco(new Endereco());
                }

                Endereco endExistente = existente.getEndereco();
                Endereco endNovo = profissional.getEndereco();

                endExistente.setCep(endNovo.getCep());
                endExistente.setLogradouro(endNovo.getLogradouro());
                endExistente.setNumero(endNovo.getNumero());
                endExistente.setBairro(endNovo.getBairro());
                endExistente.setCidade(endNovo.getCidade());
                endExistente.setEstado(endNovo.getEstado());
                endExistente.setComplemento(endNovo.getComplemento());
            }

            return profissionalRepository.salvar(existente);
        }

        return profissionalRepository.salvar(profissional);
    }

    /**
     * Regra de Negócio: Verifica se o médico já existe na base antes de cadastrar.
     * Ideal para ser chamado pela sincronização do Google Calendar ou criação de novo agendamento.
     */
    @Transactional
    public void autoCadastrarSeNaoExistir(String nome, String especialidade, String clinica) {
        if (nome != null && !nome.trim().isEmpty()) {
            boolean existe = profissionalRepository.buscarPorNome(nome).isPresent();

            if (!existe) {
                // Usando a classe de Domínio Pura, instanciada com 'new' e 'setters'
                Profissional novo = new Profissional();
                novo.setNomeProfissional(nome);
                novo.setEspecialidade(especialidade);
                novo.setNomeClinica(clinica);

                profissionalRepository.salvar(novo);
            }
        }
    }
}