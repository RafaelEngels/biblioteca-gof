package br.pucpr.biblioteca.persistencia;

import br.pucpr.biblioteca.dominio.Devolucao;
import br.pucpr.biblioteca.dominio.Emprestimo;
import br.pucpr.biblioteca.dominio.Reserva;
import br.pucpr.biblioteca.multa.Multa;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MovimentacaoEmMemoria implements RepositorioMovimentacao {
    private final UnidadeEmMemoria unidade;
    private final Map<String, Reserva> reservas = new HashMap<>();
    private final Map<String, Emprestimo> emprestimos = new HashMap<>();
    private final Map<String, Devolucao> devolucoes = new HashMap<>();
    private final Map<String, Multa> multas = new LinkedHashMap<>();

    MovimentacaoEmMemoria(UnidadeEmMemoria unidade) {
        this.unidade = unidade;
    }

    public UnidadeEmMemoria unidade() { return unidade; }

    private static String chave(String idLivro, String idEstudante) { return idLivro + "|" + idEstudante; }

    public void adicionarReserva(Reserva r) { reservas.put(chave(r.getIdLivro(), r.getIdEstudante()), r); }
    public Optional<Reserva> buscarReserva(String l, String e) { return Optional.ofNullable(reservas.get(chave(l, e))); }
    public void removerReserva(Reserva r) { reservas.remove(chave(r.getIdLivro(), r.getIdEstudante())); }

    public void adicionarEmprestimo(Emprestimo e) { emprestimos.put(chave(e.getIdLivro(), e.getIdEstudante()), e); }
    public Optional<Emprestimo> buscarEmprestimo(String l, String e) { return Optional.ofNullable(emprestimos.get(chave(l, e))); }
    public void atualizarEmprestimo(Emprestimo e) { /* o objeto ja e o mesmo da memoria */ }
    public void removerEmprestimo(Emprestimo e) { emprestimos.remove(chave(e.getIdLivro(), e.getIdEstudante())); }

    public void adicionarDevolucao(Devolucao d) { devolucoes.put(chave(d.getIdLivro(), d.getIdEstudante()), d); }
    public Optional<Devolucao> buscarDevolucao(String l, String e) { return Optional.ofNullable(devolucoes.get(chave(l, e))); }

    public void removerDevolucao(Devolucao d) { devolucoes.remove(chave(d.getIdLivro(), d.getIdEstudante())); }

    public void salvarMulta(Multa m) { multas.put(m.getId(), m); }
    public Optional<Multa> buscarMulta(String id) { return Optional.ofNullable(multas.get(id)); }

    public Optional<Multa> buscarMultaPorIdExterno(String idExterno) {
        return multas.values().stream().filter(m -> idExterno.equals(m.getIdExterno())).findFirst();
    }

    public List<Multa> listarMultas() { return new ArrayList<>(multas.values()); }
}
