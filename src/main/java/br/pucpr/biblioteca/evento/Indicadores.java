package br.pucpr.biblioteca.evento;

public class Indicadores {
    private int devolucoes;
    private int devolucoesAtrasadas;
    private long diasDeAtrasoTotal;

    public synchronized void registrar(EventoDevolucao e) {
        devolucoes++;
        if (e.diasAtraso() > 0) {
            devolucoesAtrasadas++;
            diasDeAtrasoTotal += e.diasAtraso();
        }
    }

    public synchronized int devolucoes() { return devolucoes; }
    public synchronized int devolucoesAtrasadas() { return devolucoesAtrasadas; }
    public synchronized long diasDeAtrasoTotal() { return diasDeAtrasoTotal; }
}
