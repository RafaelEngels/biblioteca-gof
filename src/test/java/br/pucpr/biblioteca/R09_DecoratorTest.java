package br.pucpr.biblioteca;

import static org.junit.jupiter.api.Assertions.*;

import br.pucpr.biblioteca.consulta.ConsultaAcervoOriginal;
import br.pucpr.biblioteca.consulta.ConsultaComCache;
import br.pucpr.biblioteca.consulta.ConsultaComMedicao;
import br.pucpr.biblioteca.consulta.MetadadosLivro;
import br.pucpr.biblioteca.dominio.RegraDeNegocioException;
import java.util.AbstractMap;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class R09_DecoratorTest {

    @Test
    void R09_segundaConsultaDeMetadadosUsaCacheEAindaGeraMedicao() {
        Cenario c = new Cenario("L1");
        ConsultaAcervoOriginal original = new ConsultaAcervoOriginal(c.app.fabrica.livros());
        ConsultaComCache cache = new ConsultaComCache(original);
        ConsultaComMedicao medicao = new ConsultaComMedicao(cache);

        medicao.metadados("L1");
        medicao.metadados("L1");

        assertEquals(1, cache.acertosDeCache());
        assertEquals(2, medicao.totalDeConsultas());
    }

    @Test
    void R09_cacheAntigoNuncaAutorizaReservaDeLivroQueDeixouDeSerLivre() {
        Cenario c = new Cenario("L1");
        c.app.tratadorDeAcervo.consultarLivro("L1");
        c.app.tratadorDeAcervo.consultarLivro("L1"); // metadados ja estao em cache

        c.app.tratadorDeEmprestimo.reservarLivro("outra", "L1", Cenario.ESTUDANTE, Cenario.ATENDENTE);

        assertTrue(c.app.tratadorDeAcervo.consultarLivro("L1").isPresent());
        assertEquals("RESERVADO", c.app.tratadorDeAcervo.estadoDoLivro("L1").orElseThrow());
        assertThrows(RegraDeNegocioException.class, () ->
                c.app.tratadorDeEmprestimo.reservarLivro(UUID.randomUUID().toString(), "L1", Cenario.ESTUDANTE, Cenario.ATENDENTE));
    }

    @Test
    void R09_estadoAtualNuncaVemDoCache() {
        Cenario c = new Cenario("L1");
        c.app.tratadorDeAcervo.consultarLivro("L1");
        assertEquals("LIVRE", c.app.tratadorDeAcervo.estadoDoLivro("L1").orElseThrow());
        c.reservarEEmprestar("L1");
        assertEquals("EMPRESTADO", c.app.tratadorDeAcervo.estadoDoLivro("L1").orElseThrow());
    }

    @Test
    void R09_seOCacheFalharAConsultaSegueSemEle() {
        Cenario c = new Cenario("L1");
        var mapaQuebrado = new java.util.AbstractMap<String, MetadadosLivro>() {
            public Set<Entry<String, MetadadosLivro>> entrySet() { return Set.of(); }
            public MetadadosLivro get(Object k) { throw new IllegalStateException("cache fora"); }
            public MetadadosLivro put(String k, MetadadosLivro v) { throw new IllegalStateException("cache fora"); }
        };
        ConsultaComCache cache = new ConsultaComCache(new ConsultaAcervoOriginal(c.app.fabrica.livros()), mapaQuebrado);
        assertTrue(cache.metadados("L1").isPresent());
    }
}
