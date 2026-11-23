package br.pucpr.biblioteca;

import static org.junit.jupiter.api.Assertions.*;

import br.pucpr.biblioteca.acervo.Grupo;
import br.pucpr.biblioteca.acervo.ItemLivro;
import br.pucpr.biblioteca.dominio.RegraDeNegocioException;
import java.util.List;
import org.junit.jupiter.api.Test;

class R10_CompositeTest {

    private Grupo montar(Grupo unidade, Grupo s1, Grupo s2) {
        for (String id : List.of("L1", "L2", "L3")) s1.adicionar(new ItemLivro(id));
        for (String id : List.of("L4", "L5")) s2.adicionar(new ItemLivro(id));
        unidade.adicionar(s1);
        unidade.adicionar(s2);
        return unidade;
    }

    @Test
    void R10_unidadeComDuasSecoesDeTresEDoisLivrosTemCinco() {
        Grupo unidade = montar(new Grupo("Unidade"), new Grupo("Secao A"), new Grupo("Secao B"));
        assertEquals(5, unidade.contarLivros());
        assertEquals(List.of("L1", "L2", "L3", "L4", "L5"), unidade.listarLivros());
    }

    @Test
    void R10_profundidadeVariavelFunciona() {
        Grupo unidade = new Grupo("U");
        Grupo secao = new Grupo("S");
        Grupo sub = new Grupo("SS");
        sub.adicionar(new ItemLivro("L1"));
        secao.adicionar(sub);
        unidade.adicionar(secao);
        assertEquals(1, unidade.contarLivros());
    }

    @Test
    void R10_moverLivroEntreSecoesNaoAlteraIdEstadoNemEmprestimo() {
        Cenario c = new Cenario("L1", "L2");
        c.reservarEEmprestar("L1");
        Grupo unidade = new Grupo("U");
        Grupo a = new Grupo("A");
        Grupo b = new Grupo("B");
        a.adicionar(new ItemLivro("L1"));
        a.adicionar(new ItemLivro("L2"));
        unidade.adicionar(a);
        unidade.adicionar(b);
        var livroAntes = c.app.fabrica.livros().buscarPorId("L1").orElseThrow();
        var emprestimoAntes = c.app.fabrica.movimentacao().buscarEmprestimo("L1", Cenario.ESTUDANTE).orElseThrow();

        a.moverLivro("L1", b);

        assertEquals(1, a.contarLivros());
        assertEquals(1, b.contarLivros());
        assertEquals(2, unidade.contarLivros());
        var livroDepois = c.app.fabrica.livros().buscarPorId("L1").orElseThrow();
        assertSame(livroAntes, livroDepois);
        assertEquals("L1", livroDepois.getId());
        assertEquals("EMPRESTADO", livroDepois.getNomeEstado());
        assertSame(emprestimoAntes, c.app.fabrica.movimentacao().buscarEmprestimo("L1", Cenario.ESTUDANTE).orElseThrow());
    }

    @Test
    void R10_grupoRecusaCiclos() {
        Grupo a = new Grupo("A");
        Grupo b = new Grupo("B");
        a.adicionar(b);
        assertThrows(RegraDeNegocioException.class, () -> b.adicionar(a));
        assertThrows(RegraDeNegocioException.class, () -> a.adicionar(a));
    }

    @Test
    void R10_grupoRecusaLivroQueJaPertenceAOutroGrupo() {
        Grupo unidade = new Grupo("U");
        Grupo a = new Grupo("A");
        Grupo b = new Grupo("B");
        unidade.adicionar(a);
        unidade.adicionar(b);
        a.adicionar(new ItemLivro("L1"));
        assertThrows(RegraDeNegocioException.class, () -> b.adicionar(new ItemLivro("L1")));
        assertEquals(1, unidade.contarLivros());
    }
}
