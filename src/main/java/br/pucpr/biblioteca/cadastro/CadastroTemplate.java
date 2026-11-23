package br.pucpr.biblioteca.cadastro;

import java.util.Optional;

// D = dados de entrada, T = entidade criada
public abstract class CadastroTemplate<D, T> {

    // metodo-modelo: final, ninguem muda a ordem dos passos
    public final T incluir(D dados) {
        Optional<T> existente = buscarExistente(dados);
        if (existente.isPresent()) {
            apresentar(existente.get());
            return existente.get();
        }
        validar(dados);
        verificarPreRequisitos(dados);
        T novo = criar(dados);
        armazenar(novo);
        depoisDeArmazenar(novo);
        apresentar(novo);
        return novo;
    }

    protected abstract Optional<T> buscarExistente(D dados);
    protected abstract void validar(D dados);
    protected abstract void verificarPreRequisitos(D dados);
    protected abstract T criar(D dados);
    protected abstract void armazenar(T novo);
    protected abstract void apresentar(T cadastro);

    // gancho: por padrao nao faz nada
    protected void depoisDeArmazenar(T novo) {
    }
}
