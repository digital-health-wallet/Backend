package br.com.healthwallet.application.usecase;

import br.com.healthwallet.domain.model.Profissional;
// IMPORTANTE: Importar a interface do domínio!
import br.com.healthwallet.domain.repository.ProfissionalRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GerenciarProfissionalUseCase {

    // CORREÇÃO 1: Injetando a interface de domínio, e não a do JPA
    private final ProfissionalRepository profissionalRepository;

    // CORREÇÃO 2: Métodos que o Controller estava pedindo e não existiam aqui

    public List<Profissional> listarTodos() {
        return profissionalRepository.listarTodos();
    }

    public Profissional buscarPorId(Long id) {
        return profissionalRepository.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Profissional não encontrado com id: " + id));
    }

    @Transactional
    public void deletar(Long id) {
        buscarPorId(id); // Garante que existe antes de tentar deletar
        profissionalRepository.deletar(id);
    }

    // Seus métodos originais agora usando o profissionalRepository correto:

    @Transactional
    public Profissional salvarOuAtualizar(Profissional profissional) {
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