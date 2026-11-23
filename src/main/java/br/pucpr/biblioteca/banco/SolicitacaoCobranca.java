package br.pucpr.biblioteca.banco;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SolicitacaoCobranca(String idMulta, String cpf, BigDecimal valor, LocalDate vencimento) {
}
