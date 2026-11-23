package br.pucpr.biblioteca.evento;

import br.pucpr.biblioteca.multa.ServicoDeMulta;

public class OuvinteMulta implements OuvinteDeDevolucao {
    private final ServicoDeMulta servico;

    public OuvinteMulta(ServicoDeMulta servico) {
        this.servico = servico;
    }

    public void aoDevolver(EventoDevolucao evento) {
        servico.emitir(evento);
    }
}
