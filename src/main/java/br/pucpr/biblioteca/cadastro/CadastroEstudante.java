package br.pucpr.biblioteca.cadastro;

import br.pucpr.biblioteca.dominio.Estudante;
import br.pucpr.biblioteca.dominio.Pessoa;
import br.pucpr.biblioteca.dominio.RegraDeNegocioException;
import br.pucpr.biblioteca.persistencia.RepositorioRelacionamento;
import java.util.Optional;
import java.util.function.Consumer;

public class CadastroEstudante extends CadastroTemplate<DadosEstudante, Estudante> {
    private final RepositorioRelacionamento relacionamento;
    private final Consumer<String> saida;

    public CadastroEstudante(RepositorioRelacionamento relacionamento, Consumer<String> saida) {
        this.relacionamento = relacionamento;
        this.saida = saida;
    }

    protected Optional<Estudante> buscarExistente(DadosEstudante d) {
        return relacionamento.buscarEstudante(d.matricula());
    }

    protected void validar(DadosEstudante d) {
        if (d.matricula() == null || d.matricula().isBlank()) {
            throw new RegraDeNegocioException("matricula obrigatoria");
        }
    }

    // a pessoa precisa existir antes de qualquer gravacao
    protected void verificarPreRequisitos(DadosEstudante d) {
        if (relacionamento.buscarPessoa(d.cpf()).isEmpty()) {
            throw new RegraDeNegocioException("cpf nao cadastrado: " + d.cpf());
        }
    }

    protected Estudante criar(DadosEstudante d) {
        Pessoa pessoa = relacionamento.buscarPessoa(d.cpf()).orElseThrow();
        return new Estudante(d.matricula(), pessoa);
    }

    protected void armazenar(Estudante e) { relacionamento.salvarEstudante(e); }

    protected void apresentar(Estudante e) {
        saida.accept("Estudante " + e.getMatricula() + " - " + e.getPessoa().getNome());
    }
}
