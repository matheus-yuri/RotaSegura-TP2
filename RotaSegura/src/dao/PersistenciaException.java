package dao;

// Matheus Yuri Sadamatsu Rodrigues - RA 1301392611037
// Exceção minha pra erros de banco. Os DAOs pegam a SQLException e jogam
// essa aqui com uma mensagem mais fácil de entender, aí a tela só mostra.
public class PersistenciaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public PersistenciaException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
