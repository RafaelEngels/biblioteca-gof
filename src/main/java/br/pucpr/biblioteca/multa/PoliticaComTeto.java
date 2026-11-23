package br.pucpr.biblioteca.multa;

import java.math.BigDecimal;

public class PoliticaComTeto implements PoliticaDeMulta {
    private static final BigDecimal TETO = new BigDecimal("20.00");
    private final PoliticaDeMulta base = new PoliticaOriginal();

    public String nome() { return "COM_TETO"; }

    public BigDecimal calcular(long diasAtraso) {
        return base.calcular(diasAtraso).min(TETO);
    }
}
