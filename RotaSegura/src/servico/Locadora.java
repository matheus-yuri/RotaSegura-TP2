package servico;

import java.util.ArrayList;

import dao.ClienteDAO;
import dao.ConexaoBD;
import dao.ContratoDAO;
import dao.VeiculoDAO;
import modelo.Cliente;
import modelo.Contrato;
import modelo.Veiculo;

// Matheus Yuri Sadamatsu Rodrigues - RA 1301392611037
// Regras de negócio da locadora.
// A tela nunca fala direto com o DAO: ela chama a Locadora, que confere
// as regras e só depois chama o DAO.
//   Tela -> Locadora -> DAO -> MySQL
// Na Parte 1 essa classe guardava tudo em ArrayList; agora vai pro banco.
public class Locadora {

    private ClienteDAO clienteDAO = new ClienteDAO();
    private VeiculoDAO veiculoDAO = new VeiculoDAO();
    private ContratoDAO contratoDAO = new ContratoDAO();

    public void testarConexao() {
        ConexaoBD.testarConexao();
    }

    // ---------- clientes ----------

    public void cadastrarCliente(Cliente cliente) {
        clienteDAO.inserir(cliente);
    }

    public void atualizarCliente(Cliente cliente) {
        clienteDAO.atualizar(cliente);
    }

    public void removerCliente(Cliente cliente) {

        // não deixo apagar cliente com locação, senão perde o histórico
        if (contratoDAO.contarPorCliente(cliente.getId()) > 0) {
            throw new IllegalArgumentException(
                "Este cliente possui locações registradas e não pode ser removido."
            );
        }

        clienteDAO.remover(cliente.getId());
    }

    public ArrayList<Cliente> listarClientes() {
        return clienteDAO.listar();
    }

    // ---------- veículos ----------

    public void cadastrarVeiculo(Veiculo veiculo) {
        veiculoDAO.inserir(veiculo);
    }

    public void atualizarVeiculo(Veiculo veiculo) {

        // carro locado não pode ser editado, senão muda o valor no meio do contrato
        Veiculo atual = veiculoDAO.buscarPorId(veiculo.getId());

        if (atual != null && !atual.isDisponivel()) {
            throw new IllegalArgumentException(
                "Este veículo está locado e não pode ser editado agora."
            );
        }

        veiculoDAO.atualizar(veiculo);
    }

    public void removerVeiculo(Veiculo veiculo) {

        if (!veiculo.isDisponivel()) {
            throw new IllegalArgumentException(
                "Este veículo está locado e não pode ser removido."
            );
        }

        if (contratoDAO.contarPorVeiculo(veiculo.getId()) > 0) {
            throw new IllegalArgumentException(
                "Este veículo possui locações no histórico e não pode ser removido."
            );
        }

        veiculoDAO.remover(veiculo.getId());
    }

    public ArrayList<Veiculo> listarVeiculos() {
        return veiculoDAO.listar();
    }

    public ArrayList<Veiculo> listarVeiculosDisponiveis() {
        return veiculoDAO.listarDisponiveis();
    }

    // ---------- locações ----------

    public void abrirLocacao(Contrato contrato) {

        // confiro no banco de novo, porque a lista da tela pode estar desatualizada
        Veiculo atual = veiculoDAO.buscarPorId(contrato.getVeiculo().getId());

        if (atual == null || !atual.isDisponivel()) {
            throw new IllegalArgumentException(
                "O veículo escolhido não está disponível."
            );
        }

        contratoDAO.abrir(contrato);
    }

    public void encerrarLocacao(Contrato contrato) {

        // o próprio Contrato não deixa devolver duas vezes
        contrato.devolver();

        try {
            contratoDAO.encerrar(contrato);
        } catch (RuntimeException e) {
            // se o banco falhar, volta o contrato pra "em aberto"
            contrato.setDevolvido(false);
            throw e;
        }
    }

    public ArrayList<Contrato> listarContratos() {
        return contratoDAO.listar();
    }

    public ArrayList<Contrato> listarContratosAbertos() {
        return contratoDAO.listarAbertos();
    }
}
