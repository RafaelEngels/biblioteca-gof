package br.pucpr.biblioteca.acervo;

import java.util.List;

public abstract class ComponenteDoAcervo {
    private Grupo pai;

    public abstract String nome();
    public abstract int contarLivros();
    public abstract List<String> listarLivros();

    Grupo pai() { return pai; }
    void definirPai(Grupo pai) { this.pai = pai; }

    Grupo raiz() {
        ComponenteDoAcervo atual = this;
        while (atual.pai != null) {
            atual = atual.pai;
        }
        return atual instanceof Grupo g ? g : null;
    }
}
