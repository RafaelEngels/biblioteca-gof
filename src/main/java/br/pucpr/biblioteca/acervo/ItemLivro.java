package br.pucpr.biblioteca.acervo;

import java.util.List;

// folha: so guarda o id do livro, nunca o Livro em si
public class ItemLivro extends ComponenteDoAcervo {
    private final String idLivro;

    public ItemLivro(String idLivro) {
        this.idLivro = idLivro;
    }

    public String nome() { return idLivro; }
    public int contarLivros() { return 1; }
    public List<String> listarLivros() { return List.of(idLivro); }
    public String idLivro() { return idLivro; }
}
