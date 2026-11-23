package br.pucpr.biblioteca.acervo;

import br.pucpr.biblioteca.dominio.RegraDeNegocioException;
import java.util.ArrayList;
import java.util.List;

// unidade, secao ou subsecao: profundidade livre
public class Grupo extends ComponenteDoAcervo {
    private final String nome;
    private final List<ComponenteDoAcervo> filhos = new ArrayList<>();

    public Grupo(String nome) {
        this.nome = nome;
    }

    public String nome() { return nome; }

    public int contarLivros() {
        int total = 0;
        for (ComponenteDoAcervo f : filhos) {
            total += f.contarLivros();
        }
        return total;
    }

    public List<String> listarLivros() {
        List<String> ids = new ArrayList<>();
        for (ComponenteDoAcervo f : filhos) {
            ids.addAll(f.listarLivros());
        }
        return ids;
    }

    public void adicionar(ComponenteDoAcervo novo) {
        if (novo == this || (novo instanceof Grupo g && g.contem(this))) {
            throw new RegraDeNegocioException("ciclo na arvore do acervo");
        }
        if (novo.pai() != null) {
            throw new RegraDeNegocioException("componente ja pertence a outro grupo");
        }
        List<String> existentes = raiz().listarLivros();
        for (String id : novo.listarLivros()) {
            if (existentes.contains(id)) {
                throw new RegraDeNegocioException("livro ja pertence a um grupo: " + id);
            }
        }
        filhos.add(novo);
        novo.definirPai(this);
    }

    public boolean remover(ComponenteDoAcervo filho) {
        boolean tirou = filhos.remove(filho);
        if (tirou) filho.definirPai(null);
        return tirou;
    }

    // move so o item da arvore; Livro e Movimentacao nem sao tocados
    public void moverLivro(String idLivro, Grupo destino) {
        ItemLivro item = filhos.stream()
                .filter(f -> f instanceof ItemLivro i && i.idLivro().equals(idLivro))
                .map(f -> (ItemLivro) f)
                .findFirst()
                .orElseThrow(() -> new RegraDeNegocioException("livro nao esta direto neste grupo: " + idLivro));
        remover(item);
        try {
            destino.adicionar(item);
        } catch (RuntimeException e) {
            filhos.add(item);
            item.definirPai(this);
            throw e;
        }
    }

    boolean contem(ComponenteDoAcervo alvo) {
        for (ComponenteDoAcervo f : filhos) {
            if (f == alvo || (f instanceof Grupo g && g.contem(alvo))) {
                return true;
            }
        }
        return false;
    }
}
