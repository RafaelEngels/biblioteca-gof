package br.pucpr.biblioteca.persistencia;

public interface UnidadeDeTrabalho {
    void iniciar();
    void confirmar();
    void desfazer();

    // acao que reverte o objeto em memoria caso a operacao falhe no meio
    void aoDesfazer(Runnable acao);
}
