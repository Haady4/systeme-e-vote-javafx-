package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Election;
import model.Election.CibleRole;
import model.Election.Statut;
import model.User;

public class ElectionDao {

    public boolean creerElection(Election e) {
        String sql = "INSERT INTO elections (titre, type_election, date_debut, date_fin, statut, cible_role, cible_ufr, cible_departement, cible_niveau) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, e.getTitre());
            stmt.setString(2, e.getTypeElection());
            stmt.setTimestamp(3, Timestamp.valueOf(e.getDateDebut()));
            stmt.setTimestamp(4, Timestamp.valueOf(e.getDateFin()));
            stmt.setString(5, e.getStatut().name());
            stmt.setString(6, e.getCibleRole().name());
            stmt.setString(7, e.getCibleUfr());
            stmt.setString(8, e.getCibleDepartement());
            stmt.setString(9, e.getCibleNiveau());

            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    
    public List<Election> getElectionsAccessibles(User user) {
        List<Election> liste = new ArrayList<>();
        String sql = "SELECT * FROM elections WHERE statut = 'OUVERTE' " +
                     "AND (cible_role = 'TOUS' OR cible_role = ?) " +
                     "AND (cible_ufr IS NULL OR cible_ufr = ?) " +
                     "AND (cible_departement IS NULL OR cible_departement = ?) " +
                     "AND (cible_niveau IS NULL OR cible_niveau = ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getRole().name());
            stmt.setString(2, user.getUfr());
            stmt.setString(3, user.getDepartement());
            stmt.setString(4, user.getNiveauFiliere());

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                liste.add(mapResultSetToElection(rs));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return liste;
    }
    public List<Election> getElectionsResultatsPublies(User user) {
    List<Election> liste = new ArrayList<>();
    String sql = "SELECT * FROM elections WHERE resultats_publies = TRUE " +
                 "AND (cible_role = 'TOUS' OR cible_role = ?) " +
                 "AND (cible_ufr IS NULL OR cible_ufr = ?) " +
                 "AND (cible_departement IS NULL OR cible_departement = ?) " +
                 "AND (cible_niveau IS NULL OR cible_niveau = ?)";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, user.getRole().name());
        stmt.setString(2, user.getUfr());
        stmt.setString(3, user.getDepartement());
        stmt.setString(4, user.getNiveauFiliere());

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            liste.add(mapResultSetToElection(rs));
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
    }
    return liste;
}

    private Election mapResultSetToElection(ResultSet rs) throws SQLException {
        Election e = new Election();
        e.setId(rs.getInt("id"));
        e.setTitre(rs.getString("titre"));
        e.setTypeElection(rs.getString("type_election"));
        e.setDateDebut(rs.getTimestamp("date_debut").toLocalDateTime());
        e.setDateFin(rs.getTimestamp("date_fin").toLocalDateTime());
        e.setStatut(Statut.valueOf(rs.getString("statut")));
        e.setCibleRole(CibleRole.valueOf(rs.getString("cible_role")));
        e.setCibleUfr(rs.getString("cible_ufr"));
        e.setCibleDepartement(rs.getString("cible_departement"));
        e.setCibleNiveau(rs.getString("cible_niveau"));
        e.setResultatsPublies(rs.getBoolean("resultats_publies"));
        return e;
    }
    
    public List<Election> getToutesLesElections() {
        List<Election> liste = new ArrayList<>();
        String sql = "SELECT * FROM elections";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                liste.add(mapResultSetToElection(rs));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return liste;
    }
    public boolean publierResultats(int electionId) {
    String sql = "UPDATE elections SET resultats_publies = TRUE WHERE id = ?";
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, electionId);
        return stmt.executeUpdate() > 0;
    } catch (SQLException ex) {
        ex.printStackTrace();
        return false;
    }
}
    public boolean changerStatut(int electionId, Statut statut) {
        String sql = "UPDATE elections SET statut = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, statut.name());
            stmt.setInt(2, electionId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public void mettreAJourStatutsExpires() {
    String sql = "UPDATE elections SET statut = 'FERMEE' " +
                 "WHERE date_fin < NOW() AND statut != 'FERMEE'";
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.executeUpdate();
    } catch (SQLException ex) {
        ex.printStackTrace();
    }
}
}