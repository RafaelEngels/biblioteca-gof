package br.pucpr.biblioteca.estado;

import br.pucpr.biblioteca.dominio.Livro;

public class EstadoReparando extends EstadoBase {
    public String nome() { return "REPARANDO"; }

    @Override
    public boolean retornarDoReparo(Livro livro) {
        livro.setEstado(new EstadoLivre());
        return true;
    }

    @Override
    public boolean registrarLaudoIrrecuperavel(Livro livro) {
        livro.setEstado(new EstadoIncinerado());
        return true;
    }
}
