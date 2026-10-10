package br.com.healthwallet.web.controller;

import br.com.healthwallet.application.usecase.CadastrarProntuarioUseCase;
import br.com.healthwallet.application.usecase.PacienteComAlergia;
import br.com.healthwallet.application.usecase.PacienteUseCase;
import br.com.healthwallet.domain.model.Alergia;
import br.com.healthwallet.domain.model.ItemReceita;
import br.com.healthwallet.domain.model.Medicamento;
import br.com.healthwallet.domain.model.Paciente;
import br.com.healthwallet.infrastructure.security.AuthenticatedUser;
import br.com.healthwallet.web.dto.AlergiaRequest;
import br.com.healthwallet.web.dto.CadastroProntuarioRequest;
import br.com.healthwallet.web.dto.PacienteResponse;
import br.com.healthwallet.web.dto.PacienteUpdateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * UC07 - Cadastrar e Gerenciar Perfil de Paciente. Todas as operações são
 * restritas ao Usuário autenticado (modo cuidador: um Usuário pode gerenciar
 * vários Pacientes - o próprio e/ou dependentes).
 */
@RestController
@RequestMapping("/api/pacientes")
@RequiredArgsConstructor
public class PacienteController {

    private final CadastrarProntuarioUseCase cadastrarProntuarioUseCase;
    private final PacienteUseCase pacienteUseCase;

    @PostMapping("/prontuario")
    public ResponseEntity<PacienteResponse> salvarProntuarioCompleto(@RequestBody @Valid CadastroProntuarioRequest request) {
        Paciente pacienteCriado = cadastrarProntuarioUseCase.executar(
                toModel(request),
                AuthenticatedUser.idOuFalhar(),
                Boolean.TRUE.equals(request.possuiAlergia()) ? toAlergias(request.alergias()) : List.of(),
                Boolean.TRUE.equals(request.usaMedicamentoContinuo())
                        ? toMedicamentos(request.medicamentosContinuos()) : List.of());
        PacienteComAlergia detalhado = pacienteUseCase.buscarDetalhadoDoUsuario(pacienteCriado.getId(), AuthenticatedUser.idOuFalhar());
        return ResponseEntity.status(HttpStatus.CREATED).body(PacienteResponse.from(detalhado));
    }

    @GetMapping("/meus")
    public ResponseEntity<List<PacienteResponse>> listarMeusPacientes() {
        Long idUsuario = AuthenticatedUser.idOuFalhar();
        return ResponseEntity.ok(pacienteUseCase.listarDoUsuario(idUsuario).stream()
                .map(paciente -> PacienteResponse.from(
                        pacienteUseCase.buscarDetalhadoDoUsuario(paciente.getId(), idUsuario)))
                .toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PacienteResponse> buscarPorId(@PathVariable Long id) {
        PacienteComAlergia detalhado = pacienteUseCase.buscarDetalhadoDoUsuario(id, AuthenticatedUser.idOuFalhar());
        return ResponseEntity.ok(PacienteResponse.from(detalhado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PacienteResponse> atualizar(@PathVariable Long id, @Valid @RequestBody PacienteUpdateRequest request) {
        Paciente dados = new Paciente();
        dados.setNome(request.nome());
        dados.setCpf(request.cpf());
        dados.setDataNascimento(request.dataNascimento());
        dados.setTipoSanguineo(request.tipoSanguineo());
        dados.setFichaEmergencialAtiva(request.fichaEmergencialAtiva());

        List<Alergia> alergias = Boolean.TRUE.equals(request.possuiAlergia())
                ? toAlergias(request.alergias()) : List.of();

        PacienteComAlergia detalhado = pacienteUseCase.atualizar(
                id, AuthenticatedUser.idOuFalhar(), dados, alergias);
        return ResponseEntity.ok(PacienteResponse.from(detalhado));
    }

    @PatchMapping("/{id}/inativar")
    public ResponseEntity<Void> inativar(@PathVariable Long id) {
        pacienteUseCase.inativar(id, AuthenticatedUser.idOuFalhar());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/medicamentos-continuos")
    public ResponseEntity<PacienteResponse> adicionarMedicamentosContinuos(
            @PathVariable Long id,
            @RequestBody List<CadastroProntuarioRequest.MedicamentoContinuoRequest> medicamentos) {

        // Garante que o paciente pertence ao usuário autenticado antes de gravar.
        pacienteUseCase.buscarDoUsuario(id, AuthenticatedUser.idOuFalhar());

        cadastrarProntuarioUseCase.adicionarMedicamentosContinuos(id, toMedicamentos(medicamentos));

        PacienteComAlergia detalhado = pacienteUseCase.buscarDetalhadoDoUsuario(id, AuthenticatedUser.idOuFalhar());
        return ResponseEntity.ok(PacienteResponse.from(detalhado));
    }

    private Paciente toModel(CadastroProntuarioRequest request) {
        Paciente paciente = new Paciente();
        paciente.setCpf(request.cpf());
        paciente.setNome(request.nome());
        paciente.setDataNascimento(request.dataNascimento());
        paciente.setTipoSanguineo(request.tipoSanguineo());
        paciente.setFichaEmergencialAtiva(request.fichaEmergencialAtiva());
        return paciente;
    }

    /** Descarta as linhas em branco que a tela envia quando um campo fica vazio. */
    private List<Alergia> toAlergias(List<AlergiaRequest> informadas) {
        if (informadas == null) return List.of();

        return informadas.stream()
                .filter(a -> a.descricao() != null && !a.descricao().isBlank())
                .map(a -> {
                    Alergia alergia = new Alergia();
                    alergia.setId(a.id());
                    alergia.setTipo(a.tipo());
                    alergia.setDescricao(a.descricao());
                    return alergia;
                })
                .toList();
    }

    private List<ItemReceita> toMedicamentos(List<CadastroProntuarioRequest.MedicamentoContinuoRequest> informados) {
        if (informados == null) return List.of();

        return informados.stream().map(med -> {
            Medicamento medicamento = new Medicamento();
            medicamento.setNomeMedicamento(med.nome());

            ItemReceita item = new ItemReceita();
            item.setMedicamento(medicamento);
            item.setPosologia(med.posologia());
            item.setUsoContinuo(true);
            return item;
        }).toList();
    }
}
