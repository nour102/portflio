package config.java;
//responsable de toutes les opérations liées à la base
//Importation des classes nécessaires pour la manipulation de la base de données
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

//Déclaration de la classe ContactDAO (DAO = Data Access Object)
public class ContactDAO {
// Requêtes SQL pour la manipuler les données dans la table Contact
    private static final String INSERT_SQL = "INSERT INTO Contact (Nom_contact, Prenom, email, tel) VALUES (?, ?, ?, ?)";
    private static final String SELECT_ALL_SQL = "SELECT * FROM Contact";
    private static final String UPDATE_SQL = "UPDATE Contact SET Nom_contact=?, Prenom=?, email=?, tel=? WHERE id_contact=?";
    private static final String DELETE_SQL = "DELETE FROM Contact WHERE id_contact=?";
    private static final String SEARCH_SQL = "SELECT * FROM Contact WHERE Nom_contact LIKE ? OR Prenom LIKE ? OR tel LIKE ?";
    private static final String CHECK_EMAIL_SQL = "SELECT 1 FROM Contact WHERE email=?";
    private static final String CHECK_PHONE_SQL = "SELECT 1 FROM Contact WHERE tel=?";

    /**
     * Ajoute un nouveau contact à la base de données.
     * @param contact L'objet Contact à ajouter.
     * @return true si l'ajout a réussi, false sinon.
     */
    public boolean addContact(Contact contact) {
    	// try-with-resources : la connexion et la requête sont automatiquement fermées
        try (Connection conn = Config.getConnection(); // Obtient une connexion à la base de données
             PreparedStatement stmt = conn.prepareStatement(INSERT_SQL)) { // Prépare la requête d'insertion
            
 // Définit les paramètres de la requête
            stmt.setString(1, contact.getNom());
            stmt.setString(2, contact.getPrenom());
            stmt.setString(3, contact.getEmail());
            stmt.setString(4, contact.getTel());
            
 // Exécute la requête et retourne true si au moins une ligne a été insérée
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            // Gère les erreurs SQL et affiche un message d'erreur
            System.err.println("Erreur ajout contact: " + e.getMessage());
            return false;
        }
    }

    /**
     * Récupère tous les contacts de la base de données.
     * @return Une liste de tous les objets Contact.
     */
    public List<Contact> getAllContacts() {
        List<Contact> contacts = new ArrayList<>(); // Crée une liste pour stocker les contacts
        try (Connection conn = Config.getConnection(); // Obtient une connexion à la base de données
             Statement stmt = conn.createStatement(); // Crée une instruction
             ResultSet rs = stmt.executeQuery(SELECT_ALL_SQL)) { // Exécute la requête de sélection
            
 // Parcours des résultats de la requête et création des objets Contact
            while (rs.next()) {
                // Crée un nouvel objet Contact avec les données de la ligne actuelle
                contacts.add(new Contact(
                    rs.getInt("id_contact"),
                    rs.getString("Nom_contact"),
                    rs.getString("Prenom"),
                    rs.getString("email"),
                    rs.getString("tel")
                ));
            }
        } catch (SQLException e) {
 // Gère les erreurs SQL et affiche un message d'erreur
            System.err.println("Erreur récupération contacts: " + e.getMessage());
        }
        return contacts; // Retourne la liste des contacts
    }

    /**
     * Met à jour les informations d'un contact existant dans la base de données.
     * @param contact L'objet Contact avec les informations mises à jour.
     * @return true si la mise à jour a réussi, false sinon.
     */
    public boolean updateContact(Contact contact) {
        try (Connection conn = Config.getConnection(); // Obtient une connexion à la base de données
             PreparedStatement stmt = conn.prepareStatement(UPDATE_SQL)) { // Prépare la requête de mise à jour
            
  // Définit les paramètres de la requête
            stmt.setString(1, contact.getNom());
            stmt.setString(2, contact.getPrenom());
            stmt.setString(3, contact.getEmail());
            stmt.setString(4, contact.getTel());
            stmt.setInt(5, contact.getId()); // L'ID est utilisé pour identifier le contact à mettre à jour
            
 // Exécute la requête et retourne true si au moins une ligne a été affectée
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
 // Gère les erreurs SQL et affiche un message d'erreur
            System.err.println("Erreur mise à jour contact: " + e.getMessage());
            return false;
        }
    }

    /**
     * Supprime un contact de la base de données par son ID.
     * @param id L'ID du contact à supprimer.
     * @return true si la suppression a réussi, false sinon.
     */
    public boolean deleteContact(int id) {
        try (Connection conn = Config.getConnection(); // Obtient une connexion à la base de données
             PreparedStatement stmt = conn.prepareStatement(DELETE_SQL)) { // Prépare la requête de suppression
            
            // Définit le paramètre de la requête (l'ID du contact)
            stmt.setInt(1, id);
            // Exécute la requête et retourne true si au moins une ligne a été affectée
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            // Gère les erreurs SQL et affiche un message d'erreur
            System.err.println("Erreur suppression contact: " + e.getMessage());
            return false;
        }
    }

    /**
     * Recherche des contacts dans la base de données par nom, prénom ou numéro de téléphone.
     * @param term Le terme de recherche.
     * @return Une liste des objets Contact correspondant au terme de recherche.
     */
    public List<Contact> searchContacts(String term) {
        List<Contact> results = new ArrayList<>(); // Crée une liste pour stocker les résultats
        try (Connection conn = Config.getConnection(); // Obtient une connexion à la base de données
             PreparedStatement stmt = conn.prepareStatement(SEARCH_SQL)) { // Prépare la requête de recherche
            
            String pattern = "%" + term + "%"; // Crée un motif pour la recherche (correspondance partielle)
            // Définit les paramètres de la requête
            stmt.setString(1, pattern); // Recherche dans le nom
            stmt.setString(2, pattern); // Recherche dans le prénom
            stmt.setString(3, pattern); // Recherche dans le numéro de téléphone
            
            try (ResultSet rs = stmt.executeQuery()) { // Exécute la requête et obtient les résultats
                // Parcourt les résultats de la requête
                while (rs.next()) {
                    // Crée un nouvel objet Contact avec les données de la ligne actuelle
                    results.add(new Contact(
                        rs.getInt("id_contact"),
                        rs.getString("Nom_contact"),
                        rs.getString("Prenom"),
                        rs.getString("email"),
                        rs.getString("tel")
                    ));
                }
            }
        } catch (SQLException e) {
            // Gère les erreurs SQL et affiche un message d'erreur
            System.err.println("Erreur recherche contacts: " + e.getMessage());
        }
        return results; // Retourne la liste des contacts trouvés
    }

    /**
     * Vérifie si un email existe déjà dans la base de données.
     * @param email L'email à vérifier.
     * @return true si l'email existe, false sinon.
     */
    public boolean emailExists(String email) {
        try (Connection conn = Config.getConnection(); // Obtient une connexion à la base de données
             PreparedStatement stmt = conn.prepareStatement(CHECK_EMAIL_SQL)) { // Prépare la requête de vérification d'email
            
            // Définit le paramètre de la requête (l'email)
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) { // Exécute la requête et obtient les résultats
                // Retourne true si le jeu de résultats contient au moins une ligne (l'email existe)
                return rs.next();
            }
        } catch (SQLException e) {
            // Gère les erreurs SQL et affiche un message d'erreur
            System.err.println("Erreur vérification email: " + e.getMessage());
            return false;
        }
    }

    /**
     * Vérifie si un numéro de téléphone existe déjà dans la base de données.
     * @param phone Le numéro de téléphone à vérifier.
     * @return true si le numéro de téléphone existe, false sinon.
     */
    public boolean phoneExists(String phone) {
        try (Connection conn = Config.getConnection(); // Obtient une connexion à la base de données
             PreparedStatement stmt = conn.prepareStatement(CHECK_PHONE_SQL)) { // Prépare la requête de vérification de téléphone
            
            // Définit le paramètre de la requête (le numéro de téléphone)
            stmt.setString(1, phone);
            try (ResultSet rs = stmt.executeQuery()) { // Exécute la requête et obtient les résultats
                // Retourne true si le jeu de résultats contient au moins une ligne (le numéro de téléphone existe)
                return rs.next();
            }
        } catch (SQLException e) {
            // Gère les erreurs SQL et affiche un message d'erreur
            System.err.println("Erreur vérification téléphone: " + e.getMessage());
            return false;
        }
    }
}
