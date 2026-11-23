package br.pucpr.biblioteca.banco;

// so serve pra demo/testes: monta a notificacao crua no formato de cada banco
public final class SimuladorDeBanco {
    private SimuladorDeBanco() {
    }

    public static Object notificacaoDePagamento(String banco, String idExterno) {
        if ("B".equals(banco)) {
            return new NotificacaoBancoB(Integer.parseInt(idExterno.substring(2)), "LIQUIDADO", 0);
        }
        return new NotificacaoBancoA(idExterno, "PAID", 0);
    }
}
