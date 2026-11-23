package br.pucpr.biblioteca.evento;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

// ja leva tudo que os ouvintes precisam, ninguem precisa consultar depois
public record EventoDevolucao(String idLivro, String idEstudante, String cpf, String email,
                              LocalDate vencimento, LocalDate dataDevolucao) {

    public long diasAtraso() {
        return Math.max(0, ChronoUnit.DAYS.between(vencimento, dataDevolucao));
    }
}
