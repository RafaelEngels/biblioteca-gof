package br.pucpr.biblioteca.evento;

// so coloca o livro na fila; o livro continua DEVOLVIDO ate alguem decidir a inspecao
public class OuvinteInspecao implements OuvinteDeDevolucao {
    private final FilaDeInspecao fila;

    public OuvinteInspecao(FilaDeInspecao fila) {
        this.fila = fila;
    }

    public void aoDevolver(EventoDevolucao evento) {
        fila.entrar(evento.idLivro());
    }
}
