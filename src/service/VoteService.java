package service;

import dao.VoteDao;
import model.Election;
import model.Vote;

public class VoteService {
    private final VoteDao voteDao = new VoteDao();

    public boolean voter(int utilisateurId, Election election, int candidatId) {
        
        if (election.getStatut() != Election.Statut.OUVERTE) {
            System.out.println("Erreur : L'élection n'est pas ouverte.");
            return false;
        }

        
        if (voteDao.aDejaVote(utilisateurId, election.getId())) {
            System.out.println("Erreur : Vous avez déjà voté pour cette élection.");
            return false;
        }

        
        Vote vote = new Vote();
        vote.setElectionId(election.getId());
        vote.setCandidatId(candidatId);
        vote.setUtilisateurId(utilisateurId);

        return voteDao.enregistrerVote(vote);
    }
}