package utils;

import model.Candidat;
import model.Election;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class PdfExporter {

    /**
     * Génère le procès-verbal de dépouillement en PDF (vrai PDF, pas un .txt).
     * @param election            l'élection concernée
     * @param resultats           voix obtenues par candidat
     * @param electeursEligibles  nombre d'électeurs éligibles (pour le taux de participation)
     */
    public static void genererProcesVerbal(Election election, Map<Candidat, Integer> resultats, int electeursEligibles) {
        String fileName = "proces_verbal_election_" + election.getId() + ".pdf";
        DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        int totalVotes = 0;
        for (int voix : resultats.values()) {
            totalVotes += voix;
        }
        double taux = electeursEligibles > 0 ? (totalVotes * 100.0 / electeursEligibles) : 0.0;

        PdfDocument pdf = new PdfDocument();

        pdf.ajouterTexte("PROCÈS-VERBAL DE DÉPOUILLEMENT", 16, true);
        pdf.ligneVide();
        pdf.ligneSeparatrice();
        pdf.ligneVide();

        pdf.ajouterTexte("Élection : " + election.getTitre(), 12, false);
        pdf.ajouterTexte("Type : " + election.getTypeElection(), 12, false);
        pdf.ajouterTexte("Statut : " + election.getStatut(), 12, false);
        if (election.getDateDebut() != null) {
            pdf.ajouterTexte("Début du scrutin : " + election.getDateDebut().format(formatDate), 12, false);
        }
        if (election.getDateFin() != null) {
            pdf.ajouterTexte("Fin du scrutin : " + election.getDateFin().format(formatDate), 12, false);
        }

        pdf.ligneVide();
        pdf.ligneSeparatrice();
        pdf.ligneVide();

        pdf.ajouterTexte("STATISTIQUES DE PARTICIPATION", 13, true);
        pdf.ligneVide();
        pdf.ajouterTexte("Électeurs éligibles : " + electeursEligibles, 12, false);
        pdf.ajouterTexte("Votes exprimés : " + totalVotes, 12, false);
        pdf.ajouterTexte(String.format("Taux de participation : %.1f %%", taux), 12, false);

        pdf.ligneVide();
        pdf.ligneSeparatrice();
        pdf.ligneVide();

        pdf.ajouterTexte("RÉSULTATS PAR CANDIDAT", 13, true);
        pdf.ligneVide();

        Candidat gagneur = null;
        int maxVoix = -1;
        for (Map.Entry<Candidat, Integer> entry : resultats.entrySet()) {
            Candidat c = entry.getKey();
            int voix = entry.getValue();
            double pourcentage = totalVotes > 0 ? (voix * 100.0 / totalVotes) : 0.0;
            pdf.ajouterTexte("- " + c.getPrenom() + " " + c.getNom() + " : " + voix
                    + " voix (" + String.format("%.1f", pourcentage) + " %)", 12, false);

            if (voix > maxVoix) {
                maxVoix = voix;
                gagneur = c;
            }
        }

        pdf.ligneVide();
        pdf.ligneSeparatrice();
        pdf.ligneVide();

        if (gagneur != null && maxVoix > 0) {
            pdf.ajouterTexte("Vainqueur : " + gagneur.getPrenom() + " " + gagneur.getNom()
                    + " (" + maxVoix + " voix)", 13, true);
        } else {
            pdf.ajouterTexte("Aucun vote enregistré pour le moment.", 13, true);
        }

        pdf.ligneVide();
        pdf.ajouterTexte("Document généré le " + LocalDateTime.now().format(formatDate), 9, false);

        try {
            pdf.enregistrer(fileName);
            System.out.println("Export PDF réussi : " + fileName);
            ouvrirFichier(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Ouvre le PDF généré avec le lecteur par défaut du système (si disponible). */
    private static void ouvrirFichier(String fileName) {
        try {
            java.io.File fichier = new java.io.File(fileName).getAbsoluteFile();
            if (java.awt.Desktop.isDesktopSupported()
                    && java.awt.Desktop.getDesktop().isSupported(java.awt.Desktop.Action.OPEN)) {
                java.awt.Desktop.getDesktop().open(fichier);
            } else {
                System.out.println("Ouverture automatique non supportée. Fichier disponible ici : "
                        + fichier.getAbsolutePath());
            }
        } catch (IOException ex) {
            System.out.println("Impossible d'ouvrir automatiquement le PDF. Fichier disponible ici : "
                    + new java.io.File(fileName).getAbsolutePath());
        }
    }
}
