package app;

import dao.DatabaseConnection;
import java.sql.Connection;

public class TestConnection {
    public static void main(String[] args) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn != null && !conn.isClosed()) {
                System.out.println("Test de connexion réussi !");
            }
        } catch (Exception e) {
            System.err.println("Échec de la connexion :");
            e.printStackTrace();
        }
    }
}