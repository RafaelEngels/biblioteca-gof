package br.pucpr.biblioteca.consulta;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

// cache so de metadados; estadoAtual passa direto
public class ConsultaComCache implements ConsultaAcervo {
    private final ConsultaAcervo proxima;
    private final Map<String, MetadadosLivro> cache;
    private final AtomicInteger acertos = new AtomicInteger();

    public ConsultaComCache(ConsultaAcervo proxima) {
        this(proxima, new ConcurrentHashMap<>());
    }

    public ConsultaComCache(ConsultaAcervo proxima, Map<String, MetadadosLivro> cache) {
        this.proxima = proxima;
        this.cache = cache;
    }

    public Optional<MetadadosLivro> metadados(String idLivro) {
        try {
            MetadadosLivro guardado = cache.get(idLivro);
            if (guardado != null) {
                acertos.incrementAndGet();
                return Optional.of(guardado);
            }
        } catch (RuntimeException e) {
            // cache falhou: segue sem ele
        }
        Optional<MetadadosLivro> resultado = proxima.metadados(idLivro);
        try {
            resultado.ifPresent(m -> cache.put(idLivro, m));
        } catch (RuntimeException e) {
            // ignora, so perde o cache
        }
        return resultado;
    }

    public Optional<String> estadoAtual(String idLivro) { return proxima.estadoAtual(idLivro); }

    public int acertosDeCache() { return acertos.get(); }
}
