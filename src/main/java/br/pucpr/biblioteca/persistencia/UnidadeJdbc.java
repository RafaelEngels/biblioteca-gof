package br.pucpr.biblioteca.persistencia;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Supplier;

// uma conexao so; todos os repositorios da familia usam ela, entao entram na mesma transacao
public class UnidadeJdbc implements UnidadeDeTrabalho {
    private final Supplier<Connection> origem;
    private final Deque<Runnable> desfazimentos = new ArrayDeque<>();
    private Connection conexao;

    public UnidadeJdbc(Supplier<Connection> origem) {
        this.origem = origem;
    }

    // so conecta quando alguem precisa (assim da pra montar a fabrica sem banco no ar)
    public synchronized Connection conexao() {
        if (conexao == null) {
            conexao = origem.get();
        }
        return conexao;
    }

    public void iniciar() {
        desfazimentos.clear();
        try {
            conexao().setAutoCommit(false);
        } catch (SQLException e) {
            throw new IllegalStateException("falha ao iniciar transacao", e);
        }
    }

    public void confirmar() {
        try {
            conexao().commit();
            desfazimentos.clear();
            conexao().setAutoCommit(true); // gravacoes fora de transacao (cadastros) voltam a dar commit sozinhas
        } catch (SQLException e) {
            throw new IllegalStateException("falha no commit", e);
        }
    }

    public void desfazer() {
        try {
            conexao().rollback();
            conexao().setAutoCommit(true);
        } catch (SQLException e) {
            throw new IllegalStateException("falha no rollback", e);
        } finally {
            while (!desfazimentos.isEmpty()) {
                desfazimentos.pop().run();
            }
        }
    }

    public void aoDesfazer(Runnable acao) { desfazimentos.push(acao); }
}
