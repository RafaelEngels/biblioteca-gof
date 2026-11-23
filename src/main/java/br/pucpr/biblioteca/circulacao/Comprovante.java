package br.pucpr.biblioteca.circulacao;

import java.time.LocalDate;

// o que a fachada devolve para a interface, no lugar de colecoes ou entidades
public record Comprovante(String tipo, String idLivro, String idEstudante, LocalDate data,
                          LocalDate vencimento, String estadoDoLivro) {
}
