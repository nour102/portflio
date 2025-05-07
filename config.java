package config.java;
// pou gerer la connecxion a la base de données
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class config {
    
    private static final String URL = "jdbc:mysql://localhost:3306/contact";
    private static final String USER = "root"; 
    private static final String PASSWORD = "Joujou.melek123";

    // Constructeur privé pour empêcher l'instanciation
    private config() {
    }

    // Méthode pour obtenir une connexion à la base de données
    public static Connection getConnection() throws SQLException {
        Connection connect = null;
        try {
            // 1. Chargement explicite du pilote 
            Class.forName("com.mysql.jdbc.Driver");
            
            // 2. Ouverture de la connexion
            connect = DriverManager.getConnection(URL, USER, PASSWORD);
            
            return connect;
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, 
                "Pilote JDBC MySQL introuvable: com.mysql.jdbc.Driver", 
                "Erreur de Pilote", JOptionPane.ERROR_MESSAGE);
            throw new SQLException("Pilote JDBC MySQL introuvable", e);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, 
                "Erreur de connexion à la base: " + e.getMessage(), 
                "Erreur de Connexion", JOptionPane.ERROR_MESSAGE);
            throw e;
        }
    }

    // Méthode pour fermer une connexion
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Erreur lors de la fermeture: " + e.getMessage());
            }
        }
    }
}


