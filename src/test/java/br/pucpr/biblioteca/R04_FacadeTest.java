package br.pucpr.biblioteca;

import static org.junit.jupiter.api.Assertions.*;

import br.pucpr.biblioteca.circulacao.Comprovante;
import br.pucpr.biblioteca.dominio.RegraDeNegocioException;
import br.pucpr.biblioteca.estado.EstadoLivre;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class R04_FacadeTest {

    @Test
    void R04_efetivacaoValidaReservaRemoveCriaEmprestimoEAtualizaLivro() {
        Cenario c = new Cenario("L1");
        c.app.tratadorDeEmprestimo.reservarLivro(UUID.randomUUID().toString(), "L1", Cenario.ESTUDANTE, Cenario.ATENDENTE);
        assertTrue(c.app.tratadorDeEmprestimo.consultarReserva("L1", Cenario.ESTUDANTE).isPresent());

        Comprovante comprovante = c.app.tratadorDeEmprestimo.emprestarLivro("L1", Cenario.ESTUDANTE, Cenario.ATENDENTE);

        assertEquals("EMPRESTIMO", comprovante.tipo());
        assertEquals("EMPRESTADO", comprovante.estadoDoLivro());
        assertNotNull(comprovante.vencimento());
        assertTrue(c.app.tratadorDeEmprestimo.consultarReserva("L1", Cenario.ESTUDANTE).isEmpty());
        assertTrue(c.app.fabrica.movimentacao().buscarEmprestimo("L1", Cenario.ESTUDANTE).isPresent());
        assertEquals("EMPRESTADO", c.estado("L1"));
    }

    @Test
    void R04_semReservaNaoHaEmprestimo() {
        Cenario c = new Cenario("L1");
        assertThrows(RegraDeNegocioException.class, () -> c.app.tratadorDeEmprestimo.emprestarLivro("L1", Cenario.ESTUDANTE, Cenario.ATENDENTE));
        assertTrue(c.app.fabrica.movimentacao().buscarEmprestimo("L1", Cenario.ESTUDANTE).isEmpty());
    }

    @Test
    void R04_falhaNoUltimoPassoDesfazReservaEEmprestimo() {
        Cenario c = new Cenario("L1");
        c.app.tratadorDeEmprestimo.reservarLivro(UUID.randomUUID().toString(), "L1", Cenario.ESTUDANTE, Cenario.ATENDENTE);
        // forca inconsistencia: livro volta a LIVRE, entao o passo final (Livro.emprestar) vai recusar
        c.app.fabrica.livros().buscarPorId("L1").orElseThrow().setEstado(new EstadoLivre());

        assertThrows(RegraDeNegocioException.class, () -> c.app.tratadorDeEmprestimo.emprestarLivro("L1", Cenario.ESTUDANTE, Cenario.ATENDENTE));

        assertTrue(c.app.fabrica.movimentacao().buscarReserva("L1", Cenario.ESTUDANTE).isPresent());
        assertTrue(c.app.fabrica.movimentacao().buscarEmprestimo("L1", Cenario.ESTUDANTE).isEmpty());
        assertEquals("LIVRE", c.estado("L1"));
    }
}
