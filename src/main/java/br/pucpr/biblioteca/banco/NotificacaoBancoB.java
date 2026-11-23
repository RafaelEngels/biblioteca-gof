package br.pucpr.biblioteca.banco;

// "LIQUIDADO" ou "ABERTO"
public record NotificacaoBancoB(int idCobranca, String situacao, double valor) {
}
