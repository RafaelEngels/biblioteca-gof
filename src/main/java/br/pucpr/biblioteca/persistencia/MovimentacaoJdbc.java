package br.pucpr.biblioteca.persistencia;

import br.pucpr.biblioteca.dominio.Devolucao;
import br.pucpr.biblioteca.dominio.Emprestimo;
import br.pucpr.biblioteca.dominio.Reserva;
import br.pucpr.biblioteca.multa.Multa;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MovimentacaoJdbc implements RepositorioMovimentacao {
    private final UnidadeJdbc unidade;

    MovimentacaoJdbc(UnidadeJdbc unidade) {
        this.unidade = unidade;
    }

    public UnidadeJdbc unidade() { return unidade; }

    // executa um insert/update/delete simples
    private void executar(String sql, Object... params) {
        try (PreparedStatement ps = unidade.conexao().prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    public void adicionarReserva(Reserva r) {
        executar("INSERT INTO reserva (id_livro, id_estudante, id_atendente, data) VALUES (?, ?, ?, ?)",
                r.getIdLivro(), r.getIdEstudante(), r.getIdAtendente(), r.getData());
    }

    public Optional<Reserva> buscarReserva(String idLivro, String idEstudante) {
        String sql = "SELECT id_atendente, data FROM reserva WHERE id_livro = ? AND id_estudante = ?";
        try (PreparedStatement ps = unidade.conexao().prepareStatement(sql)) {
            ps.setString(1, idLivro);
            ps.setString(2, idEstudante);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(new Reserva(idLivro, idEstudante, rs.getString(1), rs.getObject(2, LocalDate.class)));
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    public void removerReserva(Reserva r) {
        executar("DELETE FROM reserva WHERE id_livro = ? AND id_estudante = ?", r.getIdLivro(), r.getIdEstudante());
    }

    public void adicionarEmprestimo(Emprestimo e) {
        executar("INSERT INTO emprestimo (id_livro, id_estudante, id_atendente, data_emprestimo, data_vencimento, num_renovacoes) "
                        + "VALUES (?, ?, ?, ?, ?, ?)",
                e.getIdLivro(), e.getIdEstudante(), e.getIdAtendente(), e.getDataEmprestimo(), e.getDataVencimento(), e.getNumRenovacoes());
    }

    public Optional<Emprestimo> buscarEmprestimo(String idLivro, String idEstudante) {
        String sql = "SELECT id_atendente, data_emprestimo, data_vencimento, num_renovacoes FROM emprestimo "
                + "WHERE id_livro = ? AND id_estudante = ?";
        try (PreparedStatement ps = unidade.conexao().prepareStatement(sql)) {
            ps.setString(1, idLivro);
            ps.setString(2, idEstudante);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(new Emprestimo(idLivro, idEstudante, rs.getString(1),
                        rs.getObject(2, LocalDate.class), rs.getObject(3, LocalDate.class), rs.getInt(4)));
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    public void atualizarEmprestimo(Emprestimo e) {
        executar("UPDATE emprestimo SET data_vencimento = ?, num_renovacoes = ? WHERE id_livro = ? AND id_estudante = ?",
                e.getDataVencimento(), e.getNumRenovacoes(), e.getIdLivro(), e.getIdEstudante());
    }

    public void removerEmprestimo(Emprestimo e) {
        executar("DELETE FROM emprestimo WHERE id_livro = ? AND id_estudante = ?", e.getIdLivro(), e.getIdEstudante());
    }

    public void adicionarDevolucao(Devolucao d) {
        executar("DELETE FROM devolucao WHERE id_livro = ? AND id_estudante = ?", d.getIdLivro(), d.getIdEstudante());
        executar("INSERT INTO devolucao (id_livro, id_estudante, id_atendente, data_vencimento, data_devolucao) VALUES (?, ?, ?, ?, ?)",
                d.getIdLivro(), d.getIdEstudante(), d.getIdAtendente(), d.getDataVencimento(), d.getDataDevolucao());
    }

    public Optional<Devolucao> buscarDevolucao(String idLivro, String idEstudante) {
        String sql = "SELECT id_atendente, data_vencimento, data_devolucao FROM devolucao WHERE id_livro = ? AND id_estudante = ?";
        try (PreparedStatement ps = unidade.conexao().prepareStatement(sql)) {
            ps.setString(1, idLivro);
            ps.setString(2, idEstudante);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(new Devolucao(idLivro, idEstudante, rs.getString(1),
                        rs.getObject(2, LocalDate.class), rs.getObject(3, LocalDate.class)));
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    public void removerDevolucao(Devolucao d) {
        executar("DELETE FROM devolucao WHERE id_livro = ? AND id_estudante = ?", d.getIdLivro(), d.getIdEstudante());
    }

    public void salvarMulta(Multa m) {
        executar("INSERT INTO multa (id, id_livro, id_estudante, dias_atraso, valor, politica, vencimento_boleto, id_externo, situacao) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) "
                        + "ON DUPLICATE KEY UPDATE id_externo = VALUES(id_externo), situacao = VALUES(situacao)",
                m.getId(), m.getIdLivro(), m.getIdEstudante(), m.getDiasAtraso(), m.getValor(),
                m.getPoliticaAplicada(), m.getVencimentoDoBoleto(), m.getIdExterno(), m.getSituacao());
    }

    private Optional<Multa> buscarMultaPor(String coluna, String valor) {
        String sql = "SELECT id, id_livro, id_estudante, dias_atraso, valor, politica, vencimento_boleto, id_externo, situacao "
                + "FROM multa WHERE " + coluna + " = ?";
        try (PreparedStatement ps = unidade.conexao().prepareStatement(sql)) {
            ps.setString(1, valor);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(lerMulta(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    private Multa lerMulta(ResultSet rs) throws SQLException {
        return new Multa(rs.getString(1), rs.getString(2), rs.getString(3), rs.getLong(4), rs.getBigDecimal(5),
                rs.getString(6), rs.getObject(7, LocalDate.class), rs.getString(8), rs.getString(9));
    }

    public Optional<Multa> buscarMulta(String id) { return buscarMultaPor("id", id); }

    public Optional<Multa> buscarMultaPorIdExterno(String idExterno) { return buscarMultaPor("id_externo", idExterno); }

    public List<Multa> listarMultas() {
        List<Multa> lista = new ArrayList<>();
        String sql = "SELECT id, id_livro, id_estudante, dias_atraso, valor, politica, vencimento_boleto, id_externo, situacao FROM multa ORDER BY id";
        try (PreparedStatement ps = unidade.conexao().prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(lerMulta(rs));
            return lista;
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }
}
