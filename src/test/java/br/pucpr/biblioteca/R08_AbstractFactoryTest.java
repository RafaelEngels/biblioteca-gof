package br.pucpr.biblioteca;

import static org.junit.jupiter.api.Assertions.*;

import br.pucpr.biblioteca.app.Aplicacao;
import br.pucpr.biblioteca.app.Configuracao;
import br.pucpr.biblioteca.app.Perfil;
import br.pucpr.biblioteca.persistencia.Esquema;
import br.pucpr.biblioteca.persistencia.FabricaEmMemoria;
import br.pucpr.biblioteca.persistencia.FabricaRelacional;
import br.pucpr.biblioteca.persistencia.LivrosEmMemoria;
import br.pucpr.biblioteca.persistencia.LivrosJdbc;
import br.pucpr.biblioteca.persistencia.MovimentacaoEmMemoria;
import br.pucpr.biblioteca.persistencia.MovimentacaoJdbc;
import br.pucpr.biblioteca.persistencia.RelacionamentoEmMemoria;
import br.pucpr.biblioteca.persistencia.RelacionamentoJdbc;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class R08_AbstractFactoryTest {

    @Test
    void R08_configuracaoDeTesteFornecaApenasObjetosEmMemoria() {
        Aplicacao app = new Aplicacao(Configuracao.teste(), s -> { });
        assertInstanceOf(FabricaEmMemoria.class, app.fabrica);
        assertInstanceOf(LivrosEmMemoria.class, app.fabrica.livros());
        assertInstanceOf(MovimentacaoEmMemoria.class, app.fabrica.movimentacao());
        assertInstanceOf(RelacionamentoEmMemoria.class, app.fabrica.relacionamento());
    }

    @Test
    void R08_familiaRelacionalCompartilhaAMesmaUnidadeDeTrabalhoSemConectarAinda() {
        AtomicInteger conexoesAbertas = new AtomicInteger();
        FabricaRelacional fabrica = new FabricaRelacional(() -> {
            conexoesAbertas.incrementAndGet();
            throw new IllegalStateException("nao deveria conectar so por criar a fabrica");
        });

        LivrosJdbc livros = assertInstanceOf(LivrosJdbc.class, fabrica.livros());
        MovimentacaoJdbc mov = assertInstanceOf(MovimentacaoJdbc.class, fabrica.movimentacao());
        RelacionamentoJdbc rel = assertInstanceOf(RelacionamentoJdbc.class, fabrica.relacionamento());

        assertSame(fabrica.unidade(), livros.unidade());
        assertSame(fabrica.unidade(), mov.unidade());
        assertSame(fabrica.unidade(), rel.unidade());
        assertEquals(0, conexoesAbertas.get());
    }

    @Test
    void R08_perfilDeProducaoCriaFabricaRelacionalEFluxoCompletoFuncionaNoBanco() throws Exception {
        // H2 em modo MySQL faz o papel do MySQL aqui, so pra provar o SQL e a transacao
        String url = "jdbc:h2:mem:bib" + UUID.randomUUID() + ";MODE=MySQL;DB_CLOSE_DELAY=-1";
        Configuracao config = new Configuracao(Perfil.PRODUCAO, "A", "ORIGINAL", url, "sa", "");

        Cenario c = new Cenario(config, "L1");
        assertInstanceOf(FabricaRelacional.class, c.app.fabrica);

        c.reservarEEmprestar("L1");
        c.renovar("L1");
        c.app.relogio.avancarDias(70);
        c.app.tratadorDeDevolucao.registrarDevolucao("L1", Cenario.ESTUDANTE, Cenario.ATENDENTE);

        assertEquals("DEVOLVIDO", c.estado("L1"));
        assertEquals(1, c.app.tratadorDeMulta.listarMultas().size());
        assertEquals("10.00", c.app.tratadorDeMulta.listarMultas().get(0).getValor().toPlainString());
        assertTrue(c.app.fabrica.movimentacao().buscarEmprestimo("L1", Cenario.ESTUDANTE).isEmpty());
    }

    @Test
    void R08_tudoQueOAppDizTerGravadoEstaCommitadoParaOutraConexao() throws Exception {
        String url = "jdbc:h2:mem:bib" + UUID.randomUUID() + ";MODE=MySQL;DB_CLOSE_DELAY=-1";
        Configuracao config = new Configuracao(Perfil.PRODUCAO, "A", "ORIGINAL", url, "sa", "");
        Cenario c = new Cenario(config, "L1");
        c.reservarEEmprestar("L1");
        c.app.relogio.avancarDias(40);
        c.app.tratadorDeDevolucao.registrarDevolucao("L1", Cenario.ESTUDANTE, Cenario.ATENDENTE);
        var multa = c.app.tratadorDeMulta.listarMultas().get(0);
        c.app.tratadorDeMulta.registrarQuitacao(
                br.pucpr.biblioteca.banco.SimuladorDeBanco.notificacaoDePagamento("A", multa.getIdExterno()));
        c.app.tratadorDeLivro.inserirLivro("L2", "Cadastrado depois de uma transacao");

        // conexao nova: so enxerga o que foi commitado
        try (Connection outra = DriverManager.getConnection(url, "sa", "");
             var st = outra.createStatement()) {
            var rs = st.executeQuery("SELECT situacao FROM multa");
            assertTrue(rs.next());
            assertEquals("QUITADA", rs.getString(1));
            rs = st.executeQuery("SELECT COUNT(*) FROM livro");
            rs.next();
            assertEquals(2, rs.getInt(1));
            rs = st.executeQuery("SELECT estado FROM livro WHERE id = 'L1'");
            rs.next();
            assertEquals("DEVOLVIDO", rs.getString(1));
        }
    }

    @Test
    void R08_esquemaPodeSerAplicadoDuasVezes() throws Exception {
        try (Connection conexao = DriverManager.getConnection("jdbc:h2:mem:esq" + UUID.randomUUID() + ";MODE=MySQL", "sa", "")) {
            Esquema.aplicar(conexao);
            Esquema.aplicar(conexao);
        }
    }
}
