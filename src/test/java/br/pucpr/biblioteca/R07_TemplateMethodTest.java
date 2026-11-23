package br.pucpr.biblioteca;

import static org.junit.jupiter.api.Assertions.*;

import br.pucpr.biblioteca.cadastro.CadastroTemplate;
import br.pucpr.biblioteca.dominio.RegraDeNegocioException;
import br.pucpr.biblioteca.dominio.iEstudante;
import java.lang.reflect.Modifier;
import org.junit.jupiter.api.Test;

class R07_TemplateMethodTest {

    @Test
    void R07_incluirEstudanteDuplicadoRetornaOCadastroExistente() {
        Cenario c = new Cenario();
        iEstudante primeiro = c.app.tratadorDeEstudante.inserirEstudante("11144477735", "E777");
        iEstudante segundo = c.app.tratadorDeEstudante.inserirEstudante("11144477735", "E777");
        assertSame(primeiro, segundo);
    }

    @Test
    void R07_estudanteComCpfNaoCadastradoFalhaAntesDeQualquerGravacao() {
        Cenario c = new Cenario();
        assertThrows(RegraDeNegocioException.class, () -> c.app.tratadorDeEstudante.inserirEstudante("39053344705", "E888"));
        assertTrue(c.app.fabrica.relacionamento().buscarEstudante("E888").isEmpty());
        assertTrue(c.app.fabrica.relacionamento().buscarPessoa("39053344705").isEmpty());
    }

    @Test
    void R07_atendenteComCpfNaoCadastradoTambemFalhaSemGravar() {
        Cenario c = new Cenario();
        assertThrows(RegraDeNegocioException.class, () -> c.app.tratadorDeAtendente.inserirAtendente("39053344705", "A888"));
        assertTrue(c.app.fabrica.relacionamento().buscarAtendente("A888").isEmpty());
    }

    @Test
    void R07_metodoModeloNaoPodeSerSobrescrito() throws Exception {
        var metodo = CadastroTemplate.class.getMethod("incluir", Object.class);
        assertTrue(Modifier.isFinal(metodo.getModifiers()));
    }

    @Test
    void R07_livroInvalidoERecusadoNaValidacao() {
        Cenario c = new Cenario();
        assertThrows(RegraDeNegocioException.class, () -> c.app.tratadorDeLivro.inserirLivro("L1", " "));
        assertTrue(c.app.fabrica.livros().buscarPorId("L1").isEmpty());
    }
}
