package br.pucpr.biblioteca.banco;

public interface ContratoCobranca {
    // devolve o identificador externo da cobranca, ja no formato institucional
    String emitir(SolicitacaoCobranca solicitacao);

    // traduz a notificacao crua do banco para o formato institucional
    NotificacaoPagamento traduzir(Object notificacaoDoBanco);
}
