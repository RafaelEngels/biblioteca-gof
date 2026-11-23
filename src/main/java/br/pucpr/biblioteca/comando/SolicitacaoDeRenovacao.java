package br.pucpr.biblioteca.comando;

import br.pucpr.biblioteca.circulacao.Comprovante;
import br.pucpr.biblioteca.circulacao.FachadaDeCirculacao;
import java.time.Instant;

public record SolicitacaoDeRenovacao(String id, String autor, Instant criadaEm, String idLivro, String idEstudante)
        implements SolicitacaoDeCirculacao {

    public Comprovante executar(FachadaDeCirculacao fachada) {
        return fachada.renovar(idLivro, idEstudante);
    }
}
