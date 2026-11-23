package br.pucpr.biblioteca.app;

import br.pucpr.biblioteca.banco.AdaptadorBancoA;
import br.pucpr.biblioteca.banco.AdaptadorBancoB;
import br.pucpr.biblioteca.banco.BancoAApi;
import br.pucpr.biblioteca.banco.BancoBApi;
import br.pucpr.biblioteca.banco.ContratoCobranca;
import br.pucpr.biblioteca.cadastro.CadastroAtendente;
import br.pucpr.biblioteca.cadastro.CadastroEstudante;
import br.pucpr.biblioteca.cadastro.CadastroLivro;
import br.pucpr.biblioteca.cadastro.CadastroPessoa;
import br.pucpr.biblioteca.circulacao.FachadaDeCirculacao;
import br.pucpr.biblioteca.circulacao.RelogioAjustavel;
import br.pucpr.biblioteca.comando.ExecutorDeSolicitacoes;
import br.pucpr.biblioteca.consulta.ConsultaAcervo;
import br.pucpr.biblioteca.consulta.ConsultaAcervoOriginal;
import br.pucpr.biblioteca.consulta.ConsultaComCache;
import br.pucpr.biblioteca.consulta.ConsultaComMedicao;
import br.pucpr.biblioteca.evento.EnviadorDeEmail;
import br.pucpr.biblioteca.evento.FilaDeInspecao;
import br.pucpr.biblioteca.evento.Indicadores;
import br.pucpr.biblioteca.evento.OuvinteEmail;
import br.pucpr.biblioteca.evento.OuvinteIndicadores;
import br.pucpr.biblioteca.evento.OuvinteInspecao;
import br.pucpr.biblioteca.evento.OuvinteMulta;
import br.pucpr.biblioteca.evento.PublicadorDeDevolucao;
import br.pucpr.biblioteca.multa.ConfiguracaoAdministrativa;
import br.pucpr.biblioteca.multa.PoliticaComTeto;
import br.pucpr.biblioteca.multa.PoliticaComTolerancia;
import br.pucpr.biblioteca.multa.PoliticaDeMulta;
import br.pucpr.biblioteca.multa.PoliticaOriginal;
import br.pucpr.biblioteca.multa.ServicoDeMulta;
import br.pucpr.biblioteca.persistencia.Esquema;
import br.pucpr.biblioteca.persistencia.FabricaDeAcesso;
import br.pucpr.biblioteca.persistencia.FabricaEmMemoria;
import br.pucpr.biblioteca.persistencia.FabricaRelacional;
import br.pucpr.biblioteca.tratador.TratadorDeAcervo;
import br.pucpr.biblioteca.tratador.TratadorDeAtendente;
import br.pucpr.biblioteca.tratador.TratadorDeDevolucao;
import br.pucpr.biblioteca.tratador.TratadorDeEmprestimo;
import br.pucpr.biblioteca.tratador.TratadorDeEstudante;
import br.pucpr.biblioteca.tratador.TratadorDeLivro;
import br.pucpr.biblioteca.tratador.TratadorDeMulta;
import br.pucpr.biblioteca.tratador.TratadorDePessoa;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

// raiz de composicao: aqui (e so aqui) as pecas sao escolhidas e ligadas
public class Aplicacao {
    public final Configuracao configuracao;
    public final FabricaDeAcesso fabrica;
    public final RelogioAjustavel relogio = new RelogioAjustavel(LocalDate.of(2026, 11, 23));
    public final PublicadorDeDevolucao publicador = new PublicadorDeDevolucao();
    public final FilaDeInspecao filaDeInspecao = new FilaDeInspecao();
    public final Indicadores indicadores = new Indicadores();
    public final List<String> emailsEnviados = new ArrayList<>();
    public final BancoAApi bancoA = new BancoAApi();
    public final BancoBApi bancoB = new BancoBApi();
    public final ConfiguracaoAdministrativa configuracaoAdministrativa;
    public final ServicoDeMulta servicoDeMulta;
    public final FachadaDeCirculacao fachada;
    public final ExecutorDeSolicitacoes executor;
    public final ConsultaComMedicao consulta;

    public final TratadorDeLivro tratadorDeLivro;
    public final TratadorDePessoa tratadorDePessoa;
    public final TratadorDeEstudante tratadorDeEstudante;
    public final TratadorDeAtendente tratadorDeAtendente;
    public final TratadorDeEmprestimo tratadorDeEmprestimo;
    public final TratadorDeDevolucao tratadorDeDevolucao;
    public final TratadorDeMulta tratadorDeMulta;
    public final TratadorDeAcervo tratadorDeAcervo;

    public Aplicacao(Configuracao config, Consumer<String> saida) {
        this(config, saida, null);
    }

    public Aplicacao(Configuracao config, Consumer<String> saida, EnviadorDeEmail enviadorDeEmail) {
        this.configuracao = config;
        this.fabrica = escolherFabrica(config); // escolhida uma vez so, aqui

        this.configuracaoAdministrativa = new ConfiguracaoAdministrativa(escolherPolitica(config.politica()));
        this.servicoDeMulta = new ServicoDeMulta(configuracaoAdministrativa, escolherBanco(config.banco()), fabrica);

        EnviadorDeEmail enviador = enviadorDeEmail != null ? enviadorDeEmail
                : (para, assunto, corpo) -> emailsEnviados.add(para + " | " + assunto);
        publicador.registrar(new OuvinteInspecao(filaDeInspecao));
        publicador.registrar(new OuvinteMulta(servicoDeMulta));
        publicador.registrar(new OuvinteIndicadores(indicadores));
        publicador.registrar(new OuvinteEmail(enviador));

        this.fachada = new FachadaDeCirculacao(fabrica, publicador, relogio);
        this.executor = new ExecutorDeSolicitacoes(fachada);

        // ordem importa: medicao por fora pra contar tambem as consultas atendidas pelo cache
        this.consulta = new ConsultaComMedicao(new ConsultaComCache(new ConsultaAcervoOriginal(fabrica.livros())));
        ConsultaAcervo consultaDoAcervo = consulta;

        this.tratadorDeLivro = new TratadorDeLivro(new CadastroLivro(fabrica.livros(), saida));
        this.tratadorDePessoa = new TratadorDePessoa(new CadastroPessoa(fabrica.relacionamento(), saida));
        this.tratadorDeEstudante = new TratadorDeEstudante(new CadastroEstudante(fabrica.relacionamento(), saida));
        this.tratadorDeAtendente = new TratadorDeAtendente(new CadastroAtendente(fabrica.relacionamento(), saida));
        this.tratadorDeEmprestimo = new TratadorDeEmprestimo(fachada, executor);
        this.tratadorDeDevolucao = new TratadorDeDevolucao(fachada);
        this.tratadorDeMulta = new TratadorDeMulta(servicoDeMulta);
        this.tratadorDeAcervo = new TratadorDeAcervo(consultaDoAcervo);
    }

    static FabricaDeAcesso escolherFabrica(Configuracao config) {
        return switch (config.perfil()) {
            case TESTE -> new FabricaEmMemoria();
            case PRODUCAO -> {
                FabricaRelacional fabrica = new FabricaRelacional(() -> conectar(config));
                try (Connection c = conectar(config)) {
                    Esquema.aplicar(c);
                } catch (SQLException e) {
                    throw new IllegalStateException("nao deu pra conectar no banco", e);
                }
                yield fabrica;
            }
        };
    }

    private static Connection conectar(Configuracao config) {
        try {
            return DriverManager.getConnection(config.jdbcUrl(), config.usuario(), config.senha());
        } catch (SQLException e) {
            throw new IllegalStateException("falha ao conectar: " + e.getMessage(), e);
        }
    }

    static PoliticaDeMulta escolherPolitica(String nome) {
        return switch (nome) {
            case "COM_TETO" -> new PoliticaComTeto();
            case "COM_TOLERANCIA" -> new PoliticaComTolerancia();
            default -> new PoliticaOriginal();
        };
    }

    private ContratoCobranca escolherBanco(String banco) {
        return "B".equals(banco) ? new AdaptadorBancoB(bancoB) : new AdaptadorBancoA(bancoA);
    }
}
