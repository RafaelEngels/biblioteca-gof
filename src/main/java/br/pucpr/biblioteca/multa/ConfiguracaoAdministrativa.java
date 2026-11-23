package br.pucpr.biblioteca.multa;

// guarda a politica vigente; quem administra troca em tempo de execucao
public class ConfiguracaoAdministrativa {
    private volatile PoliticaDeMulta politicaVigente;

    public ConfiguracaoAdministrativa(PoliticaDeMulta inicial) {
        this.politicaVigente = inicial;
    }

    public PoliticaDeMulta politicaVigente() { return politicaVigente; }

    public void definirPolitica(PoliticaDeMulta nova) { this.politicaVigente = nova; }
}
