package br.pucpr.biblioteca.dominio;

public class Estudante implements iEstudante {
    private final String matricula;
    private final Pessoa pessoa;

    public Estudante(String matricula, Pessoa pessoa) {
        this.matricula = matricula;
        this.pessoa = pessoa;
    }

    public String getMatricula() { return matricula; }
    public Pessoa getPessoa() { return pessoa; }
}
