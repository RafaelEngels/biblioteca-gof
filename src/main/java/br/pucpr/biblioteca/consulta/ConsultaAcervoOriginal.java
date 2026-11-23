package br.pucpr.biblioteca.consulta;

import br.pucpr.biblioteca.dominio.Livro;
import br.pucpr.biblioteca.persistencia.RepositorioLivro;
import java.util.Optional;

public class ConsultaAcervoOriginal implements ConsultaAcervo {
    private final RepositorioLivro livros;

    public ConsultaAcervoOriginal(RepositorioLivro livros) {
        this.livros = livros;
    }

    public Optional<MetadadosLivro> metadados(String idLivro) {
        return livros.buscarPorId(idLivro).map(l -> new MetadadosLivro(l.getId(), l.getTitulo()));
    }

    public Optional<String> estadoAtual(String idLivro) {
        return livros.buscarPorId(idLivro).map(Livro::getNomeEstado);
    }
}
