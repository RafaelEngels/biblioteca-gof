package br.pucpr.biblioteca;

import static org.junit.jupiter.api.Assertions.*;

import br.pucpr.biblioteca.banco.AdaptadorBancoA;
import br.pucpr.biblioteca.banco.AdaptadorBancoB;
import br.pucpr.biblioteca.banco.BancoAApi;
import br.pucpr.biblioteca.banco.BancoBApi;
import br.pucpr.biblioteca.banco.ContratoCobranca;
import br.pucpr.biblioteca.banco.SimuladorDeBanco;
import br.pucpr.biblioteca.banco.SolicitacaoCobranca;
import br.pucpr.biblioteca.app.Configuracao;
import br.pucpr.biblioteca.multa.Multa;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class R03_AdapterTest {

    @Test
    void R03_mesmaSolicitacaoDeSeteReaisViraSetecentosCentavosNoAeSeteNoB() {
        SolicitacaoCobranca s = new SolicitacaoCobranca("M1", "111.444.777-35", new BigDecimal("7.00"), LocalDate.of(2026, 12, 1));
        BancoAApi a = new BancoAApi();
        BancoBApi b = new BancoBApi();
        ContratoCobranca contratoA = new AdaptadorBancoA(a);
        ContratoCobranca contratoB = new AdaptadorBancoB(b);

        contratoA.emitir(s);
        contratoB.emitir(s);

        assertEquals(700, a.ultimoValorEmCentavos);
        assertEquals("11144477735", a.ultimoDocumento);
        assertEquals("2026-12-01", a.ultimaData);
        assertEquals(7.00, b.ultimoValor, 0.0001);
        assertEquals("111.444.777-35", b.ultimoCpf);
        assertEquals("01/12/2026", b.ultimaData);
    }

    @Test
    void R03_notificacaoRepetidaNaoGeraSegundaQuitacaoNoBancoA() {
        verificarQuitacaoUnica("A");
    }

    @Test
    void R03_notificacaoRepetidaNaoGeraSegundaQuitacaoNoBancoB() {
        verificarQuitacaoUnica("B");
    }

    private void verificarQuitacaoUnica(String banco) {
        Cenario c = new Cenario(Configuracao.teste().comBanco(banco), "L1");
        c.reservarEEmprestar("L1");
        c.app.relogio.avancarDias(40);
        c.app.tratadorDeDevolucao.registrarDevolucao("L1", Cenario.ESTUDANTE, Cenario.ATENDENTE);
        Multa multa = c.app.tratadorDeMulta.listarMultas().get(0);
        assertTrue(multa.getIdExterno().startsWith(banco + "-"));

        Object notificacao = SimuladorDeBanco.notificacaoDePagamento(banco, multa.getIdExterno());
        assertTrue(c.app.tratadorDeMulta.registrarQuitacao(notificacao));
        assertFalse(c.app.tratadorDeMulta.registrarQuitacao(notificacao));
        assertTrue(c.app.tratadorDeMulta.consultarMulta(multa.getId()).orElseThrow().estaQuitada());
    }
}
