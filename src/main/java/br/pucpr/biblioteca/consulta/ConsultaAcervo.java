package br.pucpr.biblioteca.consulta;

import java.util.Optional;

public interface ConsultaAcervo {
    Optional<MetadadosLivro> metadados(String idLivro);

    // sempre o estado de agora, nunca vem de cache
    Optional<String> estadoAtual(String idLivro);
}
