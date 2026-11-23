package br.pucpr.biblioteca.tratador;

import br.pucpr.biblioteca.cadastro.CadastroAtendente;
import br.pucpr.biblioteca.cadastro.DadosAtendente;
import br.pucpr.biblioteca.dominio.iAtendente;

public class TratadorDeAtendente {
    private final CadastroAtendente cadastro;

    public TratadorDeAtendente(CadastroAtendente cadastro) {
        this.cadastro = cadastro;
    }

    public iAtendente inserirAtendente(String cpf, String matriculaFuncional) {
        return cadastro.incluir(new DadosAtendente(cpf, matriculaFuncional));
    }
}
