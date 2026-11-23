package br.pucpr.biblioteca.evento;

import java.util.ArrayList;
import java.util.List;

public class FilaDeInspecao {
    private final List<String> pendentes = new ArrayList<>();

    public synchronized void entrar(String idLivro) { pendentes.add(idLivro); }

    public synchronized void sair(String idLivro) { pendentes.remove(idLivro); }

    public synchronized List<String> pendentes() { return new ArrayList<>(pendentes); }
}
