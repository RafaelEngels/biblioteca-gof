package br.pucpr.biblioteca.circulacao;

import java.time.LocalDate;
import java.util.function.Supplier;

// relogio que da pra adiantar, pra simular atraso nos testes e na demo
public class RelogioAjustavel implements Supplier<LocalDate> {
    private LocalDate hoje;

    public RelogioAjustavel(LocalDate inicio) {
        this.hoje = inicio;
    }

    public synchronized LocalDate get() { return hoje; }

    public synchronized void avancarDias(long dias) { hoje = hoje.plusDays(dias); }
}
