package br.pucpr.biblioteca.cadastro;

import br.pucpr.biblioteca.dominio.Pessoa;
import br.pucpr.biblioteca.dominio.RegraDeNegocioException;
import br.pucpr.biblioteca.persistencia.RepositorioRelacionamento;
import java.util.Optional;
import java.util.function.Consumer;

public class CadastroPessoa extends CadastroTemplate<DadosPessoa, Pessoa> {
    private final RepositorioRelacionamento relacionamento;
    private final Consumer<String> saida;

    public CadastroPessoa(RepositorioRelacionamento relacionamento, Consumer<String> saida) {
        this.relacionamento = relacionamento;
        this.saida = saida;
    }

    protected Optional<Pessoa> buscarExistente(DadosPessoa d) { return relacionamento.buscarPessoa(d.cpf()); }

    protected void validar(DadosPessoa d) {
        if (d.cpf() == null || !d.cpf().replaceAll("\\D", "").matches("\\d{11}")) {
            throw new RegraDeNegocioException("cpf invalido");
        }
        if (d.nome() == null || d.nome().isBlank()) {
            throw new RegraDeNegocioException("nome obrigatorio");
        }
    }

    protected void verificarPreRequisitos(DadosPessoa d) {
        // pessoa vem do RH, sem pre-requisito
    }

    protected Pessoa criar(DadosPessoa d) { return new Pessoa(d.cpf(), d.nome(), d.sobrenome(), d.email()); }

    protected void armazenar(Pessoa p) { relacionamento.salvarPessoa(p); }

    protected void apresentar(Pessoa p) { saida.accept("Pessoa " + p.getCpf() + " - " + p.getNome()); }
}
