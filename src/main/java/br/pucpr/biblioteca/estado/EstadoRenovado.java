package br.pucpr.biblioteca.estado;

import br.pucpr.biblioteca.dominio.Emprestimo;
import br.pucpr.biblioteca.dominio.Livro;

public class EstadoRenovado extends EstadoBase {
    public String nome() { return "RENOVADO"; }

    @Override
    public boolean renovar(Livro livro, Emprestimo emprestimo) {
        if (emprestimo.getNumRenovacoes() >= Emprestimo.MAX_RENOVACOES) {
            return false;
        }
        emprestimo.renovar();
        return true;
    }

    @Override
    public boolean devolver(Livro livro) {
        livro.setEstado(new EstadoDevolvido());
        return true;
    }
}
