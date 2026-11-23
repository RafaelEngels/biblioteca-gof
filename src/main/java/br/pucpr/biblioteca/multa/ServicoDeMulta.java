package br.pucpr.biblioteca.multa;

import br.pucpr.biblioteca.banco.ContratoCobranca;
import br.pucpr.biblioteca.banco.NotificacaoPagamento;
import br.pucpr.biblioteca.banco.SolicitacaoCobranca;
import br.pucpr.biblioteca.evento.EventoDevolucao;
import br.pucpr.biblioteca.persistencia.FabricaDeAcesso;
import br.pucpr.biblioteca.persistencia.RepositorioMovimentacao;
import br.pucpr.biblioteca.persistencia.UnidadeDeTrabalho;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

// nao tem if de politica nem de banco: so fala com as interfaces
public class ServicoDeMulta {
    private final ConfiguracaoAdministrativa configuracao;
    private final ContratoCobranca cobranca;
    private final FabricaDeAcesso fabrica;

    public ServicoDeMulta(ConfiguracaoAdministrativa configuracao, ContratoCobranca cobranca, FabricaDeAcesso fabrica) {
        this.configuracao = configuracao;
        this.cobranca = cobranca;
        this.fabrica = fabrica;
    }

    public Optional<Multa> emitir(EventoDevolucao evento) {
        return emTransacao(() -> emitirNaTransacao(evento));
    }

    private Optional<Multa> emitirNaTransacao(EventoDevolucao evento) {
        PoliticaDeMulta politica = configuracao.politicaVigente();
        BigDecimal valor = politica.calcular(evento.diasAtraso());
        if (valor.signum() <= 0) {
            return Optional.empty();
        }
        String id = "M-" + UUID.randomUUID().toString().substring(0, 8);
        // valor e politica ficam gravados na emissao, trocar a politica depois nao mexe nisso
        Multa multa = new Multa(id, evento.idLivro(), evento.idEstudante(), evento.diasAtraso(), valor,
                politica.nome(), evento.dataDevolucao().plusDays(7), null, Multa.EMITIDA);
        String idExterno = cobranca.emitir(new SolicitacaoCobranca(id, evento.cpf(), valor, multa.getVencimentoDoBoleto()));
        multa.definirIdExterno(idExterno);
        fabrica.movimentacao().salvarMulta(multa);
        return Optional.of(multa);
    }

    public boolean receberNotificacao(Object notificacaoDoBanco) {
        return emTransacao(() -> receberNaTransacao(notificacaoDoBanco));
    }

    private boolean receberNaTransacao(Object notificacaoDoBanco) {
        NotificacaoPagamento n = cobranca.traduzir(notificacaoDoBanco);
        RepositorioMovimentacao mov = fabrica.movimentacao();
        Optional<Multa> achada = mov.buscarMultaPorIdExterno(n.idExterno());
        if (achada.isEmpty() || !n.paga() || achada.get().estaQuitada()) {
            return false; // desconhecida, ainda aberta ou notificacao repetida
        }
        achada.get().quitar();
        mov.salvarMulta(achada.get());
        return true;
    }

    private <T> T emTransacao(Supplier<T> corpo) {
        UnidadeDeTrabalho unidade = fabrica.unidade();
        synchronized (unidade) {
            unidade.iniciar();
            try {
                T resultado = corpo.get();
                unidade.confirmar();
                return resultado;
            } catch (RuntimeException e) {
                unidade.desfazer();
                throw e;
            }
        }
    }

    public Optional<Multa> consultar(String idMulta) { return fabrica.movimentacao().buscarMulta(idMulta); }

    public List<Multa> listar() { return fabrica.movimentacao().listarMultas(); }
}
