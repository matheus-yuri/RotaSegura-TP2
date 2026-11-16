package modelo;

// Matheus Yuri Sadamatsu Rodrigues - RA 1301392611037
// Carro popular: diária normal (100%), seguro de 5% e manutenção de 3%.
// Cada tipo sobrescreve os cálculos do Veiculo do seu jeito.
public class Popular extends Veiculo {

    @Override
    public double calcularDiaria() {
        return getValorDiaria();
    }

    @Override
    public double calcularSeguro() {
        return getValorDiaria() * 0.05;
    }

    @Override
    public double calcularManutencao() {
        return getValorDiaria() * 0.03;
    }
}