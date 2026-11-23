package br.pucpr.biblioteca.comando;

import br.pucpr.biblioteca.circulacao.Comprovante;
import br.pucpr.biblioteca.circulacao.FachadaDeCirculacao;
import java.time.Instant;

public interface SolicitacaoDeCirculacao {
    String id();
    String autor();
    Instant criadaEm();
    Comprovante executar(FachadaDeCirculacao fachada);
}
