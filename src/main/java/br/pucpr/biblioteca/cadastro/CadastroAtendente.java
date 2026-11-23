package br.pucpr.biblioteca.cadastro;

import br.pucpr.biblioteca.dominio.Atendente;
import br.pucpr.biblioteca.dominio.Pessoa;
import br.pucpr.biblioteca.dominio.RegraDeNegocioException;
import br.pucpr.biblioteca.persistencia.RepositorioRelacionamento;
import java.util.Optional;
import java.util.function.Consumer;

public class CadastroAtendente extends CadastroTemplate<DadosAtendente, Atendente> {
    private final RepositorioRelacionamento relacionamento;
    private final Consumer<String> saida;

    public CadastroAtendente(RepositorioRelacionamento relacionamento, Consumer<String> saida) {
        this.relacionamento = relacionamento;
        this.saida = saida;
    }

    protected Optional<Atendente> buscarExistente(DadosAtendente d) {
        return relacionamento.buscarAtendente(d.matriculaFuncional());
    }

    protected void validar(DadosAtendente d) {
        if (d.matriculaFuncional() == null || d.matriculaFuncional().isBlank()) {
            throw new RegraDeNegocioException("matricula funcional obrigatoria");
        }
    }

    protected void verificarPreRequisitos(DadosAtendente d) {
        if (relacionamento.buscarPessoa(d.cpf()).isEmpty()) {
            throw new RegraDeNegocioException("cpf nao cadastrado: " + d.cpf());
        }
    }

    protected Atendente criar(DadosAtendente d) {
        Pessoa pessoa = relacionamento.buscarPessoa(d.cpf()).orElseThrow();
        return new Atendente(d.matriculaFuncional(), pessoa);
    }

    protected void armazenar(Atendente a) { relacionamento.salvarAtendente(a); }

    protected void apresentar(Atendente a) {
        saida.accept("Atendente " + a.getMatriculaFuncional() + " - " + a.getPessoa().getNome());
    }
}
