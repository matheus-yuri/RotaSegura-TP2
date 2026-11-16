package gui;

import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import modelo.Veiculo;
import servico.Locadora;

// Matheus Yuri Sadamatsu Rodrigues - RA 1301392611037
// Aba de veículos (a frota).
// O tipo escolhido no combo decide qual classe vai ser criada (Veiculo.criar).
public class PainelVeiculos extends PainelBase {

    private static final long serialVersionUID = 1L;

    private JComboBox<String> comboTipo =
        new JComboBox<>(new String[] { "Popular", "Sedan", "SUV" });
    private JTextField campoModelo = new JTextField(15);
    private JTextField campoPlaca = new JTextField(15);
    private JTextField campoDiaria = new JTextField(15);

    private DefaultTableModel modeloTabela =
        criarModeloTabela("ID", "Tipo", "Modelo", "Placa", "Diária base", "Status");
    private JTable tabela = criarTabela(modeloTabela);

    private ArrayList<Veiculo> veiculos = new ArrayList<>();

    public PainelVeiculos(Locadora locadora) {
        super(locadora);

        adicionarCampo("Tipo:", comboTipo);
        adicionarCampo("Modelo:", campoModelo);
        adicionarCampo("Placa:", campoPlaca);
        adicionarCampo("Diária base (R$):", campoDiaria);

        adicionarBotao("Salvar", this::salvar);
        adicionarBotao("Editar", this::editar);
        adicionarBotao("Remover", this::remover);
        adicionarBotao("Limpar", this::limpar);

        tabela.getSelectionModel().addListSelectionListener(evento -> preencherCampos());

        montarLayout(comTitulo(tabela, "Frota cadastrada"));
    }

    @Override
    public void atualizar() {

        veiculos = locadora.listarVeiculos();
        modeloTabela.setRowCount(0);

        for (Veiculo v : veiculos) {
            modeloTabela.addRow(new Object[] {
                v.getId(),
                v.getTipo(),
                v.getModelo(),
                v.getPlaca(),
                String.format("R$ %.2f", v.getValorDiaria()),
                v.getStatus()
            });
        }
    }

    // monta o veículo com o que foi digitado
    private Veiculo lerCampos() {

        String tipo = (String) comboTipo.getSelectedItem();

        Veiculo veiculo = Veiculo.criar(tipo); // Popular, Sedan ou SUV
        veiculo.setModelo(campoModelo.getText());
        veiculo.setPlaca(campoPlaca.getText());

        // aceita vírgula ou ponto; se não for número o PainelBase mostra o aviso
        String textoDiaria = campoDiaria.getText().trim().replace(",", ".");
        veiculo.setValorDiaria(Double.parseDouble(textoDiaria));

        return veiculo;
    }

    private void salvar() {

        Veiculo veiculo = lerCampos();
        locadora.cadastrarVeiculo(veiculo);

        limpar();
        atualizar();
        sucesso("Veículo cadastrado com sucesso.");
    }

    private void editar() {

        Veiculo selecionado = veiculoSelecionado();

        Veiculo veiculo = lerCampos();
        veiculo.setId(selecionado.getId());
        veiculo.setStatus(selecionado.getStatus());

        locadora.atualizarVeiculo(veiculo);

        limpar();
        atualizar();
        sucesso("Veículo atualizado com sucesso.");
    }

    private void remover() {

        Veiculo selecionado = veiculoSelecionado();

        if (confirmar("Remover o veículo " + selecionado + "?")) {
            locadora.removerVeiculo(selecionado);
            limpar();
            atualizar();
        }
    }

    private void limpar() {
        comboTipo.setSelectedIndex(0);
        campoModelo.setText("");
        campoPlaca.setText("");
        campoDiaria.setText("");
        tabela.clearSelection();
    }

    private void preencherCampos() {

        int linha = tabela.getSelectedRow();

        if (linha >= 0 && linha < veiculos.size()) {
            Veiculo v = veiculos.get(linha);
            comboTipo.setSelectedItem(v.getTipo());
            campoModelo.setText(v.getModelo());
            campoPlaca.setText(v.getPlaca());
            campoDiaria.setText(String.format("%.2f", v.getValorDiaria()));
        }
    }

    private Veiculo veiculoSelecionado() {

        int linha = tabela.getSelectedRow();

        if (linha < 0) {
            throw new IllegalArgumentException("Selecione um veículo na tabela.");
        }

        return veiculos.get(linha);
    }
}
