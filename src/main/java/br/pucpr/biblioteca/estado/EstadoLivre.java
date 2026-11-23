package br.pucpr.biblioteca.estado;

import br.pucpr.biblioteca.dominio.Livro;

public class EstadoLivre extends EstadoBase {
    public String nome() { return "LIVRE"; }

    @Override
    public boolean reservar(Livro livro) {
        livro.setEstado(new EstadoReservado());
        return true;
    }
}
