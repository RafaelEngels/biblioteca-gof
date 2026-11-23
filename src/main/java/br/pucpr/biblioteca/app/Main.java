package br.pucpr.biblioteca.app;

import br.pucpr.biblioteca.banco.SimuladorDeBanco;
import java.util.Scanner;
import java.util.UUID;

// interface de linha de comando: so chama tratadores, nenhuma regra de dominio aqui
public class Main {
    public static void main(String[] args) {
        Aplicacao app = new Aplicacao(Configuracao.doAmbiente(), System.out::println);
        if (args.length > 0 && args[0].equals("demo")) {
            demo(app);
            return;
        }
        System.out.println("Biblioteca (perfil " + app.configuracao.perfil() + "). Digite 'ajuda'.");
        Scanner in = new Scanner(System.in);
        while (in.hasNextLine()) {
            String linha = in.nextLine().trim();
            if (linha.equals("sair")) break;
            if (linha.isEmpty()) continue;
            try {
                executar(app, linha);
            } catch (RuntimeException e) {
                System.out.println("ERRO: " + e.getMessage());
            }
        }
    }

    static void executar(Aplicacao app, String linha) {
        String[] p = linha.split("\\s+", 5);
        switch (p[0]) {
            case "ajuda" -> System.out.println("""
                    pessoa <cpf> <nome> <sobrenome> <email>
                    estudante <cpf> <matricula>
                    atendente <cpf> <matriculaFuncional>
                    livro <id> <titulo>
                    reservar|emprestar|renovar|devolver <livro> <estudante> <atendente>
                    inspecionar <livro> <bom|ruim>
                    consultar <livro>
                    avancar <dias>
                    multas
                    pagar <idMulta>
                    demo | sair""");
            case "pessoa" -> app.tratadorDePessoa.inserirPessoa(p[1], p[2], p[3], p[4]);
            case "estudante" -> app.tratadorDeEstudante.inserirEstudante(p[1], p[2]);
            case "atendente" -> app.tratadorDeAtendente.inserirAtendente(p[1], p[2]);
            case "livro" -> app.tratadorDeLivro.inserirLivro(p[1], linha.substring(linha.indexOf(p[1]) + p[1].length()).trim());
            case "reservar" -> System.out.println(app.tratadorDeEmprestimo.reservarLivro(UUID.randomUUID().toString(), p[1], p[2], p[3]));
            case "emprestar" -> System.out.println(app.tratadorDeEmprestimo.emprestarLivro(p[1], p[2], p[3]));
            case "renovar" -> System.out.println(app.tratadorDeEmprestimo.renovarEmprestimo(UUID.randomUUID().toString(), p[1], p[2], p[3]));
            case "devolver" -> System.out.println(app.tratadorDeDevolucao.registrarDevolucao(p[1], p[2], p[3]));
            case "inspecionar" -> System.out.println(app.tratadorDeDevolucao.inspecionar(p[1], p[2].equals("bom")));
            case "consultar" -> System.out.println(app.tratadorDeAcervo.consultarLivro(p[1]) + " estado=" + app.tratadorDeAcervo.estadoDoLivro(p[1]).orElse("?"));
            case "avancar" -> app.relogio.avancarDias(Long.parseLong(p[1]));
            case "multas" -> app.tratadorDeMulta.listarMultas().forEach(m ->
                    System.out.println(m.getId() + " livro=" + m.getIdLivro() + " dias=" + m.getDiasAtraso()
                            + " valor=" + m.getValor() + " politica=" + m.getPoliticaAplicada() + " " + m.getSituacao()
                            + " banco=" + m.getIdExterno()));
            case "pagar" -> {
                String idExterno = app.tratadorDeMulta.consultarMulta(p[1]).orElseThrow().getIdExterno();
                boolean quitou = app.tratadorDeMulta.registrarQuitacao(SimuladorDeBanco.notificacaoDePagamento(app.configuracao.banco(), idExterno));
                System.out.println(quitou ? "multa quitada" : "notificacao ignorada");
            }
            case "demo" -> demo(app);
            default -> System.out.println("comando desconhecido, digite 'ajuda'");
        }
    }

    // fluxo completo: reserva, emprestimo, renovacao, devolucao atrasada, multa e quitacao
    static void demo(Aplicacao app) {
        String[] passos = {
                "pessoa 11144477735 Ana Silva ana@exemplo.com",
                "pessoa 52998224725 Bruno Souza bruno@exemplo.com",
                "estudante 11144477735 E001",
                "atendente 52998224725 A001",
                "livro L1 Engenharia de Software",
                "reservar L1 E001 A001",
                "emprestar L1 E001 A001",
                "renovar L1 E001 A001",
                "avancar 70",
                "devolver L1 E001 A001",
                "multas",
                "inspecionar L1 bom",
                "consultar L1"
        };
        for (String passo : passos) {
            System.out.println("> " + passo);
            executar(app, passo);
        }
        System.out.println("> pagar (multa emitida)");
        String id = app.tratadorDeMulta.listarMultas().get(0).getId();
        executar(app, "pagar " + id);
        executar(app, "multas");
    }
}
