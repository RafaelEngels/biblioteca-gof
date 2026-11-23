package br.pucpr.biblioteca.estado;

import br.pucpr.biblioteca.dominio.Livro;

public class EstadoDevolvido extends EstadoBase {
    public String nome() { return "DEVOLVIDO"; }

    @Override
    public boolean aprovarInspecao(Livro livro) {
        livro.setEstado(new EstadoLivre());
        return true;
    }

    @Override
    public boolean enviarParaReparo(Livro livro) {
        livro.setEstado(new EstadoReparando());
        return true;
    }
}
