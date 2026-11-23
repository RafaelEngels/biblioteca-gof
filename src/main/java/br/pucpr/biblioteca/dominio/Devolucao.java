package br.pucpr.biblioteca.dominio;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Devolucao {
    private final String idLivro;
    private final String idEstudante;
    private final String idAtendente;
    private final LocalDate dataVencimento;
    private final LocalDate dataDevolucao;

    public Devolucao(String idLivro, String idEstudante, String idAtendente,
                     LocalDate dataVencimento, LocalDate dataDevolucao) {
        this.idLivro = idLivro;
        this.idEstudante = idEstudante;
        this.idAtendente = idAtendente;
        this.dataVencimento = dataVencimento;
        this.dataDevolucao = dataDevolucao;
    }

    public long calcularDiasAtraso() {
        return Math.max(0, ChronoUnit.DAYS.between(dataVencimento, dataDevolucao));
    }

    public String getIdLivro() { return idLivro; }
    public String getIdEstudante() { return idEstudante; }
    public String getIdAtendente() { return idAtendente; }
    public LocalDate getDataVencimento() { return dataVencimento; }
    public LocalDate getDataDevolucao() { return dataDevolucao; }
}
