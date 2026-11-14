-- =========================================================
-- Insertion des comptes de test — UAM e-Vote
-- Mots de passe hachés en SHA-256 (hex), comme attendu par
-- utils/PasswordUtil.java
-- =========================================================
USE uam_evote;

INSERT INTO users (matricule, nom, prenom, email, login, password, role, ufr, departement, niveau_filiere) VALUES
('ADM001', 'Admin',  'Admin',  'admin@uam-evote.local',  'admin',  '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918', 'ADMIN',      NULL, NULL, NULL),
('ETU001', 'Hady',   'Hady',   'hady@uam-evote.local',   'hady',   '36432aa0a54a06c13ca2ff16cef78ca66e1cd5fa869f36791a79bc4f4c5d8120', 'ETUDIANT',   NULL, NULL, NULL),
('ENS001', 'Sow',    'Sow',    'sow@uam-evote.local',    'sow',    '756e1aca491f16516fc82e06c2a4f9c7fe92858c622bac443131945edda48137', 'ENSEIGNANT', NULL, NULL, NULL),
('ETU003', 'Nogaye', 'Nogaye', 'nogaye@uam-evote.local', 'nogaye', 'b076d0643d8294e911d8e3e67c6bdf2a7668dcafa8558a4d36b7442783d4b055', 'ETUDIANT',   NULL, NULL, NULL),
('ENS002', 'Lamine', 'Lamine', 'lamine@uam-evote.local', 'lamine', '13d10e597b3fbae09ff2fcb858950b9ec2b4f06264ae94171b14719456c89beb', 'ENSEIGNANT', NULL, NULL, NULL);
