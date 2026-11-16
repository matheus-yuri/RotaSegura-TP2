package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;

import modelo.Veiculo;

// Matheus Yuri Sadamatsu Rodrigues - RA 1301392611037
// DAO dos veículos.
// Popular, Sedan e SUV ficam na mesma tabela, com uma coluna "tipo".
// Quando leio do banco, o Veiculo.criar() usa essa coluna pra criar
// o objeto certo, e o polimorfismo continua funcionando.
public class VeiculoDAO {

    public void inserir(Veiculo veiculo) {

        String sql = "INSERT INTO veiculo (tipo, modelo, placa, valor_diaria, status) "
                   + "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = ConexaoBD.getConexao();
             PreparedStatement ps = con.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, veiculo.getTipo());
            ps.setString(2, veiculo.getModelo());
            ps.setString(3, veiculo.getPlaca());
            ps.setDouble(4, veiculo.getValorDiaria());
            ps.setString(5, veiculo.getStatus());
            ps.executeUpdate();

            try (ResultSet chaves = ps.getGeneratedKeys()) {
                if (chaves.next()) {
                    veiculo.setId(chaves.getInt(1));
                }
            }

        } catch (SQLIntegrityConstraintViolationException e) {
            throw new PersistenciaException(
                "Já existe um veículo cadastrado com essa placa.", e);

        } catch (SQLException e) {
            throw new PersistenciaException(
                "Erro ao cadastrar veículo: " + e.getMessage(), e);
        }
    }

    // não mexe no status aqui, ele só muda ao abrir/encerrar locação
    public void atualizar(Veiculo veiculo) {

        String sql = "UPDATE veiculo SET tipo = ?, modelo = ?, placa = ?, valor_diaria = ? "
                   + "WHERE id = ?";

        try (Connection con = ConexaoBD.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, veiculo.getTipo());
            ps.setString(2, veiculo.getModelo());
            ps.setString(3, veiculo.getPlaca());
            ps.setDouble(4, veiculo.getValorDiaria());
            ps.setInt(5, veiculo.getId());
            ps.executeUpdate();

        } catch (SQLIntegrityConstraintViolationException e) {
            throw new PersistenciaException(
                "Já existe outro veículo cadastrado com essa placa.", e);

        } catch (SQLException e) {
            throw new PersistenciaException(
                "Erro ao atualizar veículo: " + e.getMessage(), e);
        }
    }

    // recebe a conexão do ContratoDAO pra fazer parte da mesma transação
    void atualizarStatus(Connection con, int idVeiculo, String status) throws SQLException {

        String sql = "UPDATE veiculo SET status = ? WHERE id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, idVeiculo);
            ps.executeUpdate();
        }
    }

    public void remover(int id) {

        String sql = "DELETE FROM veiculo WHERE id = ?";

        try (Connection con = ConexaoBD.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new PersistenciaException(
                "Erro ao remover veículo: " + e.getMessage(), e);
        }
    }

    public Veiculo buscarPorId(int id) {

        String sql = "SELECT * FROM veiculo WHERE id = ?";

        try (Connection con = ConexaoBD.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return montarVeiculo(rs);
                }
            }

        } catch (SQLException e) {
            throw new PersistenciaException(
                "Erro ao buscar veículo: " + e.getMessage(), e);
        }

        return null;
    }

    public ArrayList<Veiculo> listar() {
        return listarComFiltro("SELECT * FROM veiculo ORDER BY id");
    }

    public ArrayList<Veiculo> listarDisponiveis() {
        return listarComFiltro(
            "SELECT * FROM veiculo WHERE status = '" + Veiculo.DISPONIVEL + "' ORDER BY id");
    }

    // pra não repetir o mesmo código nos dois listar
    private ArrayList<Veiculo> listarComFiltro(String sql) {

        ArrayList<Veiculo> veiculos = new ArrayList<>();

        try (Connection con = ConexaoBD.getConexao();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                veiculos.add(montarVeiculo(rs));
            }

        } catch (SQLException e) {
            throw new PersistenciaException(
                "Erro ao listar veículos: " + e.getMessage(), e);
        }

        return veiculos;
    }

    // transforma uma linha do banco em Veiculo
    private Veiculo montarVeiculo(ResultSet rs) throws SQLException {

        // o tipo salvo no banco decide qual classe criar
        Veiculo veiculo = Veiculo.criar(rs.getString("tipo"));

        veiculo.setId(rs.getInt("id"));
        veiculo.setModelo(rs.getString("modelo"));
        veiculo.setPlaca(rs.getString("placa"));
        veiculo.setValorDiaria(rs.getDouble("valor_diaria"));
        veiculo.setStatus(rs.getString("status"));

        return veiculo;
    }
}
