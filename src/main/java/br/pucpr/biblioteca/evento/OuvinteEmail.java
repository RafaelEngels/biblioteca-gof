package br.pucpr.biblioteca.evento;

public class OuvinteEmail implements OuvinteDeDevolucao {
    private final EnviadorDeEmail enviador;

    public OuvinteEmail(EnviadorDeEmail enviador) {
        this.enviador = enviador;
    }

    public void aoDevolver(EventoDevolucao e) {
        String corpo = "Devolucao do livro " + e.idLivro() + " registrada em " + e.dataDevolucao()
                + ". Dias de atraso: " + e.diasAtraso() + ".";
        enviador.enviar(e.email(), "Devolucao registrada", corpo);
    }
}
