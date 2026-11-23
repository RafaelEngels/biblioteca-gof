package br.pucpr.biblioteca.persistencia;

import br.pucpr.biblioteca.dominio.Atendente;
import br.pucpr.biblioteca.dominio.Estudante;
import br.pucpr.biblioteca.dominio.Pessoa;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RelacionamentoEmMemoria implements RepositorioRelacionamento {
    private final UnidadeEmMemoria unidade;
    private final Map<String, Pessoa> pessoas = new HashMap<>();
    private final Map<String, Estudante> estudantes = new HashMap<>();
    private final Map<String, Atendente> atendentes = new HashMap<>();

    RelacionamentoEmMemoria(UnidadeEmMemoria unidade) {
        this.unidade = unidade;
    }

    public UnidadeEmMemoria unidade() { return unidade; }

    public void salvarPessoa(Pessoa p) { pessoas.put(p.getCpf(), p); }
    public Optional<Pessoa> buscarPessoa(String cpf) { return Optional.ofNullable(pessoas.get(cpf)); }

    public void salvarEstudante(Estudante e) { estudantes.put(e.getMatricula(), e); }
    public Optional<Estudante> buscarEstudante(String m) { return Optional.ofNullable(estudantes.get(m)); }

    public void salvarAtendente(Atendente a) { atendentes.put(a.getMatriculaFuncional(), a); }
    public Optional<Atendente> buscarAtendente(String m) { return Optional.ofNullable(atendentes.get(m)); }
}
