package br.pucpr.biblioteca;

import static org.junit.jupiter.api.Assertions.*;

import br.pucpr.biblioteca.dominio.Emprestimo;
import br.pucpr.biblioteca.dominio.RegraDeNegocioException;
import java.time.LocalDate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.Test;

class R06_CommandTest {

    private Emprestimo emprestimo(Cenario c) {
        return c.app.fabrica.movimentacao().buscarEmprestimo("L1", Cenario.ESTUDANTE).orElseThrow();
    }

    @Test
    void R06_renovacaoComMesmoIdentificadorExecutadaDuasVezesSoAcrescentaTrintaDias() {
        Cenario c = new Cenario("L1");
        c.reservarEEmprestar("L1");
        LocalDate original = emprestimo(c).getDataVencimento();

        c.app.tratadorDeEmprestimo.renovarEmprestimo("sol-1", "L1", Cenario.ESTUDANTE, Cenario.ATENDENTE);
        c.app.tratadorDeEmprestimo.renovarEmprestimo("sol-1", "L1", Cenario.ESTUDANTE, Cenario.ATENDENTE);

        assertEquals(original.plusDays(30), emprestimo(c).getDataVencimento());
        assertEquals(1, emprestimo(c).getNumRenovacoes());
    }

    @Test
    void R06_solicitacoesDiferentesContinuamLimitadasADuasRenovacoes() {
        Cenario c = new Cenario("L1");
        c.reservarEEmprestar("L1");
        c.app.tratadorDeEmprestimo.renovarEmprestimo("sol-1", "L1", Cenario.ESTUDANTE, Cenario.ATENDENTE);
        c.app.tratadorDeEmprestimo.renovarEmprestimo("sol-2", "L1", Cenario.ESTUDANTE, Cenario.ATENDENTE);

        assertThrows(RegraDeNegocioException.class,
                () -> c.app.tratadorDeEmprestimo.renovarEmprestimo("sol-3", "L1", Cenario.ESTUDANTE, Cenario.ATENDENTE));
        assertEquals(2, emprestimo(c).getNumRenovacoes());
    }

    @Test
    void R06_duasSubmissoesSimultaneasComMesmoIdentificadorExecutamUmaVez() throws Exception {
        Cenario c = new Cenario("L1");
        c.reservarEEmprestar("L1");
        LocalDate original = emprestimo(c).getDataVencimento();
        CountDownLatch largada = new CountDownLatch(1);
        ExecutorService pool = Executors.newFixedThreadPool(2);

        Runnable envio = () -> {
            try {
                largada.await();
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            c.app.tratadorDeEmprestimo.renovarEmprestimo("sol-dupla", "L1", Cenario.ESTUDANTE, Cenario.ATENDENTE);
        };
        Future<?> f1 = pool.submit(envio);
        Future<?> f2 = pool.submit(envio);
        largada.countDown();
        f1.get();
        f2.get();
        pool.shutdown();

        assertEquals(original.plusDays(30), emprestimo(c).getDataVencimento());
        assertEquals(1, emprestimo(c).getNumRenovacoes());
    }
}
