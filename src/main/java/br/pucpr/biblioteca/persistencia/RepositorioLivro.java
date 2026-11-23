package br.pucpr.biblioteca.persistencia;

import br.pucpr.biblioteca.dominio.Livro;
import java.util.List;
import java.util.Optional;

public interface RepositorioLivro {
    void salvar(Livro livro);
    Optional<Livro> buscarPorId(String id);
    List<Livro> listar();
}
