-- =========================================================
-- Insertion de données de test — élections, candidats, votes
-- UAM e-Vote
-- =========================================================
USE uam_evote;

-- 1) Une élection OUVERTE, accessible à tout le monde (TOUS)
INSERT INTO elections (titre, type_election, date_debut, date_fin, statut, cible_role, cible_ufr, cible_departement, cible_niveau, resultats_publies)
VALUES ('Election du Bureau des Etudiants 2026', 'Bureau Etudiant', NOW() - INTERVAL 1 DAY, NOW() + INTERVAL 30 DAY, 'OUVERTE', 'TOUS', NULL, NULL, NULL, 0);

SET @election_ouverte_id = LAST_INSERT_ID();

INSERT INTO candidats (election_id, nom, prenom, programme) VALUES
(@election_ouverte_id, 'Diop',   'Fatou',    'Amelioration de la vie associative et des infrastructures sportives.'),
(@election_ouverte_id, 'Ndiaye', 'Ibrahima', 'Renforcement de l accompagnement pedagogique et des bourses.'),
(@election_ouverte_id, 'Fall',   'Aissatou', 'Modernisation du campus numerique et des espaces de travail.');

-- 2) Une élection FERMEE avec résultats publiés (pour tester l'écran de résultats)
INSERT INTO elections (titre, type_election, date_debut, date_fin, statut, cible_role, cible_ufr, cible_departement, cible_niveau, resultats_publies)
VALUES ('Election Test 2025 (archivee)', 'Test', NOW() - INTERVAL 60 DAY, NOW() - INTERVAL 30 DAY, 'FERMEE', 'TOUS', NULL, NULL, NULL, 1);

SET @election_fermee_id = LAST_INSERT_ID();

INSERT INTO candidats (election_id, nom, prenom, programme) VALUES
(@election_fermee_id, 'Sarr',  'Moussa', 'Programme A'),
(@election_fermee_id, 'Diallo','Awa',    'Programme B');

SET @candidat_a_id = (SELECT id FROM candidats WHERE election_id = @election_fermee_id ORDER BY id LIMIT 1);
SET @candidat_b_id = (SELECT id FROM candidats WHERE election_id = @election_fermee_id ORDER BY id DESC LIMIT 1);

INSERT INTO votes (election_id, candidat_id, utilisateur_id, date_vote) VALUES
(@election_fermee_id, @candidat_a_id, (SELECT id FROM users WHERE login = 'hady'),   NOW() - INTERVAL 35 DAY),
(@election_fermee_id, @candidat_a_id, (SELECT id FROM users WHERE login = 'nogaye'), NOW() - INTERVAL 35 DAY),
(@election_fermee_id, @candidat_b_id, (SELECT id FROM users WHERE login = 'sow'),    NOW() - INTERVAL 34 DAY);
