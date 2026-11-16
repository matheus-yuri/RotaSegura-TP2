-- Rota Segura - banco de dados (TP2)
-- Matheus Yuri Sadamatsu Rodrigues - RA 1301392611037
--
-- Pra usar: liga o MySQL no XAMPP, abre o phpMyAdmin (botão Admin),
-- vai em Importar, escolhe esse arquivo e clica em Importar.
-- Obs: ele apaga o banco rotasegura e cria de novo do zero.

DROP DATABASE IF EXISTS rotasegura;
CREATE DATABASE rotasegura CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE rotasegura;

-- clientes (CPF é UNIQUE, não deixa repetir)
CREATE TABLE cliente (
    id    INT AUTO_INCREMENT PRIMARY KEY,
    nome  VARCHAR(100) NOT NULL,
    cpf   VARCHAR(14)  NOT NULL UNIQUE
) ENGINE = InnoDB;

-- veículos: Popular, Sedan e SUV ficam na mesma tabela, a coluna "tipo"
-- diz qual classe o Java cria. Os percentuais ficam nas classes, não aqui.
CREATE TABLE veiculo (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    tipo          VARCHAR(10)   NOT NULL,
    modelo        VARCHAR(60)   NOT NULL,
    placa         VARCHAR(10)   NOT NULL UNIQUE,
    valor_diaria  DECIMAL(10,2) NOT NULL,
    status        VARCHAR(10)   NOT NULL DEFAULT 'DISPONIVEL',
    CHECK (tipo IN ('Popular', 'Sedan', 'SUV')),
    CHECK (status IN ('DISPONIVEL', 'LOCADO')),
    CHECK (valor_diaria > 0)
) ENGINE = InnoDB;

-- contratos (histórico das locações)
-- as FOREIGN KEY não deixam apagar cliente ou veículo que tenha contrato
CREATE TABLE contrato (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    cliente_id   INT     NOT NULL,
    veiculo_id   INT     NOT NULL,
    data_inicio  DATE    NOT NULL,
    data_fim     DATE    NOT NULL,
    devolvido    BOOLEAN NOT NULL DEFAULT FALSE,
    CHECK (data_fim > data_inicio),
    FOREIGN KEY (cliente_id) REFERENCES cliente (id) ON DELETE RESTRICT,
    FOREIGN KEY (veiculo_id) REFERENCES veiculo (id) ON DELETE RESTRICT
) ENGINE = InnoDB;

-- dados de exemplo pra testar
INSERT INTO cliente (nome, cpf) VALUES
    ('Ana Lima',     '111.222.333-44'),
    ('Bruno Costa',  '555.666.777-88'),
    ('Carla Mendes', '999.888.777-66');

INSERT INTO veiculo (tipo, modelo, placa, valor_diaria, status) VALUES
    ('Popular', 'Fiat Mobi',      'ABC1A23', 100.00, 'DISPONIVEL'),
    ('Popular', 'Renault Kwid',   'JKL2D34', 95.00,  'DISPONIVEL'),
    ('Sedan',   'Toyota Corolla', 'DEF4B56', 100.00, 'LOCADO'),
    ('Sedan',   'Honda City',     'MNO5E67', 140.00, 'DISPONIVEL'),
    ('SUV',     'Jeep Compass',   'GHI7C89', 100.00, 'DISPONIVEL'),
    ('SUV',     'Hyundai Creta',  'PQR8F90', 180.00, 'DISPONIVEL');

-- uma locação em aberto (Bruno com o Corolla, por isso ele está LOCADO)
-- e uma já encerrada (Ana com o Mobi) pro histórico não começar vazio
INSERT INTO contrato (cliente_id, veiculo_id, data_inicio, data_fim, devolvido) VALUES
    (2, 3, '2026-11-10', '2026-11-15', FALSE),
    (1, 1, '2026-11-01', '2026-11-04', TRUE);
