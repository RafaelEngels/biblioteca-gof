package br.pucpr.biblioteca.tratador;

import br.pucpr.biblioteca.cadastro.CadastroPessoa;
import br.pucpr.biblioteca.cadastro.DadosPessoa;
import br.pucpr.biblioteca.dominio.iPessoa;

public class TratadorDePessoa {
    private final CadastroPessoa cadastro;

    public TratadorDePessoa(CadastroPessoa cadastro) {
        this.cadastro = cadastro;
    }

    public iPessoa inserirPessoa(String cpf, String nome, String sobrenome, String email) {
        return cadastro.incluir(new DadosPessoa(cpf, nome, sobrenome, email));
    }
}
