package modelo;

// Matheus Yuri Sadamatsu Rodrigues - RA 1301392611037
// Classe mãe dos veículos. É abstrata porque não existe um "veículo genérico":
// todo carro da frota é Popular, Sedan ou SUV, e cada um calcula do seu jeito.
public abstract class Veiculo {

    // deixei o status em constantes pra não errar a escrita em outros lugares
    public static final String DISPONIVEL = "DISPONIVEL";
    public static final String LOCADO = "LOCADO";

    private int id;
    private String modelo;
    private String placa;
    private double valorDiaria;
    private String status = DISPONIVEL;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {

        if (modelo == null || modelo.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "O modelo do veículo não pode ficar vazio."
            );
        }

        this.modelo = modelo.trim();
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {

        if (placa == null || placa.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "A placa do veículo não pode ficar vazia."
            );
        }

        this.placa = placa.trim().toUpperCase();
    }

    public double getValorDiaria() {
        return valorDiaria;
    }

    public void setValorDiaria(double valorDiaria) {
        if (valorDiaria <= 0) {
            throw new IllegalArgumentException(
                "O valor da diária deve ser maior que zero."
            );
        }

        this.valorDiaria = valorDiaria;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {

        if (!DISPONIVEL.equals(status) && !LOCADO.equals(status)) {
            throw new IllegalArgumentException(
                "Status inválido: " + status
            );
        }

        this.status = status;
    }

    public boolean isDisponivel() {
        return DISPONIVEL.equals(status);
    }

    // devolve "Popular", "Sedan" ou "SUV" (nome da classe)
    // é esse texto que vai pra coluna "tipo" no banco
    public String getTipo() {
        return getClass().getSimpleName();
    }

    // Recebe o tipo em texto e cria o objeto certo.
    // Uso isso quando leio do banco e quando cadastro pela tela.
    public static Veiculo criar(String tipo) {

        switch (tipo) {
            case "Popular":
                return new Popular();
            case "Sedan":
                return new Sedan();
            case "SUV":
                return new SUV();
            default:
                throw new IllegalArgumentException(
                    "Tipo de veículo inválido: " + tipo);
        }
    }

    // cada subclasse é obrigada a implementar esses três
    public abstract double calcularDiaria();

    public abstract double calcularSeguro();

    public abstract double calcularManutencao();

    // quanto o cliente paga por dia (soma dos três)
    public double calcularValorPorDia() {
        return calcularDiaria() + calcularSeguro() + calcularManutencao();
    }

    // texto que aparece nas caixas de seleção da tela
    @Override
    public String toString() {
        return getTipo() + " - " + modelo + " (" + placa + ")";
    }
}
