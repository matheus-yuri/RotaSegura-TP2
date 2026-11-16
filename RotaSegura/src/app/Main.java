package app;

import java.util.Locale;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import gui.TelaPrincipal;

// Matheus Yuri Sadamatsu Rodrigues - RA 1301392611037
// Classe que inicia o sistema.
// Na Parte 1 rodava no console (Scanner), agora abre a janela com Swing.
public class Main {

    public static void main(String[] args) {

        // português do Brasil: botões "Sim/Não" e valores com vírgula
        Locale.setDefault(Locale.of("pt", "BR"));

        // tema Nimbus (já vem no Java); se não tiver, usa o padrão
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            System.out.println("Tema Nimbus indisponível. Usando o padrão.");
        }

        // no Swing a tela tem que ser criada pelo invokeLater (thread da interface)
        SwingUtilities.invokeLater(() -> {
            TelaPrincipal tela = new TelaPrincipal();
            tela.setVisible(true);
            tela.iniciar();
        });
    }
}
