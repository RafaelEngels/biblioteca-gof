package br.pucpr.biblioteca.tratador;

import br.pucpr.biblioteca.cadastro.CadastroLivro;
import br.pucpr.biblioteca.cadastro.DadosLivro;
import br.pucpr.biblioteca.dominio.iLivro;

public class TratadorDeLivro {
    private final CadastroLivro cadastro;

    public TratadorDeLivro(CadastroLivro cadastro) {
        this.cadastro = cadastro;
    }

    public iLivro inserirLivro(String idLivro, String titulo) { return cadastro.incluir(new DadosLivro(idLivro, titulo)); }
}
