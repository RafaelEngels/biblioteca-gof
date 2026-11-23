package br.pucpr.biblioteca.evento;

public class OuvinteIndicadores implements OuvinteDeDevolucao {
    private final Indicadores indicadores;

    public OuvinteIndicadores(Indicadores indicadores) {
        this.indicadores = indicadores;
    }

    public void aoDevolver(EventoDevolucao evento) {
        indicadores.registrar(evento);
    }
}
