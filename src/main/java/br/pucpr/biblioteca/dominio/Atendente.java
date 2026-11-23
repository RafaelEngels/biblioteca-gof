package br.pucpr.biblioteca.dominio;

public class Atendente implements iAtendente {
    private final String matriculaFuncional;
    private final Pessoa pessoa;

    public Atendente(String matriculaFuncional, Pessoa pessoa) {
        this.matriculaFuncional = matriculaFuncional;
        this.pessoa = pessoa;
    }

    public String getMatriculaFuncional() { return matriculaFuncional; }
    public Pessoa getPessoa() { return pessoa; }
}
