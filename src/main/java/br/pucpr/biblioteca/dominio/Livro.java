package br.pucpr.biblioteca.dominio;

import br.pucpr.biblioteca.estado.EstadoLivre;
import br.pucpr.biblioteca.estado.EstadoLivro;

public class Livro implements iLivro {
    private final String id;
    private String titulo;
    private EstadoLivro estado;

    public Livro(String id, String titulo) {
        this(id, titulo, new EstadoLivre());
    }

    public Livro(String id, String titulo, EstadoLivro estado) {
        this.id = id;
        this.titulo = titulo;
        this.estado = estado;
    }

    // reconstitui o livro a partir do nome do estado gravado no banco
    public static Livro reconstituir(String id, String titulo, String nomeEstado) {
        return new Livro(id, titulo, EstadoLivro.de(nomeEstado));
    }

    // o livro so delega, quem decide e o objeto de estado
    public boolean reservar() { return estado.reservar(this); }
    public boolean emprestar() { return estado.emprestar(this); }
    public boolean renovar(Emprestimo emprestimo) { return estado.renovar(this, emprestimo); }
    public boolean devolver() { return estado.devolver(this); }
    public boolean aprovarInspecao() { return estado.aprovarInspecao(this); }
    public boolean enviarParaReparo() { return estado.enviarParaReparo(this); }
    public boolean retornarDoReparo() { return estado.retornarDoReparo(this); }
    public boolean registrarLaudoIrrecuperavel() { return estado.registrarLaudoIrrecuperavel(this); }

    public void setEstado(EstadoLivro novo) { this.estado = novo; }
    public String getNomeEstado() { return estado.nome(); }
    public String getId() { return id; }
    public String getTitulo() { return titulo; }
    public void atualizarTitulo(String titulo) { this.titulo = titulo; }
}
