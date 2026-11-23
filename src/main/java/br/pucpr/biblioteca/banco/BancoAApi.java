package br.pucpr.biblioteca.banco;

// simula a API do banco A: valor em centavos, cpf so com digitos, data ISO
public class BancoAApi {
    private int contador = 0;
    public long ultimoValorEmCentavos;
    public String ultimoDocumento;
    public String ultimaData;

    public String criarBoleto(long valorEmCentavos, String documento, String referencia, String dataIso) {
        this.ultimoValorEmCentavos = valorEmCentavos;
        this.ultimoDocumento = documento;
        this.ultimaData = dataIso;
        contador++;
        return "A-" + contador;
    }
}
