package service;

import dao.CandidatDao;
import dao.VoteDao;
import model.Candidat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DepouillementService {
    private final CandidatDao candidatDao = new CandidatDao();
    private final VoteDao voteDao = new VoteDao();

    public Map<Candidat, Integer> calculerResultats(int electionId) {
        Map<Candidat, Integer> resultats = new HashMap<>();
        List<Candidat> candidats = candidatDao.getCandidatsParElection(electionId);

        for (Candidat candidat : candidats) {
            int voix = voteDao.getNombreVoixCandidat(candidat.getId());
            resultats.put(candidat, voix);
        }
        return resultats;
    }

    public Candidat determinerGagnant(Map<Candidat, Integer> resultats) {
        Candidat gagnant = null;
        int maxVoix = -1;

        for (Map.Entry<Candidat, Integer> entry : resultats.entrySet()) {
            if (entry.getValue() > maxVoix) {
                maxVoix = entry.getValue();
                gagnant = entry.getKey();
            }
        }
        return gagnant;
    }

    public int calculerTotalVotes(Map<Candidat, Integer> resultats) {
        int total = 0;
        for (int voix : resultats.values()) {
            total += voix;
        }
        return total;
    }
}