package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;

import modelo.Cliente;

// Matheus Yuri Sadamatsu Rodrigues - RA 1301392611037
// DAO do cliente: todo SQL da tabela cliente fica aqui.
// Usei PreparedStatement com "?" no lugar dos valores, que é o jeito
// seguro de montar o SQL (evita SQL Injection).
public class ClienteDAO {

    public void inserir(Cliente cliente) {

        String sql = "INSERT INTO cliente (nome, cpf) VALUES (?, ?)";

        // try-with-resources: fecha a conexão sozinho no final
        try (Connection con = ConexaoBD.getConexao();
             PreparedStatement ps = con.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, cliente.getNome());
            ps.setString(2, cliente.getCpf());
            ps.executeUpdate();

            // pega o id que o banco gerou e coloca no objeto
            try (ResultSet chaves = ps.getGeneratedKeys()) {
                if (chaves.next()) {
                    cliente.setId(chaves.getInt(1));
                }
            }

        } catch (SQLIntegrityConstraintViolationException e) {
            throw new PersistenciaException(
                "Já existe um cliente cadastrado com esse CPF.", e);

        } catch (SQLException e) {
            throw new PersistenciaException(
                "Erro ao cadastrar cliente: " + e.getMessage(), e);
        }
    }

    public void atualizar(Cliente cliente) {

        String sql = "UPDATE cliente SET nome = ?, cpf = ? WHERE id = ?";

        try (Connection con = ConexaoBD.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, cliente.getNome());
            ps.setString(2, cliente.getCpf());
            ps.setInt(3, cliente.getId());
            ps.executeUpdate();

        } catch (SQLIntegrityConstraintViolationException e) {
            throw new PersistenciaException(
                "Já existe outro cliente cadastrado com esse CPF.", e);

        } catch (SQLException e) {
            throw new PersistenciaException(
                "Erro ao atualizar cliente: " + e.getMessage(), e);
        }
    }

    public void remover(int id) {

        String sql = "DELETE FROM cliente WHERE id = ?";

        try (Connection con = ConexaoBD.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new PersistenciaException(
                "Erro ao remover cliente: " + e.getMessage(), e);
        }
    }

    public ArrayList<Cliente> listar() {

        String sql = "SELECT id, nome, cpf FROM cliente ORDER BY nome";

        ArrayList<Cliente> clientes = new ArrayList<>();

        try (Connection con = ConexaoBD.getConexao();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            // cada linha do resultado vira um Cliente
            while (rs.next()) {
                Cliente cliente = new Cliente();
                cliente.setId(rs.getInt("id"));
                cliente.setNome(rs.getString("nome"));
                cliente.setCpf(rs.getString("cpf"));
                clientes.add(cliente);
            }

        } catch (SQLException e) {
            throw new PersistenciaException(
                "Erro ao listar clientes: " + e.getMessage(), e);
        }

        return clientes;
    }
}
