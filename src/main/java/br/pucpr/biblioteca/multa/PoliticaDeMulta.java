package br.pucpr.biblioteca.multa;

import java.math.BigDecimal;

public interface PoliticaDeMulta {
    String nome();
    BigDecimal calcular(long diasAtraso);
}
