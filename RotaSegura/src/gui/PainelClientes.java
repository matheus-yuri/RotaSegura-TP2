package gui;

import java.util.ArrayList;

import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import modelo.Cliente;
import servico.Locadora;

// Matheus Yuri Sadamatsu Rodrigues - RA 1301392611037
// Aba de clientes (cadastrar, editar, remover e listar).
// Não tem nenhum SQL aqui: a tela só monta o Cliente e pede pra Locadora salvar.
public class PainelClientes extends PainelBase {

    private static final long serialVersionUID = 1L;

    private JTextField campoNome = new JTextField(15);
    private JTextField campoCpf = new JTextField(15);

    private DefaultTableModel modeloTabela = criarModeloTabela("ID", "Nome", "CPF");
    private JTable tabela = criarTabela(modeloTabela);

    // mesma ordem das linhas da tabela
    private ArrayList<Cliente> clientes = new ArrayList<>();

    public PainelClientes(Locadora locadora) {
        super(locadora);

        adicionarCampo("Nome:", campoNome);
        adicionarCampo("CPF:", campoCpf);

        adicionarBotao("Salvar", this::salvar);
        adicionarBotao("Editar", this::editar);
        adicionarBotao("Remover", this::remover);
        adicionarBotao("Limpar", this::limpar);

        // clicou numa linha, os dados vão pros campos
        tabela.getSelectionModel().addListSelectionListener(evento -> preencherCampos());

        montarLayout(comTitulo(tabela, "Clientes cadastrados"));
    }

    @Override
    public void atualizar() {

        clientes = locadora.listarClientes();
        modeloTabela.setRowCount(0); // limpa a tabela

        for (Cliente c : clientes) {
            modeloTabela.addRow(new Object[] { c.getId(), c.getNome(), c.getCpf() });
        }
    }

    private void salvar() {

        Cliente cliente = new Cliente();
        cliente.setNome(campoNome.getText()); // se estiver vazio, o set já lança o erro
        cliente.setCpf(campoCpf.getText());

        locadora.cadastrarCliente(cliente);

        limpar();
        atualizar();
        sucesso("Cliente cadastrado com sucesso.");
    }

    private void editar() {

        Cliente selecionado = clienteSelecionado();

        Cliente cliente = new Cliente();
        cliente.setId(selecionado.getId());
        cliente.setNome(campoNome.getText());
        cliente.setCpf(campoCpf.getText());

        locadora.atualizarCliente(cliente);

        limpar();
        atualizar();
        sucesso("Cliente atualizado com sucesso.");
    }

    private void remover() {

        Cliente selecionado = clienteSelecionado();

        if (confirmar("Remover o cliente " + selecionado.getNome() + "?")) {
            locadora.removerCliente(selecionado);
            limpar();
            atualizar();
        }
    }

    private void limpar() {
        campoNome.setText("");
        campoCpf.setText("");
        tabela.clearSelection();
    }

    private void preencherCampos() {

        int linha = tabela.getSelectedRow();

        if (linha >= 0 && linha < clientes.size()) {
            Cliente c = clientes.get(linha);
            campoNome.setText(c.getNome());
            campoCpf.setText(c.getCpf());
        }
    }

    private Cliente clienteSelecionado() {

        int linha = tabela.getSelectedRow();

        if (linha < 0) {
            throw new IllegalArgumentException("Selecione um cliente na tabela.");
        }

        return clientes.get(linha);
    }
}
