package modelo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

// Matheus Yuri Sadamatsu Rodrigues - RA 1301392611037
// Contrato = uma locação (cliente + veículo + período).
// Implementa a interface Relatorio pra gerar o resumo que aparece na devolução.
public class Contrato implements Relatorio {

    private int id;
    private Cliente cliente;
    private Veiculo veiculo;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private boolean devolvido;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {

        if (cliente == null) {
            throw new IllegalArgumentException("Selecione um cliente.");
        }

        this.cliente = cliente;
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(Veiculo veiculo) {

        if (veiculo == null) {
            throw new IllegalArgumentException("Selecione um veículo.");
        }

        this.veiculo = veiculo;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {

        if (dataInicio == null) {
            throw new IllegalArgumentException("Informe a data de início.");
        }

        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    // precisa chamar o setDataInicio antes, senão não tem como comparar
    public void setDataFim(LocalDate dataFim) {

        if (dataFim == null) {
            throw new IllegalArgumentException("Informe a data de fim.");
        }

        if (dataInicio != null && !dataFim.isAfter(dataInicio)) {
            throw new IllegalArgumentException(
                "A data de fim deve ser posterior à data de início."
            );
        }

        this.dataFim = dataFim;
    }

    public boolean isDevolvido() {
        return devolvido;
    }

    // usado pelo DAO quando carrega um contrato que já está no banco
    public void setDevolvido(boolean devolvido) {
        this.devolvido = devolvido;
    }

    public void devolver() {

        if (devolvido) {
            throw new IllegalArgumentException(
                "O veículo já foi devolvido."
            );
        }

        devolvido = true;
    }

    public long calcularDias() {
        return ChronoUnit.DAYS.between(dataInicio, dataFim);
    }

    // Aqui está o polimorfismo: não preciso saber se o carro é Popular,
    // Sedan ou SUV. Cada um calcula o valor por dia do seu jeito.
    public double calcularValor() {
        return calcularDias() * veiculo.calcularValorPorDia();
    }

    @Override
    public String gerarRelatorio() {

        String status = devolvido ? "DEVOLVIDO" : "EM ABERTO";

        return "===== CONTRATO DE LOCAÇÃO Nº " + id + " =====\n"
                + "Cliente: " + cliente.getNome() + "\n"
                + "CPF: " + cliente.getCpf() + "\n"
                + "Veículo: " + veiculo + "\n"
                + "Data de início: " + dataInicio + "\n"
                + "Data de fim: " + dataFim + "\n"
                + "Dias: " + calcularDias() + "\n"
                + "\n"
                + String.format("Diária:          R$ %8.2f%n", veiculo.calcularDiaria())
                + String.format("Seguro:          R$ %8.2f%n", veiculo.calcularSeguro())
                + String.format("Manutenção:      R$ %8.2f%n", veiculo.calcularManutencao())
                + String.format("Valor por dia:   R$ %8.2f%n", veiculo.calcularValorPorDia())
                + "\n"
                + "Status: " + status + "\n"
                + String.format("Valor total: R$ %.2f", calcularValor());
    }

    // texto que aparece na caixa de seleção da aba Devolução
    @Override
    public String toString() {
        return "#" + id + " - " + cliente.getNome() + " - " + veiculo;
    }
}
