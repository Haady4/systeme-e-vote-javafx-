-- =========================================================
-- Schéma de base de données — UAM e-Vote
-- =========================================================
CREATE DATABASE IF NOT EXISTS uam_evote CHARACTER SET utf8mb4;
USE uam_evote;

CREATE TABLE users (
    id                INT(11) AUTO_INCREMENT PRIMARY KEY,
    matricule         VARCHAR(20) NOT NULL UNIQUE,
    nom               VARCHAR(50) NOT NULL,
    prenom            VARCHAR(50) NOT NULL,
    email             VARCHAR(100) NOT NULL,
    login             VARCHAR(50) NOT NULL UNIQUE,
    password          VARCHAR(255) NOT NULL,        -- hash SHA-256 stocké en hexadécimal
    role              ENUM('ADMIN', 'ETUDIANT', 'ENSEIGNANT') NOT NULL,
    ufr               VARCHAR(50) DEFAULT NULL,
    departement       VARCHAR(50) DEFAULT NULL,
    niveau_filiere    VARCHAR(50) DEFAULT NULL
);

CREATE TABLE elections (
    id                  INT(11) AUTO_INCREMENT PRIMARY KEY,
    titre               VARCHAR(200) NOT NULL,
    type_election       VARCHAR(100) NOT NULL,
    date_debut          DATETIME NOT NULL,
    date_fin            DATETIME NOT NULL,
    statut              ENUM('EN_PREPARATION', 'OUVERTE', 'FERMEE') DEFAULT 'EN_PREPARATION',
    cible_role          ENUM('ADMIN', 'ETUDIANT', 'ENSEIGNANT', 'TOUS') NOT NULL,
    cible_ufr           VARCHAR(50) DEFAULT NULL,
    cible_departement   VARCHAR(50) DEFAULT NULL,
    cible_niveau        VARCHAR(50) DEFAULT NULL,
    resultats_publies   TINYINT(1) DEFAULT 0
);

CREATE TABLE candidats (
    id            INT(11) AUTO_INCREMENT PRIMARY KEY,
    election_id   INT(11) NOT NULL,
    nom           VARCHAR(50) NOT NULL,
    prenom        VARCHAR(50) NOT NULL,
    profession    VARCHAR(100) DEFAULT NULL,
    programme     TEXT DEFAULT NULL,
    photo         VARCHAR(255) DEFAULT NULL,
    CONSTRAINT fk_candidats_election
        FOREIGN KEY (election_id) REFERENCES elections(id)
        ON DELETE CASCADE
);

CREATE TABLE votes (
    id              INT(11) AUTO_INCREMENT PRIMARY KEY,
    election_id     INT(11) NOT NULL,
    candidat_id     INT(11) NOT NULL,
    utilisateur_id  INT(11) NOT NULL,
    date_vote       DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_votes_election
        FOREIGN KEY (election_id) REFERENCES elections(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_votes_candidat
        FOREIGN KEY (candidat_id) REFERENCES candidats(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_votes_utilisateur
        FOREIGN KEY (utilisateur_id) REFERENCES users(id)
        ON DELETE CASCADE,
    -- Empêche un même électeur de voter deux fois pour la même élection
    CONSTRAINT uq_votes_election_utilisateur
        UNIQUE (election_id, utilisateur_id)
);
