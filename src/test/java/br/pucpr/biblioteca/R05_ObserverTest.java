package br.pucpr.biblioteca;

import static org.junit.jupiter.api.Assertions.*;

import br.pucpr.biblioteca.app.Aplicacao;
import br.pucpr.biblioteca.app.Configuracao;
import org.junit.jupiter.api.Test;

class R05_ObserverTest {

    @Test
    void R05_devolucaoAtrasadaAlimentaInspecaoMultaEIndicadores() {
        Cenario c = new Cenario("L1");
        c.reservarEEmprestar("L1");
        c.app.relogio.avancarDias(36);

        c.app.tratadorDeDevolucao.registrarDevolucao("L1", Cenario.ESTUDANTE, Cenario.ATENDENTE);

        assertEquals(1, c.app.filaDeInspecao.pendentes().size());
        assertEquals(1, c.app.tratadorDeMulta.listarMultas().size());
        assertEquals(6, c.app.tratadorDeMulta.listarMultas().get(0).getDiasAtraso());
        assertEquals(1, c.app.indicadores.devolucoesAtrasadas());
        assertEquals(6, c.app.indicadores.diasDeAtrasoTotal());
        assertEquals(1, c.app.emailsEnviados.size());
        assertEquals("DEVOLVIDO", c.estado("L1"));
    }

    @Test
    void R05_devolucaoNoPrazoNaoGeraMulta() {
        Cenario c = new Cenario("L1");
        c.reservarEEmprestar("L1");
        c.app.relogio.avancarDias(10);

        c.app.tratadorDeDevolucao.registrarDevolucao("L1", Cenario.ESTUDANTE, Cenario.ATENDENTE);

        assertTrue(c.app.tratadorDeMulta.listarMultas().isEmpty());
        assertEquals(1, c.app.filaDeInspecao.pendentes().size());
        assertEquals(0, c.app.indicadores.devolucoesAtrasadas());
        assertEquals("DEVOLVIDO", c.estado("L1"));
    }

    @Test
    void R05_falhaDoEmailNaoAfetaOsOutrosNemDesfazADevolucao() {
        Aplicacao app = new Aplicacao(Configuracao.teste(), s -> { },
                (para, assunto, corpo) -> { throw new IllegalStateException("smtp fora do ar"); });
        app.tratadorDePessoa.inserirPessoa("11144477735", "Ana", "Silva", "ana@exemplo.com");
        app.tratadorDePessoa.inserirPessoa("52998224725", "Bruno", "Souza", "bruno@exemplo.com");
        app.tratadorDeEstudante.inserirEstudante("11144477735", "E001");
        app.tratadorDeAtendente.inserirAtendente("52998224725", "A001");
        app.tratadorDeLivro.inserirLivro("L1", "Titulo");
        app.tratadorDeEmprestimo.reservarLivro("r1", "L1", "E001", "A001");
        app.tratadorDeEmprestimo.emprestarLivro("L1", "E001", "A001");
        app.relogio.avancarDias(40);

        var comprovante = app.tratadorDeDevolucao.registrarDevolucao("L1", "E001", "A001");

        assertEquals("DEVOLVIDO", comprovante.estadoDoLivro());
        assertEquals(1, app.publicador.falhas().size());
        assertEquals(1, app.filaDeInspecao.pendentes().size());
        assertEquals(1, app.tratadorDeMulta.listarMultas().size());
        assertEquals(1, app.indicadores.devolucoes());
    }
}
