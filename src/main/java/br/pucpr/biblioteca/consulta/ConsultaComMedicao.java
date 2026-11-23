package br.pucpr.biblioteca.consulta;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ConsultaComMedicao implements ConsultaAcervo {
    private final ConsultaAcervo proxima;
    private final AtomicInteger consultas = new AtomicInteger();
    private final AtomicLong nanosTotais = new AtomicLong();

    public ConsultaComMedicao(ConsultaAcervo proxima) {
        this.proxima = proxima;
    }

    public Optional<MetadadosLivro> metadados(String idLivro) {
        long inicio = System.nanoTime();
        try {
            return proxima.metadados(idLivro);
        } finally {
            consultas.incrementAndGet();
            nanosTotais.addAndGet(System.nanoTime() - inicio);
        }
    }

    public Optional<String> estadoAtual(String idLivro) {
        long inicio = System.nanoTime();
        try {
            return proxima.estadoAtual(idLivro);
        } finally {
            consultas.incrementAndGet();
            nanosTotais.addAndGet(System.nanoTime() - inicio);
        }
    }

    public int totalDeConsultas() { return consultas.get(); }
    public long nanosTotais() { return nanosTotais.get(); }
}
