package br.pucpr.biblioteca.persistencia;

import br.pucpr.biblioteca.dominio.Atendente;
import br.pucpr.biblioteca.dominio.Estudante;
import br.pucpr.biblioteca.dominio.Pessoa;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class RelacionamentoJdbc implements RepositorioRelacionamento {
    private final UnidadeJdbc unidade;

    RelacionamentoJdbc(UnidadeJdbc unidade) {
        this.unidade = unidade;
    }

    public UnidadeJdbc unidade() { return unidade; }

    public void salvarPessoa(Pessoa p) {
        String sql = "INSERT INTO pessoa (cpf, nome, sobrenome, email) VALUES (?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE nome = VALUES(nome), sobrenome = VALUES(sobrenome), email = VALUES(email)";
        try (PreparedStatement ps = unidade.conexao().prepareStatement(sql)) {
            ps.setString(1, p.getCpf());
            ps.setString(2, p.getNome());
            ps.setString(3, p.getSobrenome());
            ps.setString(4, p.getEmail());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    public Optional<Pessoa> buscarPessoa(String cpf) {
        try (PreparedStatement ps = unidade.conexao().prepareStatement("SELECT cpf, nome, sobrenome, email FROM pessoa WHERE cpf = ?")) {
            ps.setString(1, cpf);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(new Pessoa(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4)));
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    public void salvarEstudante(Estudante e) {
        String sql = "INSERT INTO estudante (matricula, cpf) VALUES (?, ?) ON DUPLICATE KEY UPDATE cpf = VALUES(cpf)";
        try (PreparedStatement ps = unidade.conexao().prepareStatement(sql)) {
            ps.setString(1, e.getMatricula());
            ps.setString(2, e.getPessoa().getCpf());
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public Optional<Estudante> buscarEstudante(String matricula) {
        try (PreparedStatement ps = unidade.conexao().prepareStatement("SELECT cpf FROM estudante WHERE matricula = ?")) {
            ps.setString(1, matricula);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return buscarPessoa(rs.getString(1)).map(p -> new Estudante(matricula, p));
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    public void salvarAtendente(Atendente a) {
        String sql = "INSERT INTO atendente (matricula_funcional, cpf) VALUES (?, ?) ON DUPLICATE KEY UPDATE cpf = VALUES(cpf)";
        try (PreparedStatement ps = unidade.conexao().prepareStatement(sql)) {
            ps.setString(1, a.getMatriculaFuncional());
            ps.setString(2, a.getPessoa().getCpf());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    public Optional<Atendente> buscarAtendente(String matriculaFuncional) {
        try (PreparedStatement ps = unidade.conexao().prepareStatement("SELECT cpf FROM atendente WHERE matricula_funcional = ?")) {
            ps.setString(1, matriculaFuncional);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return buscarPessoa(rs.getString(1)).map(p -> new Atendente(matriculaFuncional, p));
            }
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }
}
