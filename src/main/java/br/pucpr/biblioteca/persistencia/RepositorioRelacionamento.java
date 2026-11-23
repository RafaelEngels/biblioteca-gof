package br.pucpr.biblioteca.persistencia;

import br.pucpr.biblioteca.dominio.Atendente;
import br.pucpr.biblioteca.dominio.Estudante;
import br.pucpr.biblioteca.dominio.Pessoa;
import java.util.Optional;

public interface RepositorioRelacionamento {
    void salvarPessoa(Pessoa pessoa);
    Optional<Pessoa> buscarPessoa(String cpf);

    void salvarEstudante(Estudante estudante);
    Optional<Estudante> buscarEstudante(String matricula);

    void salvarAtendente(Atendente atendente);
    Optional<Atendente> buscarAtendente(String matriculaFuncional);
}
