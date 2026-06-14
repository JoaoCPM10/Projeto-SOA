-- Tabela de espaços do condomínio (churrasqueiras, salões, etc.)
CREATE TABLE IF NOT EXISTS espaco (
    id         INT PRIMARY KEY AUTO_INCREMENT,
    nome       VARCHAR(100) NOT NULL,
    tipo       VARCHAR(50)  NOT NULL,
    capacidade INT          NOT NULL
);

-- Tabela de reservas feitas pelos moradores
-- As datas são armazenadas em colunas separadas (dia, mes, ano)
-- porque nossa classe DataReserva funciona assim
CREATE TABLE IF NOT EXISTS reserva (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    cpf_morador  VARCHAR(14)  NOT NULL,
    nome_morador VARCHAR(100) NOT NULL,
    espaco_id    INT          NOT NULL,
    inicio_dia   INT          NOT NULL,
    inicio_mes   INT          NOT NULL,
    inicio_ano   INT          NOT NULL,
    fim_dia      INT          NOT NULL,
    fim_mes      INT          NOT NULL,
    fim_ano      INT          NOT NULL,
    status       VARCHAR(20)  NOT NULL,
    FOREIGN KEY (espaco_id) REFERENCES espaco(id)
);
