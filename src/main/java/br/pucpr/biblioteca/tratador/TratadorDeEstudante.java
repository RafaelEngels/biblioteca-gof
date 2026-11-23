package br.pucpr.biblioteca.tratador;

import br.pucpr.biblioteca.cadastro.CadastroEstudante;
import br.pucpr.biblioteca.cadastro.DadosEstudante;
import br.pucpr.biblioteca.dominio.iEstudante;

public class TratadorDeEstudante {
    private final CadastroEstudante cadastro;

    public TratadorDeEstudante(CadastroEstudante cadastro) {
        this.cadastro = cadastro;
    }

    public iEstudante inserirEstudante(String cpf, String matricula) {
        return cadastro.incluir(new DadosEstudante(cpf, matricula));
    }
}
