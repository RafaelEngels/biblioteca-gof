package br.pucpr.biblioteca.tratador;

import br.pucpr.biblioteca.multa.Multa;
import br.pucpr.biblioteca.multa.ServicoDeMulta;
import java.util.List;
import java.util.Optional;

public class TratadorDeMulta {
    private final ServicoDeMulta servico;

    public TratadorDeMulta(ServicoDeMulta servico) {
        this.servico = servico;
    }

    public Optional<Multa> consultarMulta(String idMulta) { return servico.consultar(idMulta); }

    public List<Multa> listarMultas() { return servico.listar(); }

    public boolean registrarQuitacao(Object notificacaoDoBanco) { return servico.receberNotificacao(notificacaoDoBanco); }
}
