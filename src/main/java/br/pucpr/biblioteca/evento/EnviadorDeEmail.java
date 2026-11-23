package br.pucpr.biblioteca.evento;

public interface EnviadorDeEmail {
    void enviar(String para, String assunto, String corpo);
}
