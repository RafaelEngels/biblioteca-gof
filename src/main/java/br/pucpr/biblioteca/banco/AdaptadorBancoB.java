package br.pucpr.biblioteca.banco;

import java.time.format.DateTimeFormatter;

public class AdaptadorBancoB implements ContratoCobranca {
    private final BancoBApi api;

    public AdaptadorBancoB(BancoBApi api) {
        this.api = api;
    }

    public String emitir(SolicitacaoCobranca s) {
        String d = s.cpf().replaceAll("\\D", "");
        String cpf = d.substring(0, 3) + "." + d.substring(3, 6) + "." + d.substring(6, 9) + "-" + d.substring(9);
        String data = s.vencimento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        int id = api.registrarCobranca(s.valor().doubleValue(), cpf, s.idMulta(), data);
        return "B-" + id;
    }

    public NotificacaoPagamento traduzir(Object bruta) {
        NotificacaoBancoB n = (NotificacaoBancoB) bruta;
        return new NotificacaoPagamento("B-" + n.idCobranca(), "LIQUIDADO".equals(n.situacao()));
    }
}
