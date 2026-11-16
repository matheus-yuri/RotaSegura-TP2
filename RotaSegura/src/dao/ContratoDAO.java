package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import modelo.Cliente;
import modelo.Contrato;
import modelo.Veiculo;

// Matheus Yuri Sadamatsu Rodrigues - RA 1301392611037
// DAO das locações (tabela contrato).
// Abrir e encerrar locação mexe em duas tabelas (contrato e veiculo),
// por isso usei transação: ou grava as duas coisas, ou não grava nada.
public class ContratoDAO {

    private VeiculoDAO veiculoDAO = new VeiculoDAO();

    // JOIN das três tabelas pra já montar o contrato completo
    private static final String SELECT_COMPLETO =
          "SELECT c.id, c.data_inicio, c.data_fim, c.devolvido, "
        + "       cl.id AS cliente_id, cl.nome, cl.cpf, "
        + "       v.id AS veiculo_id, v.tipo, v.modelo, v.placa, v.valor_diaria, v.status "
        + "FROM contrato c "
        + "JOIN cliente cl ON cl.id = c.cliente_id "
        + "JOIN veiculo v ON v.id = c.veiculo_id ";

    // Abre a locação: grava o contrato e o carro vira LOCADO.
    // Se uma parte der erro, a outra é desfeita e o carro não fica
    // "LOCADO" sem contrato.
    public void abrir(Contrato contrato) {

        String sql = "INSERT INTO contrato "
                   + "(cliente_id, veiculo_id, data_inicio, data_fim, devolvido) "
                   + "VALUES (?, ?, ?, ?, FALSE)";

        try (Connection con = ConexaoBD.getConexao()) {

            con.setAutoCommit(false); // só grava de verdade no commit

            try (PreparedStatement ps = con.prepareStatement(
                    sql, Statement.RETURN_GENERATED_KEYS)) {

                ps.setInt(1, contrato.getCliente().getId());
                ps.setInt(2, contrato.getVeiculo().getId());
                ps.setDate(3, Date.valueOf(contrato.getDataInicio()));
                ps.setDate(4, Date.valueOf(contrato.getDataFim()));
                ps.executeUpdate();

                try (ResultSet chaves = ps.getGeneratedKeys()) {
                    if (chaves.next()) {
                        contrato.setId(chaves.getInt(1));
                    }
                }

                // mesma conexão = mesma transação
                veiculoDAO.atualizarStatus(
                    con, contrato.getVeiculo().getId(), Veiculo.LOCADO);

                con.commit(); // deu tudo certo, grava
                contrato.getVeiculo().setStatus(Veiculo.LOCADO);

            } catch (SQLException e) {
                con.rollback(); // deu erro, desfaz tudo
                throw e;
            }

        } catch (SQLException e) {
            throw new PersistenciaException(
                "Erro ao abrir a locação. Nada foi gravado.\n" + e.getMessage(), e);
        }
    }

    // Encerra a locação: contrato fica devolvido e o carro volta pra DISPONIVEL.
    // Mesmo esquema de transação do abrir.
    public void encerrar(Contrato contrato) {

        String sql = "UPDATE contrato SET devolvido = TRUE WHERE id = ?";

        try (Connection con = ConexaoBD.getConexao()) {

            con.setAutoCommit(false);

            try (PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setInt(1, contrato.getId());
                ps.executeUpdate();

                veiculoDAO.atualizarStatus(
                    con, contrato.getVeiculo().getId(), Veiculo.DISPONIVEL);

                con.commit();
                contrato.getVeiculo().setStatus(Veiculo.DISPONIVEL);

            } catch (SQLException e) {
                con.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw new PersistenciaException(
                "Erro ao encerrar a locação. Nada foi gravado.\n" + e.getMessage(), e);
        }
    }

    public ArrayList<Contrato> listar() {
        return listarComFiltro(SELECT_COMPLETO + "ORDER BY c.id DESC");
    }

    public ArrayList<Contrato> listarAbertos() {
        return listarComFiltro(SELECT_COMPLETO + "WHERE c.devolvido = FALSE ORDER BY c.id");
    }

    // usado antes de remover um cliente
    public int contarPorCliente(int idCliente) {
        return contar("SELECT COUNT(*) FROM contrato WHERE cliente_id = ?", idCliente);
    }

    // usado antes de remover um veículo
    public int contarPorVeiculo(int idVeiculo) {
        return contar("SELECT COUNT(*) FROM contrato WHERE veiculo_id = ?", idVeiculo);
    }

    private int contar(String sql, int id) {

        try (Connection con = ConexaoBD.getConexao();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            throw new PersistenciaException(
                "Erro ao consultar contratos: " + e.getMessage(), e);
        }
    }

    private ArrayList<Contrato> listarComFiltro(String sql) {

        ArrayList<Contrato> contratos = new ArrayList<>();

        try (Connection con = ConexaoBD.getConexao();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                contratos.add(montarContrato(rs));
            }

        } catch (SQLException e) {
            throw new PersistenciaException(
                "Erro ao listar locações: " + e.getMessage(), e);
        }

        return contratos;
    }

    // transforma uma linha do JOIN em Contrato (com cliente e veículo dentro)
    private Contrato montarContrato(ResultSet rs) throws SQLException {

        Cliente cliente = new Cliente();
        cliente.setId(rs.getInt("cliente_id"));
        cliente.setNome(rs.getString("nome"));
        cliente.setCpf(rs.getString("cpf"));

        Veiculo veiculo = Veiculo.criar(rs.getString("tipo"));
        veiculo.setId(rs.getInt("veiculo_id"));
        veiculo.setModelo(rs.getString("modelo"));
        veiculo.setPlaca(rs.getString("placa"));
        veiculo.setValorDiaria(rs.getDouble("valor_diaria"));
        veiculo.setStatus(rs.getString("status"));

        Contrato contrato = new Contrato();
        contrato.setId(rs.getInt("id"));
        contrato.setCliente(cliente);
        contrato.setVeiculo(veiculo);
        // início antes do fim, por causa da validação do setDataFim
        contrato.setDataInicio(rs.getDate("data_inicio").toLocalDate());
        contrato.setDataFim(rs.getDate("data_fim").toLocalDate());
        contrato.setDevolvido(rs.getBoolean("devolvido"));

        return contrato;
    }
}
