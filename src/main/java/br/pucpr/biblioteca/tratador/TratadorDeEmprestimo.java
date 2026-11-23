package br.pucpr.biblioteca.tratador;

import br.pucpr.biblioteca.circulacao.Comprovante;
import br.pucpr.biblioteca.circulacao.FachadaDeCirculacao;
import br.pucpr.biblioteca.comando.ExecutorDeSolicitacoes;
import br.pucpr.biblioteca.comando.SolicitacaoDeRenovacao;
import br.pucpr.biblioteca.comando.SolicitacaoDeReserva;
import java.time.Instant;
import java.util.Optional;

// o id da solicitacao vem da tela (um por formulario), assim reenviar o mesmo formulario nao duplica
public class TratadorDeEmprestimo {
    private final FachadaDeCirculacao fachada;
    private final ExecutorDeSolicitacoes executor;

    public TratadorDeEmprestimo(FachadaDeCirculacao fachada, ExecutorDeSolicitacoes executor) {
        this.fachada = fachada;
        this.executor = executor;
    }

    public Comprovante reservarLivro(String idSolicitacao, String idLivro, String idEstudante, String idAtendente) {
        return executor.executar(new SolicitacaoDeReserva(idSolicitacao, idAtendente, Instant.now(), idLivro, idEstudante));
    }

    public Comprovante renovarEmprestimo(String idSolicitacao, String idLivro, String idEstudante, String idAtendente) {
        return executor.executar(new SolicitacaoDeRenovacao(idSolicitacao, idAtendente, Instant.now(), idLivro, idEstudante));
    }

    public Comprovante emprestarLivro(String idLivro, String idEstudante, String idAtendente) {
        return fachada.efetivarEmprestimo(idLivro, idEstudante, idAtendente);
    }

    public Optional<Comprovante> consultarReserva(String idLivro, String idEstudante) {
        return fachada.consultarReserva(idLivro, idEstudante);
    }
}
