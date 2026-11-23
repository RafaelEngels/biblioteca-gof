package br.pucpr.biblioteca.persistencia;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class Esquema {
    private Esquema() {
    }

    public static void aplicar(Connection conexao) {
        try (InputStream in = Esquema.class.getResourceAsStream("/schema.sql")) {
            String script = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            try (Statement st = conexao.createStatement()) {
                for (String comando : script.split(";")) {
                    if (!comando.isBlank()) {
                        st.execute(comando);
                    }
                }
            }
            if (!conexao.getAutoCommit()) {
                conexao.commit();
            }
        } catch (IOException | SQLException e) {
            throw new IllegalStateException("falha ao criar o esquema", e);
        }
    }
}
