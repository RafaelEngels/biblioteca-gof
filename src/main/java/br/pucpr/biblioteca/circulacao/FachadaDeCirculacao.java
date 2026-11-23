package br.pucpr.biblioteca.circulacao;

import br.pucpr.biblioteca.dominio.Devolucao;
import br.pucpr.biblioteca.dominio.Emprestimo;
import br.pucpr.biblioteca.dominio.Livro;
import br.pucpr.biblioteca.dominio.RegraDeNegocioException;
import br.pucpr.biblioteca.dominio.Reserva;
import br.pucpr.biblioteca.estado.EstadoLivro;
import br.pucpr.biblioteca.evento.EventoDevolucao;
import br.pucpr.biblioteca.evento.PublicadorDeDevolucao;
import br.pucpr.biblioteca.persistencia.FabricaDeAcesso;
import br.pucpr.biblioteca.persistencia.RepositorioLivro;
import br.pucpr.biblioteca.persistencia.RepositorioMovimentacao;
import br.pucpr.biblioteca.persistencia.RepositorioRelacionamento;
import br.pucpr.biblioteca.persistencia.UnidadeDeTrabalho;
import java.time.LocalDate;
import java.util.Optional;
import java.util.function.Supplier;

// coordena Relacionamento, Movimentacao, Acervo (RepositorioLivro) e Livro.
// cada operacao e uma transacao: ou faz tudo ou desfaz tudo.
public class FachadaDeCirculacao {
    private final UnidadeDeTrabalho unidade;
    private final RepositorioLivro acervo;
    private final RepositorioMovimentacao movimentacao;
    private final RepositorioRelacionamento relacionamento;
    private final PublicadorDeDevolucao publicador;
    private final Supplier<LocalDate> relogio;

    public FachadaDeCirculacao(FabricaDeAcesso fabrica, PublicadorDeDevolucao publicador, Supplier<LocalDate> relogio) {
        this.unidade = fabrica.unidade();
        this.acervo = fabrica.livros();
        this.movimentacao = fabrica.movimentacao();
        this.relacionamento = fabrica.relacionamento();
        this.publicador = publicador;
        this.relogio = relogio;
    }

    public Comprovante reservar(String idLivro, String idEstudante, String idAtendente) {
        return transacao(() -> {
            Livro livro = buscarLivro(idLivro);
            validarPessoas(idEstudante, idAtendente);
            mudarEstado(livro, livro::reservar, "livro nao esta LIVRE, nao pode ser reservado");
            Reserva reserva = new Reserva(idLivro, idEstudante, idAtendente, relogio.get());
            movimentacao.adicionarReserva(reserva);
            unidade.aoDesfazer(() -> movimentacao.removerReserva(reserva));
            acervo.salvar(livro);
            return new Comprovante("RESERVA", idLivro, idEstudante, reserva.getData(), null, livro.getNomeEstado());
        });
    }

    public Comprovante efetivarEmprestimo(String idLivro, String idEstudante, String idAtendente) {
        return transacao(() -> {
            Livro livro = buscarLivro(idLivro);
            validarPessoas(idEstudante, idAtendente);
            // 1) valida a reserva
            Reserva reserva = movimentacao.buscarReserva(idLivro, idEstudante)
                    .orElseThrow(() -> new RegraDeNegocioException("nao existe reserva desse estudante para o livro"));
            // 2) remove a reserva ativa
            movimentacao.removerReserva(reserva);
            unidade.aoDesfazer(() -> movimentacao.adicionarReserva(reserva));
            // 3) cria o emprestimo
            Emprestimo emprestimo = new Emprestimo(idLivro, idEstudante, idAtendente, relogio.get());
            movimentacao.adicionarEmprestimo(emprestimo);
            unidade.aoDesfazer(() -> movimentacao.removerEmprestimo(emprestimo));
            // 4) atualiza o livro (se recusar, os passos 2 e 3 sao desfeitos)
            mudarEstado(livro, livro::emprestar, "livro nao esta RESERVADO, emprestimo recusado");
            acervo.salvar(livro);
            return new Comprovante("EMPRESTIMO", idLivro, idEstudante, emprestimo.getDataEmprestimo(),
                    emprestimo.getDataVencimento(), livro.getNomeEstado());
        });
    }

    public Comprovante renovar(String idLivro, String idEstudante) {
        return transacao(() -> {
            Livro livro = buscarLivro(idLivro);
            Emprestimo emprestimo = movimentacao.buscarEmprestimo(idLivro, idEstudante)
                    .orElseThrow(() -> new RegraDeNegocioException("nao existe emprestimo desse estudante para o livro"));
            LocalDate vencimentoAnterior = emprestimo.getDataVencimento();
            int renovacoesAnteriores = emprestimo.getNumRenovacoes();
            mudarEstado(livro, () -> livro.renovar(emprestimo), "renovacao recusada (limite de 2 ou estado invalido)");
            unidade.aoDesfazer(() -> emprestimo.restaurar(vencimentoAnterior, renovacoesAnteriores));
            movimentacao.atualizarEmprestimo(emprestimo);
            acervo.salvar(livro);
            return new Comprovante("RENOVACAO", idLivro, idEstudante, relogio.get(),
                    emprestimo.getDataVencimento(), livro.getNomeEstado());
        });
    }

    public Comprovante registrarDevolucao(String idLivro, String idEstudante, String idAtendente) {
        EventoDevolucao[] evento = new EventoDevolucao[1];
        Comprovante comprovante = transacao(() -> {
            Livro livro = buscarLivro(idLivro);
            Atendimento at = validarPessoas(idEstudante, idAtendente);
            Emprestimo emprestimo = movimentacao.buscarEmprestimo(idLivro, idEstudante)
                    .orElseThrow(() -> new RegraDeNegocioException("nao existe emprestimo desse estudante para o livro"));
            mudarEstado(livro, livro::devolver, "livro nao pode ser devolvido nesse estado");
            LocalDate hoje = relogio.get();
            Devolucao devolucao = new Devolucao(idLivro, idEstudante, idAtendente, emprestimo.getDataVencimento(), hoje);
            movimentacao.adicionarDevolucao(devolucao);
            unidade.aoDesfazer(() -> movimentacao.removerDevolucao(devolucao));
            movimentacao.removerEmprestimo(emprestimo);
            unidade.aoDesfazer(() -> movimentacao.adicionarEmprestimo(emprestimo));
            acervo.salvar(livro);
            evento[0] = new EventoDevolucao(idLivro, idEstudante, at.cpf, at.email,
                    emprestimo.getDataVencimento(), hoje);
            return new Comprovante("DEVOLUCAO", idLivro, idEstudante, hoje, emprestimo.getDataVencimento(),
                    livro.getNomeEstado());
        });
        // so publica depois que a devolucao foi confirmada
        publicador.publicar(evento[0]);
        return comprovante;
    }

    public Comprovante inspecionar(String idLivro, boolean bomEstado) {
        return transacao(() -> {
            Livro livro = buscarLivro(idLivro);
            if (bomEstado) {
                mudarEstado(livro, livro::aprovarInspecao, "livro nao esta DEVOLVIDO");
            } else {
                mudarEstado(livro, livro::enviarParaReparo, "livro nao esta DEVOLVIDO");
            }
            acervo.salvar(livro);
            return new Comprovante("INSPECAO", idLivro, null, relogio.get(), null, livro.getNomeEstado());
        });
    }

    public Comprovante retornarDoReparo(String idLivro) {
        return transacao(() -> {
            Livro livro = buscarLivro(idLivro);
            mudarEstado(livro, livro::retornarDoReparo, "livro nao esta REPARANDO");
            acervo.salvar(livro);
            return new Comprovante("RETORNO_DO_REPARO", idLivro, null, relogio.get(), null, livro.getNomeEstado());
        });
    }

    public Comprovante registrarLaudoIrrecuperavel(String idLivro) {
        return transacao(() -> {
            Livro livro = buscarLivro(idLivro);
            mudarEstado(livro, livro::registrarLaudoIrrecuperavel, "livro nao esta REPARANDO");
            acervo.salvar(livro);
            return new Comprovante("INCINERACAO", idLivro, null, relogio.get(), null, livro.getNomeEstado());
        });
    }

    public Optional<Comprovante> consultarReserva(String idLivro, String idEstudante) {
        return movimentacao.buscarReserva(idLivro, idEstudante)
                .map(r -> new Comprovante("RESERVA", idLivro, idEstudante, r.getData(), null,
                        acervo.buscarPorId(idLivro).map(Livro::getNomeEstado).orElse(null)));
    }

    // ---- apoio ----

    // trava na propria unidade: fachada e servico de multa dividem a mesma conexao, entao nao podem se misturar
    private <T> T transacao(Supplier<T> corpo) {
        synchronized (unidade) {
            unidade.iniciar();
            try {
                T resultado = corpo.get();
                unidade.confirmar();
                return resultado;
            } catch (RuntimeException e) {
                unidade.desfazer();
                throw e;
            }
        }
    }

    // tenta o evento no livro; se recusar, lanca; se aceitar, registra como voltar o estado
    private void mudarEstado(Livro livro, Supplier<Boolean> evento, String mensagemDeRecusa) {
        String anterior = livro.getNomeEstado();
        if (!evento.get()) {
            throw new RegraDeNegocioException(mensagemDeRecusa);
        }
        unidade.aoDesfazer(() -> livro.setEstado(EstadoLivro.de(anterior)));
    }

    private Livro buscarLivro(String idLivro) {
        return acervo.buscarPorId(idLivro).orElseThrow(() -> new RegraDeNegocioException("livro nao encontrado: " + idLivro));
    }

    private record Atendimento(String cpf, String email) {
    }

    private Atendimento validarPessoas(String idEstudante, String idAtendente) {
        var estudante = relacionamento.buscarEstudante(idEstudante)
                .orElseThrow(() -> new RegraDeNegocioException("estudante nao cadastrado: " + idEstudante));
        relacionamento.buscarAtendente(idAtendente)
                .orElseThrow(() -> new RegraDeNegocioException("atendente nao cadastrado: " + idAtendente));
        return new Atendimento(estudante.getPessoa().getCpf(), estudante.getPessoa().getEmail());
    }
}
