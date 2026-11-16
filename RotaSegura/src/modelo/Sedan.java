package modelo;

// Matheus Yuri Sadamatsu Rodrigues - RA 1301392611037
// Sedan: diária com 10% a mais (110%), seguro de 8% e manutenção de 5%.
// Cada tipo sobrescreve os cálculos do Veiculo do seu jeito.
public class Sedan extends Veiculo {

    @Override
    public double calcularDiaria() {
        return getValorDiaria() * 1.10;
    }

    @Override
    public double calcularSeguro() {
        return getValorDiaria() * 0.08;
    }

    @Override
    public double calcularManutencao() {
        return getValorDiaria() * 0.05;
    }
}