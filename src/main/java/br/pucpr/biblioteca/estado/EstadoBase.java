package br.pucpr.biblioteca.estado;

import br.pucpr.biblioteca.dominio.Emprestimo;
import br.pucpr.biblioteca.dominio.Livro;

// tudo recusado por padrao; cada estado sobrescreve so o que aceita
abstract class EstadoBase implements EstadoLivro {
    public boolean reservar(Livro livro) { return false; }
    public boolean emprestar(Livro livro) { return false; }
    public boolean renovar(Livro livro, Emprestimo emprestimo) { return false; }
    public boolean devolver(Livro livro) { return false; }
    public boolean aprovarInspecao(Livro livro) { return false; }
    public boolean enviarParaReparo(Livro livro) { return false; }
    public boolean retornarDoReparo(Livro livro) { return false; }
    public boolean registrarLaudoIrrecuperavel(Livro livro) { return false; }
}
