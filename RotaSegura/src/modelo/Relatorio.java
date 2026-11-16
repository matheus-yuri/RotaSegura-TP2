package modelo;

// Matheus Yuri Sadamatsu Rodrigues - RA 1301392611037
// Interface simples: quem implementa precisa saber gerar um relatório em texto.
// No projeto quem usa é o Contrato.
public interface Relatorio {

    String gerarRelatorio();
}