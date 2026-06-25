package com.condominio.repository;

import com.condominio.enums.StatusReserva;
import com.condominio.model.DataReserva;
import com.condominio.model.Espaco;
import com.condominio.model.IntervaloDatas;
import com.condominio.model.Reserva;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ReservaRepositoryImpl implements ReservaRepository {

    private Connection conexao;
    private EspacoRepository espacoRepo;

    public ReservaRepositoryImpl() {
        this.conexao    = ConexaoBanco.getConexao();
        this.espacoRepo = new EspacoRepositoryImpl();
    }

    @Override
    public Reserva save(Reserva reserva) {
        String sql = "INSERT INTO reserva " +
                     "(cpf_morador, nome_morador, espaco_id, inicio_dia, inicio_mes, inicio_ano, " +
                     "fim_dia, fim_mes, fim_ano, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            stmt.setString(1, reserva.getCpfMorador());
            stmt.setString(2, reserva.getNomeMorador());
            stmt.setInt   (3, reserva.getEspaco().getId());
            stmt.setInt   (4, reserva.getPeriodo().getInicio().getDia());
            stmt.setInt   (5, reserva.getPeriodo().getInicio().getMes());
            stmt.setInt   (6, reserva.getPeriodo().getInicio().getAno());
            stmt.setInt   (7, reserva.getPeriodo().getFim().getDia());
            stmt.setInt   (8, reserva.getPeriodo().getFim().getMes());
            stmt.setInt   (9, reserva.getPeriodo().getFim().getAno());
            stmt.setString(10, reserva.getStatus().name());

            stmt.executeUpdate();

            // Pega o id gerado automaticamente pelo banco e atualiza o objeto
            ResultSet chaves = stmt.getGeneratedKeys();
            if (chaves.next()) {
                reserva.setId(chaves.getLong(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar reserva: " + e.getMessage(), e);
        }

        return reserva;
    }

    @Override
    public List<Reserva> findByEspaco(Espaco espaco) {
        List<Reserva> lista = new ArrayList<>();

        // Busca apenas reservas ativas (CONFIRMADA) do espaço
        String sql = "SELECT * FROM reserva WHERE espaco_id = ? AND status = 'CONFIRMADA'";

        try {
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setInt(1, espaco.getId());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(mapearReserva(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar reservas por espaço: " + e.getMessage(), e);
        }

        return lista;
    }

    @Override
    public List<Reserva> findByCpf(String cpf, IntervaloDatas intervalo) {
        List<Reserva> lista = new ArrayList<>();

        // Busca reservas do morador cujo período se sobrepõe com o intervalo pedido
        // A lógica de sobreposição é:
        //   inicio_reserva <= fim_intervalo  E  fim_reserva >= inicio_intervalo
        String sql = "SELECT * FROM reserva WHERE cpf_morador = ? " +
                     "AND (inicio_ano * 10000 + inicio_mes * 100 + inicio_dia) " +
                     "    <= (? * 10000 + ? * 100 + ?) " +
                     "AND (fim_ano * 10000 + fim_mes * 100 + fim_dia) " +
                     "    >= (? * 10000 + ? * 100 + ?)";

        try {
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setString(1, cpf);
            stmt.setInt   (2, intervalo.getFim().getAno());
            stmt.setInt   (3, intervalo.getFim().getMes());
            stmt.setInt   (4, intervalo.getFim().getDia());
            stmt.setInt   (5, intervalo.getInicio().getAno());
            stmt.setInt   (6, intervalo.getInicio().getMes());
            stmt.setInt   (7, intervalo.getInicio().getDia());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(mapearReserva(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar reservas por CPF: " + e.getMessage(), e);
        }

        return lista;
    }

    @Override
    public List<Reserva> findAll(IntervaloDatas intervalo) {
        List<Reserva> lista = new ArrayList<>();

        String sql = "SELECT * FROM reserva " +
                    "WHERE (inicio_ano * 10000 + inicio_mes * 100 + inicio_dia) " +
                    "      >= (? * 10000 + ? * 100 + ?) " +
                    "AND   (inicio_ano * 10000 + inicio_mes * 100 + inicio_dia) " +
                    "      <= (? * 10000 + ? * 100 + ?) " +
                    "ORDER BY inicio_ano, inicio_mes, inicio_dia";

        try {
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setInt(1, intervalo.getInicio().getAno());
            stmt.setInt(2, intervalo.getInicio().getMes());
            stmt.setInt(3, intervalo.getInicio().getDia());
            stmt.setInt(4, intervalo.getFim().getAno());
            stmt.setInt(5, intervalo.getFim().getMes());
            stmt.setInt(6, intervalo.getFim().getDia());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(mapearReserva(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar reservas por período: " + e.getMessage(), e);
        }

        return lista;
    }

    @Override
    public List<Reserva> findTodas() {
        List<Reserva> lista = new ArrayList<>();

        String sql = "SELECT * FROM reserva ORDER BY inicio_ano, inicio_mes, inicio_dia";

        try {
            PreparedStatement stmt = conexao.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(mapearReserva(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar todas as reservas: " + e.getMessage(), e);
        }

        return lista;
    }

    @Override
    public void atualizarStatus(List<Long> ids, StatusReserva novoStatus) {
        if (ids == null || ids.isEmpty()) return;

        // Monta uma query com um ? para cada id da lista
        // Ex: UPDATE reserva SET status = ? WHERE id IN (?, ?, ?)
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < ids.size(); i++) {
            placeholders.append(i == 0 ? "?" : ", ?");
        }

        String sql = "UPDATE reserva SET status = ? WHERE id IN (" + placeholders + ")";

        try {
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setString(1, novoStatus.name());

            for (int i = 0; i < ids.size(); i++) {
                stmt.setLong(i + 2, ids.get(i));
            }

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar status das reservas: " + e.getMessage(), e);
        }
    }

    // Converte uma linha do ResultSet em um objeto Reserva
    private Reserva mapearReserva(ResultSet rs) throws SQLException {
        DataReserva inicio = new DataReserva(
            rs.getInt("inicio_dia"),
            rs.getInt("inicio_mes"),
            rs.getInt("inicio_ano")
        );

        DataReserva fim = new DataReserva(
            rs.getInt("fim_dia"),
            rs.getInt("fim_mes"),
            rs.getInt("fim_ano")
        );

        Espaco espaco = espacoRepo.findById(rs.getInt("espaco_id"));

        return new Reserva(
            rs.getLong("id"),
            rs.getString("cpf_morador"),
            rs.getString("nome_morador"),
            espaco,
            new IntervaloDatas(inicio, fim),
            StatusReserva.valueOf(rs.getString("status"))
        );
    }

    @Override
    public List<Reserva> findConfirmadasComFimAntes(DataReserva data) {
        List<Reserva> lista = new ArrayList<>();

        // Busca reservas confirmadas cujo fim é anterior à data informada
        String sql = "SELECT * FROM reserva WHERE status = 'CONFIRMADA' " +
                    "AND (fim_ano * 10000 + fim_mes * 100 + fim_dia) " +
                    "  < (? * 10000 + ? * 100 + ?)";

        try {
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setInt(1, data.getAno());
            stmt.setInt(2, data.getMes());
            stmt.setInt(3, data.getDia());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                lista.add(mapearReserva(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar reservas para concluir: " + e.getMessage(), e);
        }

        return lista;
    }

    @Override
    public void cancelar(long id) {
        String sql = "UPDATE reserva SET status = 'CANCELADA' WHERE id = ?";
        try {
            PreparedStatement stmt = conexao.prepareStatement(sql);
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cancelar reserva: " + e.getMessage(), e);
        }
    }
}
