package br.pucpr.biblioteca.tratador;

import br.pucpr.biblioteca.circulacao.Comprovante;
import br.pucpr.biblioteca.circulacao.FachadaDeCirculacao;

public class TratadorDeDevolucao {
    private final FachadaDeCirculacao fachada;

    public TratadorDeDevolucao(FachadaDeCirculacao fachada) {
        this.fachada = fachada;
    }

    public Comprovante registrarDevolucao(String idLivro, String idEstudante, String idAtendente) {
        return fachada.registrarDevolucao(idLivro, idEstudante, idAtendente);
    }

    public Comprovante inspecionar(String idLivro, boolean bomEstado) {
        return fachada.inspecionar(idLivro, bomEstado);
    }

    public Comprovante retornarDoReparo(String idLivro) { return fachada.retornarDoReparo(idLivro); }

    public Comprovante registrarLaudoIrrecuperavel(String idLivro) { return fachada.registrarLaudoIrrecuperavel(idLivro); }
}
