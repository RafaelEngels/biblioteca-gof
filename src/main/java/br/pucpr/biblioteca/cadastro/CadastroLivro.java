package br.pucpr.biblioteca.cadastro;

import br.pucpr.biblioteca.dominio.Livro;
import br.pucpr.biblioteca.dominio.RegraDeNegocioException;
import br.pucpr.biblioteca.persistencia.RepositorioLivro;
import java.util.Optional;
import java.util.function.Consumer;

public class CadastroLivro extends CadastroTemplate<DadosLivro, Livro> {
    private final RepositorioLivro livros;
    private final Consumer<String> saida;

    public CadastroLivro(RepositorioLivro livros, Consumer<String> saida) {
        this.livros = livros;
        this.saida = saida;
    }

    protected Optional<Livro> buscarExistente(DadosLivro d) { return livros.buscarPorId(d.id()); }

    protected void validar(DadosLivro d) {
        if (d.id() == null || d.id().isBlank() || d.titulo() == null || d.titulo().isBlank()) {
            throw new RegraDeNegocioException("livro precisa de id e titulo");
        }
    }

    protected void verificarPreRequisitos(DadosLivro d) {
        // livro nao depende de ninguem
    }

    protected Livro criar(DadosLivro d) { return new Livro(d.id(), d.titulo()); }

    protected void armazenar(Livro livro) { livros.salvar(livro); }

    protected void apresentar(Livro livro) {
        saida.accept("Livro " + livro.getId() + " - " + livro.getTitulo() + " [" + livro.getNomeEstado() + "]");
    }
}
