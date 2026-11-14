package utils;

import model.Candidat;
import model.Election;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public class CsvExporter {

    public static void exporterResultats(Election election, Map<Candidat, Integer> resultats) {
        String fileName = "resultats_election_" + election.getId() + ".csv";
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            writer.println("Election ID,Titre,Candidat,Voix");
            
            for (Map.Entry<Candidat, Integer> entry : resultats.entrySet()) {
                Candidat c = entry.getKey();
                int voix = entry.getValue();
                writer.println(election.getId() + ",\"" + election.getTitre() + "\",\"" + c.getPrenom() + " " + c.getNom() + "\"," + voix);
            }
            System.out.println("Export CSV reussi : " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}