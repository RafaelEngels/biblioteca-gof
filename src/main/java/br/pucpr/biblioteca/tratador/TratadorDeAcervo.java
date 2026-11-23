package br.pucpr.biblioteca.tratador;

import br.pucpr.biblioteca.consulta.ConsultaAcervo;
import br.pucpr.biblioteca.consulta.MetadadosLivro;
import java.util.Optional;

public class TratadorDeAcervo {
    private final ConsultaAcervo consulta;

    public TratadorDeAcervo(ConsultaAcervo consulta) {
        this.consulta = consulta;
    }

    public Optional<MetadadosLivro> consultarLivro(String idLivro) { return consulta.metadados(idLivro); }

    public Optional<String> estadoDoLivro(String idLivro) { return consulta.estadoAtual(idLivro); }
}
