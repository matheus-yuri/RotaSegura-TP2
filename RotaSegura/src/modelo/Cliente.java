package modelo;

// Matheus Yuri Sadamatsu Rodrigues - RA 1301392611037
// Classe do cliente. Coloquei as validações nos sets, assim não
// tem como criar um cliente sem nome ou sem CPF.
public class Cliente {

    private int id; // o id quem gera é o banco (AUTO_INCREMENT)
    private String nome;
    private String cpf;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {

        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "O nome do cliente não pode ficar vazio."
            );
        }

        this.nome = nome.trim();
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {

        if (cpf == null || cpf.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "O CPF do cliente não pode ficar vazio."
            );
        }

        this.cpf = cpf.trim();
    }

    // é esse texto que aparece na caixa de seleção da tela de locação
    @Override
    public String toString() {
        return nome + " - " + cpf;
    }
}
