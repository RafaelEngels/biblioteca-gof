package br.pucpr.biblioteca.multa;

import java.math.BigDecimal;

public class PoliticaComTolerancia implements PoliticaDeMulta {
    private static final int DIAS_DE_TOLERANCIA = 2;
    private final PoliticaDeMulta base = new PoliticaOriginal();

    public String nome() { return "COM_TOLERANCIA"; }

    public BigDecimal calcular(long diasAtraso) {
        return base.calcular(diasAtraso - DIAS_DE_TOLERANCIA);
    }
}
