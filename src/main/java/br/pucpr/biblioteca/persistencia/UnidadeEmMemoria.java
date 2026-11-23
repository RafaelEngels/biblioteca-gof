package br.pucpr.biblioteca.persistencia;

import java.util.ArrayDeque;
import java.util.Deque;

public class UnidadeEmMemoria implements UnidadeDeTrabalho {
    private final Deque<Runnable> desfazimentos = new ArrayDeque<>();

    public void iniciar() { desfazimentos.clear(); }

    public void confirmar() { desfazimentos.clear(); }

    public void desfazer() {
        while (!desfazimentos.isEmpty()) {
            desfazimentos.pop().run();
        }
    }

    public void aoDesfazer(Runnable acao) { desfazimentos.push(acao); }
}
