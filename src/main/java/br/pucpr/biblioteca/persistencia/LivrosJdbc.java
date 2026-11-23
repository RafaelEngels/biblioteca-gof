package br.pucpr.biblioteca.persistencia;

import br.pucpr.biblioteca.dominio.Livro;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LivrosJdbc implements RepositorioLivro {
    private final UnidadeJdbc unidade;

    LivrosJdbc(UnidadeJdbc unidade) {
        this.unidade = unidade;
    }

    public UnidadeJdbc unidade() { return unidade; }

    public void salvar(Livro livro) {
        String sql = "INSERT INTO livro (id, titulo, estado) VALUES (?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE titulo = VALUES(titulo), estado = VALUES(estado)";
        try (PreparedStatement ps = unidade.conexao().prepareStatement(sql)) {
            ps.setString(1, livro.getId());
            ps.setString(2, livro.getTitulo());
            ps.setString(3, livro.getNomeEstado());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    public Optional<Livro> buscarPorId(String id) {
        try (PreparedStatement ps = unidade.conexao().prepareStatement("SELECT id, titulo, estado FROM livro WHERE id = ?")) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(Livro.reconstituir(rs.getString("id"), rs.getString("titulo"), rs.getString("estado")));
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    public List<Livro> listar() {
        List<Livro> lista = new ArrayList<>();
        try (PreparedStatement ps = unidade.conexao().prepareStatement("SELECT id, titulo, estado FROM livro ORDER BY id");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(Livro.reconstituir(rs.getString("id"), rs.getString("titulo"), rs.getString("estado")));
            }
            return lista;
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }
}
