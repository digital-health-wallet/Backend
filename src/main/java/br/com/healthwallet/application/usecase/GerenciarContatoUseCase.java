package br.com.healthwallet.application.usecase;

import br.com.healthwallet.domain.model.Contato;
import br.com.healthwallet.domain.repository.ContatoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GerenciarContatoUseCase {

    private final ContatoRepository contatoRepository;

    public List<Contato> listarTodos() {
        return contatoRepository.listarTodos();
    }

    public Contato buscarPorId(Long id) {
        return contatoRepository.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Contato não encontrado com id: " + id));
    }

    @Transactional
    public void deletar(Long id) {
        buscarPorId(id);
        contatoRepository.deletar(id);
    }

    @Transactional
    public Contato salvarOuAtualizar(Contato contato) {
        if (contato.getId() != null) {
            Contato existente = buscarPorId(contato.getId());

            existente.setNome(contato.getNome());
            existente.setParentesco(contato.getParentesco());
            existente.setTelefone(contato.getTelefone());
            existente.setEmail(contato.getEmail());

            return contatoRepository.salvar(existente);
        }

        return contatoRepository.salvar(contato);
    }
}
