package gui;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

import servico.Locadora;

// Matheus Yuri Sadamatsu Rodrigues - RA 1301392611037
// Janela principal: as abas e o rodapé com meu nome e RA.
public class TelaPrincipal extends JFrame {

    private static final long serialVersionUID = 1L;

    private Locadora locadora = new Locadora();
    private JTabbedPane abas = new JTabbedPane();

    public TelaPrincipal() {

        super("Rota Segura - Sistema de Locação");

        // todas as abas recebem a mesma Locadora
        abas.addTab("Clientes", new PainelClientes(locadora));
        abas.addTab("Veículos", new PainelVeiculos(locadora));
        abas.addTab("Locações", new PainelLocacoes(locadora));
        abas.addTab("Devolução", new PainelDevolucao(locadora));

        // trocou de aba, recarrega os dados dela
        // (não importa qual é, todas são PainelBase)
        abas.addChangeListener(evento -> {
            PainelBase painel = (PainelBase) abas.getSelectedComponent();
            painel.recarregar();
        });

        JLabel rodape = new JLabel(
            "Aluno: Matheus Yuri Sadamatsu Rodrigues   |   RA: 1301392611037   |   Rota Segura - TP2"
        );
        rodape.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, Color.GRAY),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        add(abas, BorderLayout.CENTER);
        add(rodape, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 520);
        setLocationRelativeTo(null); // abre no meio da tela
    }

    // o Main chama isso depois que a janela aparece
    public void iniciar() {

        try {
            locadora.testarConexao();
            ((PainelBase) abas.getSelectedComponent()).recarregar();

        } catch (RuntimeException e) {
            // sem banco o programa não fecha, só avisa
            JOptionPane.showMessageDialog(
                this, e.getMessage(), "Banco de dados", JOptionPane.ERROR_MESSAGE);
        }
    }
}
