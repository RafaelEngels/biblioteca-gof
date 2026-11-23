package br.pucpr.biblioteca.comando;

import br.pucpr.biblioteca.circulacao.Comprovante;
import br.pucpr.biblioteca.circulacao.FachadaDeCirculacao;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// invoker: garante que o mesmo id nunca executa duas vezes (nem com duas submissoes ao mesmo tempo)
public class ExecutorDeSolicitacoes {
    private final FachadaDeCirculacao fachada;
    private final Map<String, Comprovante> jaExecutadas = new ConcurrentHashMap<>();

    public ExecutorDeSolicitacoes(FachadaDeCirculacao fachada) {
        this.fachada = fachada;
    }

    public Comprovante executar(SolicitacaoDeCirculacao solicitacao) {
        // computeIfAbsent e atomico por chave: a segunda chamada espera e reaproveita o resultado
        // se a execucao lancar excecao, nada fica registrado e da pra tentar de novo
        return jaExecutadas.computeIfAbsent(solicitacao.id(), id -> solicitacao.executar(fachada));
    }
}
