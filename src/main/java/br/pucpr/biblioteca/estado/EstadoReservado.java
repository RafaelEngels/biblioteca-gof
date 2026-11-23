package br.pucpr.biblioteca.estado;

import br.pucpr.biblioteca.dominio.Livro;

public class EstadoReservado extends EstadoBase {
    public String nome() { return "RESERVADO"; }

    @Override
    public boolean emprestar(Livro livro) {
        livro.setEstado(new EstadoEmprestado());
        return true;
    }
}
