package br.pucpr.biblioteca.persistencia;

// uma familia inteira: unidade de trabalho + os tres repositorios
public interface FabricaDeAcesso {
    UnidadeDeTrabalho unidade();
    RepositorioLivro livros();
    RepositorioMovimentacao movimentacao();
    RepositorioRelacionamento relacionamento();
}
