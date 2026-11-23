package br.pucpr.biblioteca.app;

public record Configuracao(Perfil perfil, String banco, String politica, String jdbcUrl, String usuario, String senha) {

    public static Configuracao teste() {
        return new Configuracao(Perfil.TESTE, "A", "ORIGINAL", null, null, null);
    }

    // le das variaveis de ambiente; sem nada configurado cai no perfil de teste
    public static Configuracao doAmbiente() {
        Perfil perfil = Perfil.valueOf(System.getenv().getOrDefault("BIBLIOTECA_PERFIL", "TESTE"));
        return new Configuracao(perfil,
                System.getenv().getOrDefault("BIBLIOTECA_BANCO", "A"),
                System.getenv().getOrDefault("BIBLIOTECA_POLITICA", "ORIGINAL"),
                System.getenv().getOrDefault("BIBLIOTECA_DB_URL", "jdbc:mysql://localhost:3306/bd_biblioteca"),
                System.getenv().getOrDefault("BIBLIOTECA_DB_USER", "root"),
                System.getenv().getOrDefault("BIBLIOTECA_DB_SENHA", ""));
    }

    public Configuracao comPolitica(String nova) {
        return new Configuracao(perfil, banco, nova, jdbcUrl, usuario, senha);
    }

    public Configuracao comBanco(String novo) {
        return new Configuracao(perfil, novo, politica, jdbcUrl, usuario, senha);
    }
}
