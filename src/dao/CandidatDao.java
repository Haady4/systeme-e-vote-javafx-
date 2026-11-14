package dao;

import model.Candidat;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CandidatDao {

    public boolean ajouterCandidat(Candidat c) {
        String sql = "INSERT INTO candidats (election_id, nom, prenom, profession, programme, photo) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, c.getElectionId());
            stmt.setString(2, c.getNom());
            stmt.setString(3, c.getPrenom());
            stmt.setString(4, c.getProfession());
            stmt.setString(5, c.getProgramme());
            stmt.setString(6, c.getPhoto());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean supprimerCandidat(int candidatId) {
        String sql = "DELETE FROM candidats WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, candidatId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Candidat> getCandidatsParElection(int electionId) {
        List<Candidat> liste = new ArrayList<>();
        String sql = "SELECT * FROM candidats WHERE election_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, electionId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Candidat c = new Candidat();
                c.setId(rs.getInt("id"));
                c.setElectionId(rs.getInt("election_id"));
                c.setNom(rs.getString("nom"));
                c.setPrenom(rs.getString("prenom"));
                c.setProfession(rs.getString("profession"));
                c.setProgramme(rs.getString("programme"));
                c.setPhoto(rs.getString("photo"));
                liste.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }
}