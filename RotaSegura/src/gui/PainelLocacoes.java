package gui;

import java.time.LocalDate;

import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import modelo.Cliente;
import modelo.Contrato;
import modelo.Veiculo;
import servico.Locadora;

// Matheus Yuri Sadamatsu Rodrigues - RA 1301392611037
// Aba de locações: abrir locação nova e ver o histórico.
// Só aparecem os carros DISPONIVEIS. Quando abre a locação, o carro
// vira LOCADO no banco (ContratoDAO.abrir).
public class PainelLocacoes extends PainelBase {

    private static final long serialVersionUID = 1L;

    private JComboBox<Cliente> comboCliente = new JComboBox<>();
    private JComboBox<Veiculo> comboVeiculo = new JComboBox<>();
    private JTextField campoInicio = new JTextField(15);
    private JTextField campoFim = new JTextField(15);

    private DefaultTableModel modeloTabela = criarModeloTabela(
        "Nº", "Cliente", "Veículo", "Início", "Fim", "Valor", "Status");
    private JTable tabela = criarTabela(modeloTabela);

    public PainelLocacoes(Locadora locadora) {
        super(locadora);

        adicionarCampo("Cliente:", comboCliente);
        adicionarCampo("Veículo:", comboVeiculo);
        adicionarCampo("Data início:", campoInicio);
        adicionarCampo("Data fim:", campoFim);

        // dica quando passa o mouse no campo
        campoInicio.setToolTipText("Formato AAAA-MM-DD, ex.: 2026-11-20");
        campoFim.setToolTipText("Formato AAAA-MM-DD, ex.: 2026-11-23");

        adicionarBotao("Abrir locação", this::abrirLocacao);
        adicionarBotao("Limpar", this::limpar);

        montarLayout(comTitulo(tabela, "Histórico de locações"));
        limpar();
    }

    @Override
    public void atualizar() {

        // recarrega os combos
        comboCliente.removeAllItems();
        for (Cliente c : locadora.listarClientes()) {
            comboCliente.addItem(c);
        }

        comboVeiculo.removeAllItems();
        for (Veiculo v : locadora.listarVeiculosDisponiveis()) {
            comboVeiculo.addItem(v);
        }

        // e a tabela com todas as locações
        modeloTabela.setRowCount(0);

        for (Contrato c : locadora.listarContratos()) {
            modeloTabela.addRow(new Object[] {
                c.getId(),
                c.getCliente().getNome(),
                c.getVeiculo().getTipo() + " - " + c.getVeiculo().getModelo(),
                c.getDataInicio(),
                c.getDataFim(),
                String.format("R$ %.2f", c.calcularValor()),
                c.isDevolvido() ? "DEVOLVIDO" : "EM ABERTO"
            });
        }
    }

    private void abrirLocacao() {

        Contrato contrato = new Contrato();

        // os sets do Contrato já validam tudo
        contrato.setCliente((Cliente) comboCliente.getSelectedItem());
        contrato.setVeiculo((Veiculo) comboVeiculo.getSelectedItem());

        // data em formato errado cai no aviso do PainelBase
        contrato.setDataInicio(LocalDate.parse(campoInicio.getText().trim()));
        contrato.setDataFim(LocalDate.parse(campoFim.getText().trim()));

        locadora.abrirLocacao(contrato);

        atualizar();
        limpar();
        sucesso(
            "Locação nº " + contrato.getId() + " aberta.\n"
            + "Veículo: " + contrato.getVeiculo() + "\n"
            + String.format("Valor previsto: R$ %.2f", contrato.calcularValor())
        );
    }

    // já deixa uma sugestão de data (hoje até daqui 3 dias)
    private void limpar() {
        campoInicio.setText(LocalDate.now().toString());
        campoFim.setText(LocalDate.now().plusDays(3).toString());
    }
}
