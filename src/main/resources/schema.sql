CREATE TABLE IF NOT EXISTS livro (
    id VARCHAR(40) PRIMARY KEY,
    titulo VARCHAR(200) NOT NULL,
    estado VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS pessoa (
    cpf VARCHAR(14) PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    sobrenome VARCHAR(100),
    email VARCHAR(150)
);

CREATE TABLE IF NOT EXISTS estudante (
    matricula VARCHAR(30) PRIMARY KEY,
    cpf VARCHAR(14) NOT NULL
);

CREATE TABLE IF NOT EXISTS atendente (
    matricula_funcional VARCHAR(30) PRIMARY KEY,
    cpf VARCHAR(14) NOT NULL
);

CREATE TABLE IF NOT EXISTS reserva (
    id_livro VARCHAR(40) NOT NULL,
    id_estudante VARCHAR(30) NOT NULL,
    id_atendente VARCHAR(30) NOT NULL,
    data DATE NOT NULL,
    PRIMARY KEY (id_livro, id_estudante)
);

CREATE TABLE IF NOT EXISTS emprestimo (
    id_livro VARCHAR(40) NOT NULL,
    id_estudante VARCHAR(30) NOT NULL,
    id_atendente VARCHAR(30) NOT NULL,
    data_emprestimo DATE NOT NULL,
    data_vencimento DATE NOT NULL,
    num_renovacoes INT NOT NULL,
    PRIMARY KEY (id_livro, id_estudante)
);

CREATE TABLE IF NOT EXISTS devolucao (
    id_livro VARCHAR(40) NOT NULL,
    id_estudante VARCHAR(30) NOT NULL,
    id_atendente VARCHAR(30) NOT NULL,
    data_vencimento DATE NOT NULL,
    data_devolucao DATE NOT NULL,
    PRIMARY KEY (id_livro, id_estudante)
);

CREATE TABLE IF NOT EXISTS multa (
    id VARCHAR(40) PRIMARY KEY,
    id_livro VARCHAR(40) NOT NULL,
    id_estudante VARCHAR(30) NOT NULL,
    dias_atraso BIGINT NOT NULL,
    valor DECIMAL(10,2) NOT NULL,
    politica VARCHAR(30) NOT NULL,
    vencimento_boleto DATE NOT NULL,
    id_externo VARCHAR(40),
    situacao VARCHAR(10) NOT NULL
);
