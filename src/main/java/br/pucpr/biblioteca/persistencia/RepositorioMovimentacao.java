package br.pucpr.biblioteca.persistencia;

import br.pucpr.biblioteca.dominio.Devolucao;
import br.pucpr.biblioteca.dominio.Emprestimo;
import br.pucpr.biblioteca.dominio.Reserva;
import br.pucpr.biblioteca.multa.Multa;
import java.util.List;
import java.util.Optional;

public interface RepositorioMovimentacao {
    void adicionarReserva(Reserva reserva);
    Optional<Reserva> buscarReserva(String idLivro, String idEstudante);
    void removerReserva(Reserva reserva);

    void adicionarEmprestimo(Emprestimo emprestimo);
    Optional<Emprestimo> buscarEmprestimo(String idLivro, String idEstudante);
    void atualizarEmprestimo(Emprestimo emprestimo);
    void removerEmprestimo(Emprestimo emprestimo);

    void adicionarDevolucao(Devolucao devolucao);
    Optional<Devolucao> buscarDevolucao(String idLivro, String idEstudante);
    void removerDevolucao(Devolucao devolucao);

    void salvarMulta(Multa multa);
    Optional<Multa> buscarMulta(String id);
    Optional<Multa> buscarMultaPorIdExterno(String idExterno);
    List<Multa> listarMultas();
}
