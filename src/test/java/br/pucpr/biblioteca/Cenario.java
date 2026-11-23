package br.pucpr.biblioteca;

import br.pucpr.biblioteca.app.Aplicacao;
import br.pucpr.biblioteca.app.Configuracao;
import java.util.UUID;

// monta uma aplicacao pronta: 1 estudante, 1 atendente e os livros pedidos
class Cenario {
    static final String ESTUDANTE = "E001";
    static final String ATENDENTE = "A001";

    final Aplicacao app;

    Cenario(String... livros) {
        this(Configuracao.teste(), livros);
    }

    Cenario(Configuracao config, String... livros) {
        app = new Aplicacao(config, s -> { });
        app.tratadorDePessoa.inserirPessoa("11144477735", "Ana", "Silva", "ana@exemplo.com");
        app.tratadorDePessoa.inserirPessoa("52998224725", "Bruno", "Souza", "bruno@exemplo.com");
        app.tratadorDeEstudante.inserirEstudante("11144477735", ESTUDANTE);
        app.tratadorDeAtendente.inserirAtendente("52998224725", ATENDENTE);
        for (String id : livros) {
            app.tratadorDeLivro.inserirLivro(id, "Titulo " + id);
        }
    }

    void reservarEEmprestar(String livro) {
        app.tratadorDeEmprestimo.reservarLivro(UUID.randomUUID().toString(), livro, ESTUDANTE, ATENDENTE);
        app.tratadorDeEmprestimo.emprestarLivro(livro, ESTUDANTE, ATENDENTE);
    }

    void renovar(String livro) {
        app.tratadorDeEmprestimo.renovarEmprestimo(UUID.randomUUID().toString(), livro, ESTUDANTE, ATENDENTE);
    }

    String estado(String livro) {
        return app.fabrica.livros().buscarPorId(livro).orElseThrow().getNomeEstado();
    }
}
