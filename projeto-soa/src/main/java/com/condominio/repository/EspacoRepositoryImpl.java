package com.condominio.repository;

import com.condominio.enums.TipoEspaco;
import com.condominio.model.Espaco;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EspacoRepositoryImpl implements EspacoRepository {

    private Connection conexao;

    public EspacoRepositoryImpl() {
        this.conexao = ConexaoBanco.getConexao();
    }

    @Override
    public List<Espaco> findAll() {
        List<Espaco> lista = new ArrayList<>();
        String sql = "SELECT id, nome, tipo, capacidade FROM espaco";

        try {
            PreparedStatement stmt = conexao.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(mapearEspaco(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar espaços: " + e.getMessage(), e);
        }

        return lista;
    }

    @Override
    public Espaco findById(int id) {
        String sql = "SELECT id, nome, tipo, capacidade FROM espaco WHERE id = ?";

        try {
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapearEspaco(rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar espaço por id: " + e.getMessage(), e);
        }

        return null;
    }

    @Override
    public List<Espaco> findByTipo(TipoEspaco tipo) {
        List<Espaco> lista = new ArrayList<>();
        String sql = "SELECT id, nome, tipo, capacidade FROM espaco WHERE tipo = ?";

        try {
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setString(1, tipo.name());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(mapearEspaco(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar espaços por tipo: " + e.getMessage(), e);
        }

        return lista;
    }

    // Converte uma linha do ResultSet em um objeto Espaco
    private Espaco mapearEspaco(ResultSet rs) throws SQLException {
        return new Espaco(
            rs.getInt("id"),
            rs.getString("nome"),
            TipoEspaco.valueOf(rs.getString("tipo")),
            rs.getInt("capacidade")
        );
    }
}
