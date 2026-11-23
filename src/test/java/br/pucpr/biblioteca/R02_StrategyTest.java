package br.pucpr.biblioteca;

import static org.junit.jupiter.api.Assertions.*;

import br.pucpr.biblioteca.app.Configuracao;
import br.pucpr.biblioteca.multa.Multa;
import br.pucpr.biblioteca.multa.PoliticaComTeto;
import br.pucpr.biblioteca.multa.PoliticaComTolerancia;
import br.pucpr.biblioteca.multa.PoliticaOriginal;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class R02_StrategyTest {

    @Test
    void R02_cincoDiasDeAtrasoOriginalETolerancia() {
        assertEquals(new BigDecimal("5.00"), new PoliticaOriginal().calcular(5));
        assertEquals(new BigDecimal("3.00"), new PoliticaComTolerancia().calcular(5));
        assertEquals(0, new PoliticaOriginal().calcular(0).signum());
        assertEquals(0, new PoliticaComTolerancia().calcular(0).signum());
    }

    @Test
    void R02_tetoDeVinteReais() {
        assertEquals(new BigDecimal("20.00"), new PoliticaComTeto().calcular(50));
        assertEquals(new BigDecimal("7.00"), new PoliticaComTeto().calcular(7));
    }

    @Test
    void R02_trocarPoliticaNaoRecalculaMultaJaEmitida() {
        Cenario c = new Cenario("L1");
        c.reservarEEmprestar("L1");
        c.app.relogio.avancarDias(Emprestimo30mais5());
        c.app.tratadorDeDevolucao.registrarDevolucao("L1", Cenario.ESTUDANTE, Cenario.ATENDENTE);

        Multa emitida = c.app.tratadorDeMulta.listarMultas().get(0);
        assertEquals(new BigDecimal("5.00"), emitida.getValor());
        assertEquals("ORIGINAL", emitida.getPoliticaAplicada());

        c.app.configuracaoAdministrativa.definirPolitica(new PoliticaComTolerancia());
        Multa depois = c.app.tratadorDeMulta.consultarMulta(emitida.getId()).orElseThrow();
        assertEquals(new BigDecimal("5.00"), depois.getValor());
        assertEquals("ORIGINAL", depois.getPoliticaAplicada());
    }

    @Test
    void R02_politicaVigenteVemDaConfiguracao() {
        Cenario c = new Cenario(Configuracao.teste().comPolitica("COM_TOLERANCIA"), "L1");
        c.reservarEEmprestar("L1");
        c.app.relogio.avancarDias(Emprestimo30mais5());
        c.app.tratadorDeDevolucao.registrarDevolucao("L1", Cenario.ESTUDANTE, Cenario.ATENDENTE);
        Multa m = c.app.tratadorDeMulta.listarMultas().get(0);
        assertEquals(new BigDecimal("3.00"), m.getValor());
        assertEquals("COM_TOLERANCIA", m.getPoliticaAplicada());
    }

    private static long Emprestimo30mais5() { return 35; }
}
