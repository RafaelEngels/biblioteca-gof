package br.pucpr.biblioteca.banco;

// simula a API do banco B: valor decimal em reais, cpf formatado, data dd/MM/yyyy
public class BancoBApi {
    private int contador = 0;
    public double ultimoValor;
    public String ultimoCpf;
    public String ultimaData;

    public int registrarCobranca(double valorEmReais, String cpfFormatado, String referencia, String dataBr) {
        this.ultimoValor = valorEmReais;
        this.ultimoCpf = cpfFormatado;
        this.ultimaData = dataBr;
        contador++;
        return contador;
    }
}
