package br.pucpr.biblioteca.persistencia;

import br.pucpr.biblioteca.dominio.Livro;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class LivrosEmMemoria implements RepositorioLivro {
    private final UnidadeEmMemoria unidade;
    private final Map<String, Livro> livros = new LinkedHashMap<>();

    LivrosEmMemoria(UnidadeEmMemoria unidade) {
        this.unidade = unidade;
    }

    public UnidadeEmMemoria unidade() { return unidade; }

    public void salvar(Livro livro) { livros.put(livro.getId(), livro); }

    public Optional<Livro> buscarPorId(String id) { return Optional.ofNullable(livros.get(id)); }

    public List<Livro> listar() { return new ArrayList<>(livros.values()); }
}
