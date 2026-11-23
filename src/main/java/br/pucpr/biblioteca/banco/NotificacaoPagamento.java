package br.pucpr.biblioteca.banco;

// formato institucional, igual para qualquer banco
public record NotificacaoPagamento(String idExterno, boolean paga) {
}
