package br.pucpr.biblioteca.estado;

import br.pucpr.biblioteca.dominio.Emprestimo;
import br.pucpr.biblioteca.dominio.Livro;

// cada metodo e um evento da maquina de estados; true = aceitou, false = recusou
public interface EstadoLivro {
    String nome();

    boolean reservar(Livro livro);
    boolean emprestar(Livro livro);
    boolean renovar(Livro livro, Emprestimo emprestimo);
    boolean devolver(Livro livro);
    boolean aprovarInspecao(Livro livro);
    boolean enviarParaReparo(Livro livro);
    boolean retornarDoReparo(Livro livro);
    boolean registrarLaudoIrrecuperavel(Livro livro);

    static EstadoLivro de(String nome) {
        return switch (nome) {
            case "LIVRE" -> new EstadoLivre();
            case "RESERVADO" -> new EstadoReservado();
            case "EMPRESTADO" -> new EstadoEmprestado();
            case "RENOVADO" -> new EstadoRenovado();
            case "DEVOLVIDO" -> new EstadoDevolvido();
            case "REPARANDO" -> new EstadoReparando();
            case "INCINERADO" -> new EstadoIncinerado();
            default -> throw new IllegalArgumentException("estado desconhecido: " + nome);
        };
    }
}
