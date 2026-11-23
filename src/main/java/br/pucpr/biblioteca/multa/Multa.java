package br.pucpr.biblioteca.multa;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Multa {
    public static final String EMITIDA = "EMITIDA";
    public static final String QUITADA = "QUITADA";

    private final String id;
    private final String idLivro;
    private final String idEstudante;
    private final long diasAtraso;
    private final BigDecimal valor;
    private final String politicaAplicada;
    private final LocalDate vencimentoDoBoleto;
    private String idExterno;
    private String situacao;

    public Multa(String id, String idLivro, String idEstudante, long diasAtraso, BigDecimal valor,
                 String politicaAplicada, LocalDate vencimentoDoBoleto, String idExterno, String situacao) {
        this.id = id;
        this.idLivro = idLivro;
        this.idEstudante = idEstudante;
        this.diasAtraso = diasAtraso;
        this.valor = valor;
        this.politicaAplicada = politicaAplicada;
        this.vencimentoDoBoleto = vencimentoDoBoleto;
        this.idExterno = idExterno;
        this.situacao = situacao;
    }

    public void quitar() { this.situacao = QUITADA; }
    public boolean estaQuitada() { return QUITADA.equals(situacao); }
    public void definirIdExterno(String idExterno) { this.idExterno = idExterno; }

    public String getId() { return id; }
    public String getIdLivro() { return idLivro; }
    public String getIdEstudante() { return idEstudante; }
    public long getDiasAtraso() { return diasAtraso; }
    public BigDecimal getValor() { return valor; }
    public String getPoliticaAplicada() { return politicaAplicada; }
    public LocalDate getVencimentoDoBoleto() { return vencimentoDoBoleto; }
    public String getIdExterno() { return idExterno; }
    public String getSituacao() { return situacao; }
}
