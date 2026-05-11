-- =====================================================
-- Journalsystem - Mock data
-- Ordning: parent-tabeller före child-tabeller
-- =====================================================

USE `journalsystem`;

-- =====================================================
-- 1. REGION (ingen FK, kan insertas först)
-- =====================================================
INSERT INTO `Region` (`namn`, `organisationsnummer`) VALUES
  ('Region Skåne',       '232100-0255'),
  ('Region Stockholm',   '232100-0016'),
  ('Region Västra Götaland', '232100-0131');


-- =====================================================
-- 2. SJUKHUS (FK -> Region)
-- =====================================================
INSERT INTO `Sjukhus` (`namn`, `adress`, `telefon`, `region_id`) VALUES
  ('Skånes universitetssjukhus', 'Inga Marie Nilssons gata 32, Malmö', '040-33 10 00', 1),
  ('Helsingborgs lasarett',       'Södra Stenbocksgatan 2, Helsingborg', '042-406 10 00', 1),
  ('Karolinska universitetssjukhuset', 'Solnavägen 1, Solna',            '08-517 700 00', 2),
  ('Sahlgrenska universitetssjukhuset', 'Blå Stråket 1, Göteborg',       '031-342 10 00', 3);


-- =====================================================
-- 3. SPECIALISERING (ingen FK)
-- =====================================================
INSERT INTO `Specialisering` (`namn`) VALUES
  ('Kardiologi'),
  ('Neurologi'),
  ('Ortopedi'),
  ('Akutmedicin'),
  ('Psykiatri');


-- =====================================================
-- 4. AVDELNING (FK -> Sjukhus, Specialisering)
-- =====================================================
INSERT INTO `Avdelning` (`namn`, `våningsplan`, `sjukhus_id`, `specialisering_id`) VALUES
  ('Hjärtavdelningen',    3, 1, 1),  -- SUS, Kardiologi
  ('Neurologen',          4, 1, 2),  -- SUS, Neurologi
  ('Ortopeden',           2, 2, 3),  -- Helsingborg, Ortopedi
  ('Akutmottagningen',    1, 2, 4),  -- Helsingborg, Akut
  ('Kardiologiska klin.', 5, 3, 1),  -- Karolinska, Kardiologi
  ('Psykiatrimottagning', 2, 4, 5);  -- Sahlgrenska, Psykiatri


-- =====================================================
-- 5. ROLL (ingen FK)
-- =====================================================
INSERT INTO `Roll` (`yrkesroll`) VALUES
  ('Läkare'),
  ('Sjuksköterska'),
  ('Undersköterska'),
  ('Specialist'),
  ('Receptionist');


-- =====================================================
-- 6. PERSONAL (ingen FK)
-- =====================================================
INSERT INTO `Personal` (`personnummer`, `förnamn`, `efternamn`) VALUES
  ('197203154716', 'Anna',    'Lindström'),   -- 1, läkare
  ('198511092837', 'Erik',    'Svensson'),    -- 2, läkare
  ('199001234567', 'Maria',   'Johansson'),   -- 3, sjuksköterska
  ('198706178901', 'Lars',    'Petersson'),   -- 4, sjuksköterska
  ('196512303456', 'Karin',   'Nilsson'),     -- 5, undersköterska
  ('200002145678', 'Johan',   'Bergström'),   -- 6, specialist
  ('197809221234', 'Sofia',   'Andersson'),   -- 7, receptionist
  ('198304056789', 'Magnus',  'Karlsson');    -- 8, läkare


-- =====================================================
-- 7. PERSONALPDATA (FK -> Personal, 1:1)
-- =====================================================
INSERT INTO `PersonalPdata` (`personal_id`, `email`, `telefon`, `adress`) VALUES
  (1, 'anna.lindstrom@sus.se',      '040-33 11 01', 'Storgatan 12, Malmö'),
  (2, 'erik.svensson@sus.se',       '040-33 11 02', 'Parkvägen 5, Malmö'),
  (3, 'maria.johansson@sus.se',     '040-33 11 03', 'Kyrkogatan 8, Lund'),
  (4, 'lars.petersson@hlasarett.se','042-406 11 04', 'Norra Storg. 3, Helsingborg'),
  (5, 'karin.nilsson@hlasarett.se', '042-406 11 05', 'Södra Vägen 7, Helsingborg'),
  (6, 'johan.bergstrom@sus.se',     '040-33 11 06', 'Allégatan 2, Malmö'),
  (7, 'sofia.andersson@hlasarett.se','042-406 11 07','Strandvägen 14, Helsingborg'),
  (8, 'magnus.karlsson@karolinska.se','08-517 71 08','Solnavägen 3, Solna');


-- =====================================================
-- 8. PERSONALADATA (FK -> Personal, Roll, Avdelning, 1:1)
-- =====================================================
INSERT INTO `PersonalAdata` (`personal_id`, `roll_id`, `legitimation`, `anställd_datum`, `avdelning_id`) VALUES
  (1, 1, 'LÄK-2001-4521', '2001-09-01', 1),  -- Anna, Läkare, Hjärtavd SUS
  (2, 1, 'LÄK-2010-8834', '2010-03-15', 2),  -- Erik, Läkare, Neurologen SUS
  (3, 2, 'SSK-2015-2210', '2015-06-01', 1),  -- Maria, Sjuksköterska, Hjärtavd
  (4, 2, 'SSK-2012-3349', '2012-08-20', 3),  -- Lars, Sjuksköterska, Ortopeden
  (5, 3, 'USK-2018-5567', '2018-01-10', 4),  -- Karin, Usk, Akuten
  (6, 4, 'SPE-2005-7723', '2005-04-01', 1),  -- Johan, Specialist, Hjärtavd
  (7, 5, 'REC-2020-1198', '2020-02-01', 4),  -- Sofia, Receptionist, Akuten
  (8, 1, 'LÄK-2008-6612', '2008-11-15', 5); -- Magnus, Läkare, Karolinska


-- =====================================================
-- 9. PATIENT (ingen FK)
-- =====================================================
INSERT INTO `Patient` (`personnummer`, `förnamn`, `efternamn`) VALUES
  ('194506127890', 'Sven',    'Gustafsson'),   -- 1
  ('196203284567', 'Britta',  'Holm'),         -- 2
  ('197811152345', 'Mikael',  'Strand'),       -- 3
  ('195909301234', 'Gunnel',  'Ekström'),      -- 4
  ('200105176789', 'Lina',    'Persson'),      -- 5
  ('198407228901', 'Anders',  'Björk');        -- 6


-- =====================================================
-- 10. PATIENTPDATA (FK -> Patient, 1:1)
-- =====================================================
INSERT INTO `PatientPdata` (`patient_id`, `telefonnummer`, `email`, `adress`, `nödkontakt_namn`, `nödkontakt_telefon`) VALUES
  (1, '070-123 45 67', 'sven.gustafsson@gmail.com',  'Hantverkargatan 4, Malmö',    'Eva Gustafsson',  '070-234 56 78'),
  (2, '073-234 56 78', 'britta.holm@hotmail.com',    'Rosengatan 12, Lund',         'Per Holm',        '073-345 67 89'),
  (3, '076-345 67 89', 'mikael.strand@gmail.com',    'Industrigatan 6, Helsingborg', 'Anna Strand',    '076-456 78 90'),
  (4, '070-456 78 90', NULL,                          'Kungsgatan 22, Malmö',        'Rolf Ekström',   '070-567 89 01'),
  (5, '073-567 89 01', 'lina.persson@gmail.com',     'Studentgatan 3, Lund',        'Sara Persson',    '073-678 90 12'),
  (6, '076-678 90 12', 'anders.bjork@outlook.com',   'Havsgatan 9, Vellinge',       'Maja Björk',      '076-789 01 23');


-- =====================================================
-- 11. PATIENTMDATA (FK -> Patient, 1:1)
-- =====================================================
INSERT INTO `PatientMdata` (`patient_id`, `allergier`, `blodgrupp`) VALUES
  (1, 'Penicillin',            'A+'),
  (2, NULL,                    'O-'),
  (3, 'Sulfa, Aspirin',        'B+'),
  (4, 'Latex',                 'AB+'),
  (5, NULL,                    'A-'),
  (6, 'Nötter',                'O+');


-- =====================================================
-- 12. VÅRDKONTAKT (FK -> Patient, Avdelning, Personal)
-- En patient kan ha flera vårdkontakter
-- =====================================================
INSERT INTO `Vårdkontakt` (`patient_id`, `avdelning_id`, `personal_id`) VALUES
  (1, 1, 1),  -- Sven på Hjärtavd, ansvarig Anna
  (2, 2, 2),  -- Britta på Neurologen, ansvarig Erik
  (3, 3, 4),  -- Mikael på Ortopeden, ansvarig Lars
  (4, 4, 5),  -- Gunnel på Akuten, ansvarig Karin
  (5, 1, 1),  -- Lina på Hjärtavd, ansvarig Anna
  (1, 4, 5),  -- Sven tidigare på Akuten (tidigare vårdkontakt)
  (6, 5, 8);  -- Anders på Karolinska, ansvarig Magnus


-- =====================================================
-- 13. VÅRDORSAK (FK -> Vårdkontakt, 1:1)
-- =====================================================
INSERT INTO `Vårdorsak` (`vårdkontakt_id`, `orsak`, `inskriv_datum`, `utskriv_datum`, `status`) VALUES
  (1, 'Bröstsmärtor och andfåddhet',         '2025-11-10 08:30:00', NULL,                  'INSKRIVEN'),
  (2, 'Återkommande migrän med aura',        '2025-11-08 13:00:00', NULL,                  'INSKRIVEN'),
  (3, 'Fraktur höger knä efter fall',        '2025-10-20 10:15:00', '2025-11-01 11:00:00', 'UTSKRIVEN'),
  (4, 'Akut buksmärta',                      '2025-11-09 22:45:00', '2025-11-10 06:00:00', 'UTSKRIVEN'),
  (5, 'Uppföljning hjärtarytmi',             '2025-11-12 09:00:00', NULL,                  'PLANERAD'),
  (6, 'Svimningsattack på allmän plats',     '2025-09-15 14:20:00', '2025-09-15 20:00:00', 'UTSKRIVEN'),
  (7, 'Utredning kronisk trötthet och yrsel','2025-11-05 10:00:00', NULL,                  'INSKRIVEN');


-- =====================================================
-- 14. JOURNALPOST (FK -> Vårdkontakt, Personal)
-- En vårdkontakt kan ha många journalposter
-- =====================================================
INSERT INTO `Journalpost` (`vårdkontakt_id`, `skapad_av`, `datum`, `typ`, `innehåll`) VALUES
  (1, 1, '2025-11-10 09:00:00', 'ANTECKNING',   'Patient inkommer med bröstsmärtor. EKG taget, avvaktar svar.'),
  (1, 1, '2025-11-10 14:00:00', 'UNDERSÖKNING', 'Ekokardiografi utförd. Lindrig aortastenos påvisad.'),
  (2, 2, '2025-11-08 13:30:00', 'ANTECKNING',   'Patient beskriver pulserande huvudvärk med synstörningar.'),
  (2, 2, '2025-11-09 10:00:00', 'UNDERSÖKNING', 'MR hjärna utförd. Inga strukturella förändringar.'),
  (3, 4, '2025-10-20 11:00:00', 'ANTECKNING',   'Patient inkom med smärta och svullnad höger knä efter fall.'),
  (3, 4, '2025-10-21 08:00:00', 'OPERATION',    'Artroskopi höger knä utförd utan komplikationer.'),
  (4, 5, '2025-11-09 23:00:00', 'ANTECKNING',   'Patient söker med akuta buksmärtor. Smärtlindring given.'),
  (7, 8, '2025-11-05 10:30:00', 'ANTECKNING',   'Patient beskriver trötthet och yrsel sedan 3 månader.'),
  (7, 8, '2025-11-06 09:00:00', 'UNDERSÖKNING', 'Blodprover tagna. Järnbrist påvisad.');


-- =====================================================
-- 15. DIAGNOS (FK -> Journalpost, Personal)
-- En journalpost kan ha många diagnoser
-- =====================================================
INSERT INTO `Diagnos` (`journalpost_id`, `ställd_av`, `icd10_kod`, `diagnos_namn`, `beskrivning`, `datum`) VALUES
  (2, 1, 'I35.0', 'Aortastenos',          'Lindrig aortastenos påvisad vid ekokardiografi',   '2025-11-10'),
  (4, 2, 'G43.1', 'Migrän med aura',      'Återkommande migrän med synstörningar',            '2025-11-09'),
  (5, 4, 'S83.2', 'Knäledsluxation',      'Fraktur och luxation höger knäled efter trauma',   '2025-10-20'),
  (7, 5, 'R10.0', 'Akut buksmärta',       'Diffus buksmärta, trolig gastrit',                 '2025-11-09'),
  (9, 8, 'D50.9', 'Järnbristanemi',       'Järnbrist påvisad i blodprov, Hb 98',              '2025-11-06');


-- =====================================================
-- 16. ÅTGÄRD (FK -> Journalpost, Personal)
-- En journalpost kan ha många åtgärder
-- =====================================================
INSERT INTO `Åtgärd` (`journalpost_id`, `åtgärd_av`, `beskrivning`, `utförd_datum`) VALUES
  (1, 1, 'EKG taget och analyserat',                        '2025-11-10'),
  (2, 6, 'Ekokardiografi utförd av specialist',             '2025-11-10'),
  (3, 2, 'Neurologisk undersökning utförd',                 '2025-11-08'),
  (4, 2, 'MR hjärna beställd och utförd',                  '2025-11-09'),
  (5, 4, 'Röntgen höger knä',                              '2025-10-20'),
  (6, 4, 'Artroskopi höger knä, menisksutur utförd',       '2025-10-21'),
  (7, 5, 'Smärtlindring given, morfin 5mg iv',             '2025-11-09'),
  (8, 8, 'Blodprover beställda: Hb, ferritin, B12, folat', '2025-11-05'),
  (9, 8, 'Järntillskott ordinerat, Duroferon 100mg x2',    '2025-11-06');


-- =====================================================
-- 17. AUDITLOG (inga FK:er - loggar allt)
-- =====================================================
INSERT INTO `AuditLog` (`personal_id`, `patient_id`, `händelse`, `entitet`, `entitet_id`, `ip_adress`) VALUES
  (1, 1, 'SKAPAT',  'Journalpost', 1, '192.168.1.10'),
  (1, 1, 'SKAPAT',  'Journalpost', 2, '192.168.1.10'),
  (1, 1, 'SKAPAT',  'Diagnos',     1, '192.168.1.10'),
  (2, 2, 'SKAPAT',  'Journalpost', 3, '192.168.1.11'),
  (2, 2, 'SKAPAT',  'Journalpost', 4, '192.168.1.11'),
  (4, 3, 'SKAPAT',  'Journalpost', 5, '192.168.1.12'),
  (4, 3, 'SKAPAT',  'Journalpost', 6, '192.168.1.12'),
  (4, 3, 'SKAPAT',  'Diagnos',     3, '192.168.1.12'),
  (3, 1, 'LÄST',    'Journalpost', 1, '192.168.1.13'),  -- Maria läste Svens journal
  (7, 4, 'LÄST',    'Patient',     4, '192.168.1.14'),  -- Sofia (receptionist) läste Gunnels basdata
  (8, 6, 'SKAPAT',  'Journalpost', 8, '192.168.1.15'),
  (8, 6, 'SKAPAT',  'Diagnos',     5, '192.168.1.15'),
  (1, 5, 'SKAPAT',  'Vårdkontakt', 5, '192.168.1.10');

