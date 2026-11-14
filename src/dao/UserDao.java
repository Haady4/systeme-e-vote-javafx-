package dao;

import model.Election;
import model.User;
import model.User.Role;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDao {

    
    public User authentifier(String login, String passwordHache) {
        String sql = "SELECT * FROM users WHERE login = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, login);
            stmt.setString(2, passwordHache);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    
    public boolean ajouter(User user) {
        String sql = "INSERT INTO users (matricule, nom, prenom, email, login, password, role, ufr, departement, niveau_filiere) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getMatricule());
            stmt.setString(2, user.getNom());
            stmt.setString(3, user.getPrenom());
            stmt.setString(4, user.getEmail());
            stmt.setString(5, user.getLogin());
            stmt.setString(6, user.getPassword());
            stmt.setString(7, user.getRole().name());
            stmt.setString(8, user.getUfr());
            stmt.setString(9, user.getDepartement());
            stmt.setString(10, user.getNiveauFiliere());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean modifier(User user) {
        String sql = "UPDATE users SET matricule=?, nom=?, prenom=?, email=?, login=?, password=?, role=?, ufr=?, departement=?, niveau_filiere=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getMatricule());
            stmt.setString(2, user.getNom());
            stmt.setString(3, user.getPrenom());
            stmt.setString(4, user.getEmail());
            stmt.setString(5, user.getLogin());
            stmt.setString(6, user.getPassword());
            stmt.setString(7, user.getRole().name());
            stmt.setString(8, user.getUfr());
            stmt.setString(9, user.getDepartement());
            stmt.setString(10, user.getNiveauFiliere());
            stmt.setInt(11, user.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean supprimer(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<User> rechercher(String terme) {
        List<User> liste = new ArrayList<>();
        String motif = "%" + terme + "%";
        String sql = "SELECT * FROM users WHERE matricule LIKE ? OR nom LIKE ? OR prenom LIKE ? " +
                     "OR login LIKE ? OR email LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, motif);
            stmt.setString(2, motif);
            stmt.setString(3, motif);
            stmt.setString(4, motif);
            stmt.setString(5, motif);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                liste.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }


    /**
     * Compte les utilisateurs éligibles à voter pour une élection donnée,
     * selon les mêmes critères de ciblage (rôle, UFR, département, niveau)
     * que ceux utilisés pour déterminer les élections accessibles à un
     * électeur (voir ElectionDao.getElectionsAccessibles). Utilisé pour le
     * taux de participation du tableau de bord.
     */
    public int compterElecteursEligibles(Election election) {
        String sql = "SELECT COUNT(*) FROM users WHERE " +
                     "((? = 'TOUS' AND role <> 'ADMIN') OR role = ?) " +
                     "AND (? IS NULL OR ufr = ?) " +
                     "AND (? IS NULL OR departement = ?) " +
                     "AND (? IS NULL OR niveau_filiere = ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String cibleRole = election.getCibleRole().name();
            stmt.setString(1, cibleRole);
            stmt.setString(2, cibleRole);
            stmt.setString(3, election.getCibleUfr());
            stmt.setString(4, election.getCibleUfr());
            stmt.setString(5, election.getCibleDepartement());
            stmt.setString(6, election.getCibleDepartement());
            stmt.setString(7, election.getCibleNiveau());
            stmt.setString(8, election.getCibleNiveau());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<User> tousLesUtilisateurs() {
        List<User> liste = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                liste.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setMatricule(rs.getString("matricule"));
        u.setNom(rs.getString("nom"));
        u.setPrenom(rs.getString("prenom"));
        u.setEmail(rs.getString("email"));
        u.setLogin(rs.getString("login"));
        u.setPassword(rs.getString("password"));
        u.setRole(Role.valueOf(rs.getString("role")));
        u.setUfr(rs.getString("ufr"));
        u.setDepartement(rs.getString("departement"));
        u.setNiveauFiliere(rs.getString("niveau_filiere"));
        return u;
    }
}