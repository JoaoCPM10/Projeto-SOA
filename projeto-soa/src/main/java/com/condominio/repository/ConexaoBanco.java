package com.condominio.repository;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

public class ConexaoBanco {

    // Arquivo de banco criado na pasta do projeto ao rodar
    private static final String URL    = "jdbc:h2:./condominio_db;DB_CLOSE_DELAY=-1";
    private static final String USUARIO = "sa";
    private static final String SENHA   = "";

    // Conexão única compartilhada pelo sistema inteiro (padrão Singleton)
    private static Connection conexao;

    // Retorna a conexão, criando uma nova se ainda não existir
    public static Connection getConexao() {
        try {
            if (conexao == null || conexao.isClosed()) {
                conexao = DriverManager.getConnection(URL, USUARIO, SENHA);
                inicializarBanco();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao conectar com o banco de dados: " + e.getMessage(), e);
        }
        return conexao;
    }

    // Lê e executa o schema.sql e o data.sql na primeira vez
    private static void inicializarBanco() {
        executarSql("schema.sql");

        // Só insere os dados iniciais se a tabela de espaços estiver vazia
        try {
            var stmt = conexao.createStatement();
            var rs   = stmt.executeQuery("SELECT COUNT(*) FROM espaco");
            rs.next();
            if (rs.getInt(1) == 0) {
                executarSql("data.sql");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao verificar dados iniciais: " + e.getMessage(), e);
        }
    }

    // Lê um arquivo .sql dos recursos do projeto e executa cada instrução
    private static void executarSql(String nomeArquivo) {
        try {
            InputStream is = ConexaoBanco.class
                    .getClassLoader()
                    .getResourceAsStream(nomeArquivo);

            if (is == null) {
                throw new RuntimeException("Arquivo não encontrado: " + nomeArquivo);
            }

            String sql = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));

            Statement stmt = conexao.createStatement();

            // Executa cada instrução separada por ponto e vírgula
            for (String instrucao : sql.split(";")) {
                String limpa = instrucao.trim();
                if (!limpa.isEmpty()) {
                    stmt.execute(limpa);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao executar " + nomeArquivo + ": " + e.getMessage(), e);
        }
    }

    // Fecha a conexão ao encerrar o sistema
    public static void fechar() {
        try {
            if (conexao != null && !conexao.isClosed()) {
                conexao.close();
            }
        } catch (SQLException e) {
            System.err.println("Erro ao fechar conexão: " + e.getMessage());
        }
    }
}
