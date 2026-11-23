package br.pucpr.biblioteca.evento;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class PublicadorDeDevolucao {
    private final List<OuvinteDeDevolucao> ouvintes = new CopyOnWriteArrayList<>();
    private final List<String> falhas = new ArrayList<>();

    public void registrar(OuvinteDeDevolucao ouvinte) { ouvintes.add(ouvinte); }

    public void publicar(EventoDevolucao evento) {
        for (OuvinteDeDevolucao o : ouvintes) {
            try {
                o.aoDevolver(evento);
            } catch (RuntimeException e) {
                // um ouvinte quebrado nao derruba os outros nem desfaz a devolucao
                synchronized (falhas) {
                    falhas.add(o.getClass().getSimpleName() + ": " + e.getMessage());
                }
            }
        }
    }

    public List<String> falhas() {
        synchronized (falhas) {
            return new ArrayList<>(falhas);
        }
    }
}
