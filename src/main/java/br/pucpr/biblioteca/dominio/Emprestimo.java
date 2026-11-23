package br.pucpr.biblioteca.dominio;

import java.time.LocalDate;

public class Emprestimo {
    public static final int PRAZO_EM_DIAS = 30;
    public static final int MAX_RENOVACOES = 2;

    private final String idLivro;
    private final String idEstudante;
    private final String idAtendente;
    private final LocalDate dataEmprestimo;
    private LocalDate dataVencimento;
    private int numRenovacoes;

    public Emprestimo(String idLivro, String idEstudante, String idAtendente, LocalDate dataEmprestimo) {
        this(idLivro, idEstudante, idAtendente, dataEmprestimo, dataEmprestimo.plusDays(PRAZO_EM_DIAS), 0);
    }

    public Emprestimo(String idLivro, String idEstudante, String idAtendente,
                      LocalDate dataEmprestimo, LocalDate dataVencimento, int numRenovacoes) {
        this.idLivro = idLivro;
        this.idEstudante = idEstudante;
        this.idAtendente = idAtendente;
        this.dataEmprestimo = dataEmprestimo;
        this.dataVencimento = dataVencimento;
        this.numRenovacoes = numRenovacoes;
    }

    public void renovar() {
        dataVencimento = dataVencimento.plusDays(30);
        numRenovacoes++;
    }

    // usado so no desfazer da transacao
    public void restaurar(LocalDate vencimento, int renovacoes) {
        this.dataVencimento = vencimento;
        this.numRenovacoes = renovacoes;
    }

    public String getIdLivro() { return idLivro; }
    public String getIdEstudante() { return idEstudante; }
    public String getIdAtendente() { return idAtendente; }
    public LocalDate getDataEmprestimo() { return dataEmprestimo; }
    public LocalDate getDataVencimento() { return dataVencimento; }
    public int getNumRenovacoes() { return numRenovacoes; }
}
