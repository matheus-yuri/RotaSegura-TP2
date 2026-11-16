package dao;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

// Matheus Yuri Sadamatsu Rodrigues - RA 1301392611037
// Classe que abre a conexão com o MySQL.
// Os dados de acesso ficam no db.properties, assim se mudar a porta ou a
// senha não preciso mexer no código.
public class ConexaoBD {

    private static final String ARQUIVO_CONFIG = "db.properties";

    // valores padrão, caso não ache o db.properties (3307 é a porta do meu XAMPP)
    private static String url = "jdbc:mysql://127.0.0.1:3307/rotasegura";
    private static String usuario = "root";
    private static String senha = "";

    // bloco static: roda uma vez só, na primeira vez que a classe é usada
    static {
        Properties config = new Properties();

        try (FileInputStream arquivo = new FileInputStream(ARQUIVO_CONFIG)) {

            config.load(arquivo);

            url = config.getProperty("db.url", url);
            usuario = config.getProperty("db.usuario", usuario);
            senha = config.getProperty("db.senha", senha);

        } catch (IOException e) {
            System.out.println(
                "Arquivo " + ARQUIVO_CONFIG
                + " não encontrado. Usando configuração padrão."
            );
        }
    }

    // construtor privado porque é só chamar ConexaoBD.getConexao()
    private ConexaoBD() {
    }

    // abre uma conexão nova (quem chamou é que fecha)
    public static Connection getConexao() throws SQLException {
        return DriverManager.getConnection(url, usuario, senha);
    }

    // testa a conexão quando o programa abre, pra avisar logo se o banco estiver desligado
    public static void testarConexao() {

        try (Connection con = getConexao()) {

            // se chegou aqui, conectou
            System.out.println("Conectado ao banco: " + con.getCatalog());

        } catch (SQLException e) {
            throw new PersistenciaException(
                "Não foi possível conectar ao banco de dados.\n\n"
                + "Verifique se:\n"
                + "1. O MySQL está iniciado no XAMPP (botão Start);\n"
                + "2. O script sql/rotasegura.sql foi importado;\n"
                + "3. O arquivo lib/mysql-connector-j.jar existe;\n"
                + "4. Os dados do arquivo db.properties estão corretos.\n\n"
                + "Detalhe: " + e.getMessage(),
                e
            );
        }
    }
}
