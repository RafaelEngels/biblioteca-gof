package br.pucpr.biblioteca.estado;

import br.pucpr.biblioteca.dominio.Emprestimo;
import br.pucpr.biblioteca.dominio.Livro;

public class EstadoEmprestado extends EstadoBase {
    public String nome() { return "EMPRESTADO"; }

    @Override
    public boolean renovar(Livro livro, Emprestimo emprestimo) {
        // guarda antes de mexer no vencimento
        if (emprestimo.getNumRenovacoes() >= Emprestimo.MAX_RENOVACOES) {
            return false;
        }
        emprestimo.renovar();
        livro.setEstado(new EstadoRenovado());
        return true;
    }

    @Override
    public boolean devolver(Livro livro) {
        livro.setEstado(new EstadoDevolvido());
        return true;
    }
}
