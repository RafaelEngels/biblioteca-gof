package br.pucpr.biblioteca.banco;

import java.time.format.DateTimeFormatter;

public class AdaptadorBancoA implements ContratoCobranca {
    private final BancoAApi api;

    public AdaptadorBancoA(BancoAApi api) {
        this.api = api;
    }

    public String emitir(SolicitacaoCobranca s) {
        long centavos = s.valor().movePointRight(2).longValueExact();
        String documento = s.cpf().replaceAll("\\D", "");
        String data = s.vencimento().format(DateTimeFormatter.ISO_LOCAL_DATE);
        return api.criarBoleto(centavos, documento, s.idMulta(), data);
    }

    public NotificacaoPagamento traduzir(Object bruta) {
        NotificacaoBancoA n = (NotificacaoBancoA) bruta;
        return new NotificacaoPagamento(n.ref(), "PAID".equals(n.st()));
    }
}
