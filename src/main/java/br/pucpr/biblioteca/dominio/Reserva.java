package br.pucpr.biblioteca.dominio;

import java.time.LocalDate;

public class Reserva {
    private final String idLivro;
    private final String idEstudante;
    private final String idAtendente;
    private final LocalDate data;

    public Reserva(String idLivro, String idEstudante, String idAtendente, LocalDate data) {
        this.idLivro = idLivro;
        this.idEstudante = idEstudante;
        this.idAtendente = idAtendente;
        this.data = data;
    }

    public String getIdLivro() { return idLivro; }
    public String getIdEstudante() { return idEstudante; }
    public String getIdAtendente() { return idAtendente; }
    public LocalDate getData() { return data; }
}
