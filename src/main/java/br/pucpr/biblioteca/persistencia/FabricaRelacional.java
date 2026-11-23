package br.pucpr.biblioteca.persistencia;

import java.sql.Connection;
import java.util.function.Supplier;

// familia de producao (MySQL); os tres repositorios nascem com a mesma UnidadeJdbc
public class FabricaRelacional implements FabricaDeAcesso {
    private final UnidadeJdbc unidade;
    private final LivrosJdbc livros;
    private final MovimentacaoJdbc movimentacao;
    private final RelacionamentoJdbc relacionamento;

    public FabricaRelacional(Supplier<Connection> origemDaConexao) {
        this.unidade = new UnidadeJdbc(origemDaConexao);
        this.livros = new LivrosJdbc(unidade);
        this.movimentacao = new MovimentacaoJdbc(unidade);
        this.relacionamento = new RelacionamentoJdbc(unidade);
    }

    public UnidadeDeTrabalho unidade() { return unidade; }
    public RepositorioLivro livros() { return livros; }
    public RepositorioMovimentacao movimentacao() { return movimentacao; }
    public RepositorioRelacionamento relacionamento() { return relacionamento; }
}
