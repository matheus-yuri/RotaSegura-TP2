package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.format.DateTimeParseException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import servico.Locadora;

// Matheus Yuri Sadamatsu Rodrigues - RA 1301392611037
// Classe base de todas as abas. Juntei aqui o que ia se repetir em
// todas: montar formulário, botões, tabela e principalmente tratar os
// erros mostrando caixinha de mensagem (JOptionPane).
// Cada aba herda daqui e implementa o atualizar().
public abstract class PainelBase extends JPanel {

    // só pra tirar o aviso amarelo do Eclipse (JPanel é Serializable)
    private static final long serialVersionUID = 1L;

    // todas as abas usam a mesma Locadora
    protected Locadora locadora;

    private JPanel formulario = new JPanel(new GridBagLayout());
    private JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT));
    private int linhaFormulario = 0;

    public PainelBase(Locadora locadora) {
        this.locadora = locadora;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    // cada aba diz como recarrega os dados dela
    public abstract void atualizar();

    // a TelaPrincipal chama isso quando troca de aba
    public void recarregar() {
        executar(this::atualizar);
    }

    // ---------- montar a tela ----------

    // adiciona uma linha "Rótulo: [campo]" no formulário
    protected void adicionarCampo(String rotulo, JComponent campo) {

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 6, 4, 6);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy = linhaFormulario++;

        c.gridx = 0;
        c.weightx = 0;
        formulario.add(new JLabel(rotulo), c);

        c.gridx = 1;
        c.weightx = 1;
        formulario.add(campo, c);
    }

    // todo botão roda a ação dentro do executar(), que trata os erros
    protected JButton adicionarBotao(String texto, Runnable acao) {

        JButton botao = new JButton(texto);
        botao.addActionListener(evento -> executar(acao));
        botoes.add(botao);
        return botao;
    }

    // formulário na esquerda, tabela na direita
    protected void montarLayout(JComponent conteudoDireita) {

        JPanel esquerda = new JPanel(new BorderLayout());
        esquerda.setBorder(BorderFactory.createTitledBorder("Dados"));
        esquerda.setPreferredSize(new Dimension(340, 10));

        JPanel topo = new JPanel(new BorderLayout());
        topo.add(formulario, BorderLayout.NORTH);
        topo.add(botoes, BorderLayout.SOUTH);
        esquerda.add(topo, BorderLayout.NORTH);

        add(esquerda, BorderLayout.WEST);
        add(conteudoDireita, BorderLayout.CENTER);
    }

    // tabela que não deixa editar a célula com duplo clique
    protected DefaultTableModel criarModeloTabela(String... colunas) {

        return new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int linha, int coluna) {
                return false;
            }
        };
    }

    protected JTable criarTabela(DefaultTableModel modelo) {

        JTable tabela = new JTable(modelo);
        tabela.setRowHeight(24);
        tabela.setFillsViewportHeight(true);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        return tabela;
    }

    protected JScrollPane comTitulo(JComponent componente, String titulo) {

        JScrollPane rolagem = new JScrollPane(componente);
        rolagem.setBorder(BorderFactory.createTitledBorder(titulo));
        return rolagem;
    }

    // ---------- mensagens e erros ----------

    // Roda a ação e, se der erro, mostra uma mensagem em vez de
    // fechar o programa. Cada tipo de erro tem a sua mensagem.
    protected boolean executar(Runnable acao) {

        try {
            acao.run();
            return true;

        } catch (DateTimeParseException e) {
            aviso("Data inválida. Use o formato AAAA-MM-DD (ex.: 2026-11-20).");

        } catch (NumberFormatException e) {
            aviso("Valor numérico inválido. Use números, por exemplo: 100,00");

        } catch (IllegalArgumentException e) {
            // validações do modelo e regras da Locadora
            aviso(e.getMessage());

        } catch (RuntimeException e) {
            // erros do banco (PersistenciaException) e qualquer outro
            JOptionPane.showMessageDialog(
                this, e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }

        return false;
    }

    protected void aviso(String mensagem) {
        JOptionPane.showMessageDialog(
            this, mensagem, "Atenção", JOptionPane.WARNING_MESSAGE);
    }

    protected void sucesso(String mensagem) {
        JOptionPane.showMessageDialog(
            this, mensagem, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
    }

    protected boolean confirmar(String pergunta) {

        int resposta = JOptionPane.showConfirmDialog(
            this, pergunta, "Confirmar", JOptionPane.YES_NO_OPTION);

        return resposta == JOptionPane.YES_OPTION;
    }
}
