package gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import modelo.Contrato;
import servico.Locadora;

// Matheus Yuri Sadamatsu Rodrigues - RA 1301392611037
// Aba de devolução (fechamento do contrato).
// Essa tela não faz conta nenhuma: só chama o gerarRelatorio() e cada tipo
// de carro calcula a diária, o seguro e a manutenção do seu jeito.
public class PainelDevolucao extends PainelBase {

    private static final long serialVersionUID = 1L;

    private JComboBox<Contrato> comboContratos = new JComboBox<>();
    private JTextArea areaResumo = new JTextArea();

    // pra não mostrar o resumo enquanto a lista está recarregando
    private boolean carregando = false;

    public PainelDevolucao(Locadora locadora) {
        super(locadora);

        JButton botaoResumo = new JButton("Ver resumo");
        botaoResumo.addActionListener(evento -> executar(this::mostrarResumo));

        JButton botaoEncerrar = new JButton("Encerrar contrato");
        botaoEncerrar.addActionListener(evento -> executar(this::encerrar));

        // trocou o contrato, já mostra o resumo
        comboContratos.addActionListener(evento -> {
            if (!carregando) {
                executar(this::mostrarResumo);
            }
        });

        JPanel topo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topo.add(new JLabel("Contrato em aberto:"));
        topo.add(comboContratos);
        topo.add(botaoResumo);
        topo.add(botaoEncerrar);

        areaResumo.setEditable(false);
        areaResumo.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));

        add(topo, BorderLayout.NORTH);
        add(comTitulo(areaResumo, "Resumo do contrato"), BorderLayout.CENTER);
    }

    @Override
    public void atualizar() {

        carregando = true;

        comboContratos.removeAllItems();
        for (Contrato c : locadora.listarContratosAbertos()) {
            comboContratos.addItem(c);
        }

        carregando = false;

        if (comboContratos.getItemCount() == 0) {
            areaResumo.setText("Nenhum contrato em aberto.");
        } else {
            mostrarResumo();
        }
    }

    private void mostrarResumo() {
        areaResumo.setText(contratoSelecionado().gerarRelatorio());
        areaResumo.setCaretPosition(0);
    }

    private void encerrar() {

        Contrato contrato = contratoSelecionado();

        if (!confirmar("Encerrar o contrato nº " + contrato.getId() + "?")) {
            return;
        }

        locadora.encerrarLocacao(contrato);

        // guardo o relatório antes, porque depois de atualizar o contrato
        // some da lista de abertos
        String relatorioFinal = contrato.gerarRelatorio();

        atualizar();
        areaResumo.setText(relatorioFinal);

        sucesso(
            "Contrato encerrado.\n"
            + "O veículo " + contrato.getVeiculo().getModelo()
            + " voltou para DISPONIVEL.\n"
            + String.format("Valor total: R$ %.2f", contrato.calcularValor())
        );
    }

    private Contrato contratoSelecionado() {

        Contrato contrato = (Contrato) comboContratos.getSelectedItem();

        if (contrato == null) {
            throw new IllegalArgumentException("Não há contrato em aberto selecionado.");
        }

        return contrato;
    }
}
