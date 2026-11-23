package br.pucpr.biblioteca;

import static org.junit.jupiter.api.Assertions.*;

import br.pucpr.biblioteca.dominio.Emprestimo;
import br.pucpr.biblioteca.dominio.Livro;
import br.pucpr.biblioteca.dominio.RegraDeNegocioException;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class R01_StateTest {

    @Test
    void R01_terceiraRenovacaoRecusada() {
        Cenario c = new Cenario("L1");
        c.reservarEEmprestar("L1");
        c.renovar("L1");
        c.renovar("L1");
        Emprestimo antes = c.app.fabrica.movimentacao().buscarEmprestimo("L1", Cenario.ESTUDANTE).orElseThrow();
        var vencimento = antes.getDataVencimento();

        assertThrows(RegraDeNegocioException.class, () -> c.renovar("L1"));

        Emprestimo depois = c.app.fabrica.movimentacao().buscarEmprestimo("L1", Cenario.ESTUDANTE).orElseThrow();
        assertEquals(vencimento, depois.getDataVencimento());
        assertEquals(2, depois.getNumRenovacoes());
        assertEquals("RENOVADO", c.estado("L1"));
    }

    @Test
    void R01_livroDevolvidoNaoInspecionadoNaoPodeSerReservado() {
        Cenario c = new Cenario("L1");
        c.reservarEEmprestar("L1");
        c.app.tratadorDeDevolucao.registrarDevolucao("L1", Cenario.ESTUDANTE, Cenario.ATENDENTE);
        assertEquals("DEVOLVIDO", c.estado("L1"));

        assertThrows(RegraDeNegocioException.class, () ->
                c.app.tratadorDeEmprestimo.reservarLivro(UUID.randomUUID().toString(), "L1", Cenario.ESTUDANTE, Cenario.ATENDENTE));
        assertEquals("DEVOLVIDO", c.estado("L1"));
    }

    @Test
    void R01_eventoNaoAceitoNoEstadoERecusadoSemAlterarOLivro() {
        Livro livro = new Livro("L9", "Qualquer");
        assertFalse(livro.emprestar());
        assertFalse(livro.devolver());
        assertEquals("LIVRE", livro.getNomeEstado());
    }

    @Test
    void R01_laudoIrrecuperavelLevaDeReparandoParaIncinerado() {
        Cenario c = new Cenario("L1");
        c.reservarEEmprestar("L1");
        c.app.tratadorDeDevolucao.registrarDevolucao("L1", Cenario.ESTUDANTE, Cenario.ATENDENTE);
        c.app.tratadorDeDevolucao.inspecionar("L1", false);
        assertEquals("REPARANDO", c.estado("L1"));
        c.app.tratadorDeDevolucao.registrarLaudoIrrecuperavel("L1");
        assertEquals("INCINERADO", c.estado("L1"));
        assertThrows(RegraDeNegocioException.class, () -> c.app.tratadorDeDevolucao.retornarDoReparo("L1"));
    }

    @Test
    void R01_estadoGravadoPeloNomeEReconstituido() {
        Livro livro = Livro.reconstituir("L1", "T", "REPARANDO");
        assertEquals("REPARANDO", livro.getNomeEstado());
        assertTrue(livro.retornarDoReparo());
        assertEquals("LIVRE", livro.getNomeEstado());
    }
}
