package br.pucpr.biblioteca.multa;

import java.math.BigDecimal;

// R$ 1,00 por dia de atraso
public class PoliticaOriginal implements PoliticaDeMulta {
    static final BigDecimal VALOR_DIARIO = new BigDecimal("1.00");

    public String nome() { return "ORIGINAL"; }

    public BigDecimal calcular(long diasAtraso) {
        if (diasAtraso <= 0) return new BigDecimal("0.00");
        return VALOR_DIARIO.multiply(BigDecimal.valueOf(diasAtraso));
    }
}
