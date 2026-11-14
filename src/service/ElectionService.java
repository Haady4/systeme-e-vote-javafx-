package service;

import dao.CandidatDao;
import dao.ElectionDao;
import dao.UserDao;
import dao.VoteDao;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Candidat;
import model.Election;
import model.User;

public class ElectionService {
    private final ElectionDao electionDao = new ElectionDao();
    private final CandidatDao candidatDao = new CandidatDao();
    private final VoteDao voteDao = new VoteDao();
    private final UserDao userDao = new UserDao();

    
    public List<Election> getElectionsDisponibles(User user) {
        return electionDao.getElectionsAccessibles(user);
    }
    

    public List<Election> getElectionsResultatsPublies(User user) {
    return electionDao.getElectionsResultatsPublies(user);
    }

    public boolean publierResultats(int electionId) {
        return electionDao.publierResultats(electionId);
    }

    
    public void mettreAJourStatutsElections(List<Election> elections) {
        LocalDateTime maintenant = LocalDateTime.now();
        for (Election e : elections) {
            if (maintenant.isAfter(e.getDateDebut()) && maintenant.isBefore(e.getDateFin())) {
                e.setStatut(Election.Statut.OUVERTE);
            } else if (maintenant.isAfter(e.getDateFin())) {
                e.setStatut(Election.Statut.FERMEE);
            }
        }
    }

    
    public Map<Candidat, Integer> depouillerResultats(int electionId) {
        Map<Candidat, Integer> resultats = new HashMap<>();
        List<Candidat> candidats = candidatDao.getCandidatsParElection(electionId);

        for (Candidat c : candidats) {
            int nbVoix = voteDao.getNombreVoixCandidat(c.getId());
            resultats.put(c, nbVoix);
        }
        return resultats;
    }
    public void mettreAJourStatutsExpires() {
    electionDao.mettreAJourStatutsExpires();
    }

    public int compterElecteursEligibles(Election election) {
        return userDao.compterElecteursEligibles(election);
    }

    public int compterVotes(int electionId) {
        return voteDao.compterVotesElection(electionId);
    }
}