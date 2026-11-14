-- =========================================================
-- Migration point 3 — UAM e-Vote
-- Ajoute les contraintes UNIQUE et les clés étrangères qui
-- manquaient sur une base uam_evote déjà créée avec l'ancien
-- schema.sql (sans elles).
--
-- A exécuter une seule fois sur ta base existante.
-- =========================================================
USE uam_evote;

-- -------------------------------------------------------
-- Etape 0 (verification) : s'assurer qu'il n'y a pas deja
-- de doublons de login/matricule, sinon les ALTER TABLE
-- suivants vont echouer. Si une des deux requetes ci-dessous
-- renvoie une ligne, corrige/supprime le doublon avant de
-- continuer.
-- -------------------------------------------------------
SELECT login, COUNT(*) AS occurrences
FROM users
GROUP BY login
HAVING COUNT(*) > 1;

SELECT matricule, COUNT(*) AS occurrences
FROM users
GROUP BY matricule
HAVING COUNT(*) > 1;

-- -------------------------------------------------------
-- Etape 1 : unicite du login et du matricule
-- -------------------------------------------------------
ALTER TABLE users
    ADD CONSTRAINT uq_users_login UNIQUE (login);

ALTER TABLE users
    ADD CONSTRAINT uq_users_matricule UNIQUE (matricule);

-- -------------------------------------------------------
-- Etape 2 : cle etrangere candidats -> elections
-- -------------------------------------------------------
ALTER TABLE candidats
    ADD CONSTRAINT fk_candidats_election
        FOREIGN KEY (election_id) REFERENCES elections(id)
        ON DELETE CASCADE;

-- -------------------------------------------------------
-- Etape 3 : cles etrangeres + contrainte "un seul vote"
-- sur la table votes
-- -------------------------------------------------------
ALTER TABLE votes
    ADD CONSTRAINT fk_votes_election
        FOREIGN KEY (election_id) REFERENCES elections(id)
        ON DELETE CASCADE;

ALTER TABLE votes
    ADD CONSTRAINT fk_votes_candidat
        FOREIGN KEY (candidat_id) REFERENCES candidats(id)
        ON DELETE CASCADE;

ALTER TABLE votes
    ADD CONSTRAINT fk_votes_utilisateur
        FOREIGN KEY (utilisateur_id) REFERENCES users(id)
        ON DELETE CASCADE;

ALTER TABLE votes
    ADD CONSTRAINT uq_votes_election_utilisateur
        UNIQUE (election_id, utilisateur_id);
