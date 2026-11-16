package modelo;

// Matheus Yuri Sadamatsu Rodrigues - RA 1301392611037
// SUV: diária com 20% a mais (120%), seguro de 12% e manutenção de 7%.
// Cada tipo sobrescreve os cálculos do Veiculo do seu jeito.
public class SUV extends Veiculo {

    @Override
    public double calcularDiaria() {
        return getValorDiaria() * 1.20;
    }

    @Override
    public double calcularSeguro() {
        return getValorDiaria() * 0.12;
    }

    @Override
    public double calcularManutencao() {
        return getValorDiaria() * 0.07;
    }
}