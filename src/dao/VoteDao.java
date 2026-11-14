package dao;

import model.Vote;
import java.sql.*;

public class VoteDao {

    
    public boolean aDejaVote(int utilisateurId, int electionId) {
        String sql = "SELECT COUNT(*) FROM votes WHERE utilisateur_id = ? AND election_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, utilisateurId);
            stmt.setInt(2, electionId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    
    public boolean enregistrerVote(Vote v) {
        if (aDejaVote(v.getUtilisateurId(), v.getElectionId())) {
            System.out.println("Erreur : Cet utilisateur a déjà voté pour cette élection.");
            return false;
        }

        String sql = "INSERT INTO votes (election_id, candidat_id, utilisateur_id) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, v.getElectionId());
            stmt.setInt(2, v.getCandidatId());
            stmt.setInt(3, v.getUtilisateurId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    
    public int compterVotesElection(int electionId) {
        String sql = "SELECT COUNT(*) FROM votes WHERE election_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, electionId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public int getNombreVoixCandidat(int candidatId) {
        String sql = "SELECT COUNT(*) FROM votes WHERE candidat_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, candidatId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}