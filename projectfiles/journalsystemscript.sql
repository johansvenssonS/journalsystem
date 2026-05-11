-- =====================================================
-- Journalsystem - Korrigerat SQL-script
-- =====================================================

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

CREATE SCHEMA IF NOT EXISTS `journalsystem` DEFAULT CHARACTER SET utf8mb4;
USE `journalsystem`;

-- =====================================================
-- ORGANISATIONSLAGER
-- =====================================================

-- Region är toppen av hierarkin
CREATE TABLE IF NOT EXISTS `Region` (
  `id`                  INT           NOT NULL AUTO_INCREMENT,
  `namn`                VARCHAR(45)   NOT NULL,
  `organisationsnummer` VARCHAR(13)   NOT NULL,  -- format: 802400-2413
  PRIMARY KEY (`id`),
  UNIQUE INDEX `organisationsnummer_UNIQUE` (`organisationsnummer` ASC)
) ENGINE = InnoDB;


-- Sjukhus tillhör en Region (många sjukhus per region)
CREATE TABLE IF NOT EXISTS `Sjukhus` (
  `id`        INT           NOT NULL AUTO_INCREMENT,
  `namn`      VARCHAR(45)   NOT NULL,
  `adress`    VARCHAR(100)  NOT NULL,
  `telefon`   VARCHAR(45)   NOT NULL,
  `region_id` INT           NOT NULL,  -- FK -> Region
  PRIMARY KEY (`id`),
  INDEX `fk_Sjukhus_region_idx` (`region_id` ASC),
  CONSTRAINT `fk_Sjukhus_Region`
    FOREIGN KEY (`region_id`)
    REFERENCES `Region` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
) ENGINE = InnoDB;


-- Specialisering är en uppslagstabell (kardiologi, neurologi osv)
CREATE TABLE IF NOT EXISTS `Specialisering` (
  `id`   INT         NOT NULL AUTO_INCREMENT,
  `namn` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `namn_UNIQUE` (`namn` ASC)
) ENGINE = InnoDB;


-- Avdelning tillhör ett Sjukhus och har en Specialisering
-- En avdelning per sjukhus kan ha samma våningsplan -> ingen UNIQUE på våningsplan
CREATE TABLE IF NOT EXISTS `Avdelning` (
  `id`                INT         NOT NULL AUTO_INCREMENT,
  `namn`              VARCHAR(45) NOT NULL,
  `våningsplan`       INT         NULL,
  `sjukhus_id`        INT         NOT NULL,  -- FK -> Sjukhus
  `specialisering_id` INT         NOT NULL,  -- FK -> Specialisering
  PRIMARY KEY (`id`),
  INDEX `fk_Avdelning_Sjukhus_idx`        (`sjukhus_id` ASC),
  INDEX `fk_Avdelning_Specialisering_idx` (`specialisering_id` ASC),
  CONSTRAINT `fk_Avdelning_Sjukhus`
    FOREIGN KEY (`sjukhus_id`)
    REFERENCES `Sjukhus` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Avdelning_Specialisering`
    FOREIGN KEY (`specialisering_id`)
    REFERENCES `Specialisering` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
) ENGINE = InnoDB;


-- =====================================================
-- PERSONAL
-- =====================================================

-- Grunddata för anställd (personnummer, namn)
CREATE TABLE IF NOT EXISTS `Personal` (
  `id`            INT         NOT NULL AUTO_INCREMENT,
  `personnummer`  VARCHAR(13) NOT NULL,  -- format: YYYYMMDD-XXXX
  `förnamn`       VARCHAR(45) NOT NULL,
  `efternamn`     VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `personnummer_UNIQUE` (`personnummer` ASC)
) ENGINE = InnoDB;


-- Kontaktdata för personal
CREATE TABLE IF NOT EXISTS `PersonalPdata` (
  `personal_id` INT          NOT NULL,  -- FK -> Personal (1:1)
  `email`       VARCHAR(45)  NULL,
  `telefon`     VARCHAR(45)  NULL,
  `adress`      VARCHAR(100) NULL,
  PRIMARY KEY (`personal_id`),
  UNIQUE INDEX `email_UNIQUE`   (`email` ASC),
  UNIQUE INDEX `telefon_UNIQUE` (`telefon` ASC),
  CONSTRAINT `fk_PersonalPdata_Personal`
    FOREIGN KEY (`personal_id`)
    REFERENCES `Personal` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
) ENGINE = InnoDB;


-- Yrkesroller (läkare, sjuksköterska, usk osv) - uppslagstabell
CREATE TABLE IF NOT EXISTS `Roll` (
  `id`        INT         NOT NULL AUTO_INCREMENT,
  `yrkesroll` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `yrkesroll_UNIQUE` (`yrkesroll` ASC)
) ENGINE = InnoDB;


-- Arbetsdata för personal: roll, legitimation, vilken avdelning de tillhör
CREATE TABLE IF NOT EXISTS `PersonalAdata` (
  `personal_id`    INT         NOT NULL,  -- FK -> Personal (1:1)
  `roll_id`        INT         NOT NULL,  -- FK -> Roll
  `legitimation`   VARCHAR(45) NOT NULL,
  `anställd_datum` DATE        NOT NULL,
  `avdelning_id`   INT         NOT NULL,  -- FK -> Avdelning
  PRIMARY KEY (`personal_id`),
  UNIQUE INDEX `legitimation_UNIQUE` (`legitimation` ASC),
  INDEX `fk_PersonalAdata_Roll_idx`      (`roll_id` ASC),
  INDEX `fk_PersonalAdata_Avdelning_idx` (`avdelning_id` ASC),
  CONSTRAINT `fk_PersonalAdata_Personal`
    FOREIGN KEY (`personal_id`)
    REFERENCES `Personal` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_PersonalAdata_Roll`
    FOREIGN KEY (`roll_id`)
    REFERENCES `Roll` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_PersonalAdata_Avdelning`
    FOREIGN KEY (`avdelning_id`)
    REFERENCES `Avdelning` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
) ENGINE = InnoDB;


-- =====================================================
-- PATIENT
-- =====================================================

-- Grunddata för patient
CREATE TABLE IF NOT EXISTS `Patient` (
  `id`           INT         NOT NULL AUTO_INCREMENT,
  `personnummer` VARCHAR(13) NOT NULL,  -- format: YYYYMMDD-XXXX
  `förnamn`      VARCHAR(45) NOT NULL,
  `efternamn`    VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `personnummer_UNIQUE` (`personnummer` ASC)
) ENGINE = InnoDB;


-- Kontaktdata för patient (1:1 med Patient)
CREATE TABLE IF NOT EXISTS `PatientPdata` (
  `patient_id`    INT          NOT NULL,  -- FK -> Patient (1:1)
  `telefonnummer` VARCHAR(45)  NULL,
  `email`         VARCHAR(45)  NULL,
  `adress`        VARCHAR(100) NULL,
  `nödkontakt_namn`    VARCHAR(100) NULL,  -- uppdelat för tydlighet
  `nödkontakt_telefon` VARCHAR(45)  NULL,
  PRIMARY KEY (`patient_id`),
  CONSTRAINT `fk_PatientPdata_Patient`
    FOREIGN KEY (`patient_id`)
    REFERENCES `Patient` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
) ENGINE = InnoDB;


-- Medicinsk grunddata för patient (1:1 med Patient)
-- allergier bör på sikt bli en egen tabell, men VARCHAR räcker för nu
CREATE TABLE IF NOT EXISTS `PatientMdata` (
  `patient_id`   INT         NOT NULL,  -- FK -> Patient (1:1)
  `allergier`    VARCHAR(255) NULL,
  `blodgrupp`    VARCHAR(45) NULL,
  `skapad_datum` TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`patient_id`),
  CONSTRAINT `fk_PatientMdata_Patient`
    FOREIGN KEY (`patient_id`)
    REFERENCES `Patient` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
) ENGINE = InnoDB;


-- =====================================================
-- VÅRDRELATIONER
-- =====================================================

-- Vårdkontakt är bryggan mellan Patient, Avdelning och ansvarig Personal
-- En patient kan ha MÅNGA vårdkontakter över tid (1:N Patient -> Vårdkontakt)
-- En avdelning har MÅNGA vårdkontakter          (1:N Avdelning -> Vårdkontakt)
CREATE TABLE IF NOT EXISTS `Vårdkontakt` (
  `id`            INT  NOT NULL AUTO_INCREMENT,
  `patient_id`    INT  NOT NULL,  -- FK -> Patient
  `avdelning_id`  INT  NOT NULL,  -- FK -> Avdelning
  `personal_id`   INT  NOT NULL,  -- FK -> Personal (ansvarig)
  PRIMARY KEY (`id`),
  INDEX `fk_Vårdkontakt_Patient_idx`   (`patient_id` ASC),
  INDEX `fk_Vårdkontakt_Avdelning_idx` (`avdelning_id` ASC),
  INDEX `fk_Vårdkontakt_Personal_idx`  (`personal_id` ASC),
  CONSTRAINT `fk_Vårdkontakt_Patient`
    FOREIGN KEY (`patient_id`)
    REFERENCES `Patient` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Vårdkontakt_Avdelning`
    FOREIGN KEY (`avdelning_id`)
    REFERENCES `Avdelning` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Vårdkontakt_Personal`
    FOREIGN KEY (`personal_id`)
    REFERENCES `Personal` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
) ENGINE = InnoDB;


-- Vårdorsak är 1:1 med Vårdkontakt - innehåller orsak, datum och status
CREATE TABLE IF NOT EXISTS `Vårdorsak` (
  `vårdkontakt_id` INT          NOT NULL,  -- FK -> Vårdkontakt (1:1)
  `orsak`          VARCHAR(255) NOT NULL,
  `inskriv_datum`  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `utskriv_datum`  TIMESTAMP    NULL,
  `status`         ENUM('INSKRIVEN', 'UTSKRIVEN', 'PLANERAD') NOT NULL DEFAULT 'PLANERAD',
  PRIMARY KEY (`vårdkontakt_id`),
  CONSTRAINT `fk_Vårdorsak_Vårdkontakt`
    FOREIGN KEY (`vårdkontakt_id`)
    REFERENCES `Vårdkontakt` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
) ENGINE = InnoDB;


-- =====================================================
-- JOURNAL
-- =====================================================

-- En vårdkontakt kan ha MÅNGA journalposter (1:N)
-- skapad_av pekar på Personal som skrev posten
CREATE TABLE IF NOT EXISTS `Journalpost` (
  `id`               INT       NOT NULL AUTO_INCREMENT,
  `vårdkontakt_id`   INT       NOT NULL,  -- FK -> Vårdkontakt
  `skapad_av`        INT       NOT NULL,  -- FK -> Personal
  `datum`            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `typ`              ENUM('ANTECKNING', 'OPERATION', 'UNDERSÖKNING') NOT NULL DEFAULT 'ANTECKNING',
  `innehåll`         TEXT      NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_Journalpost_Vårdkontakt_idx` (`vårdkontakt_id` ASC),
  INDEX `fk_Journalpost_Personal_idx`    (`skapad_av` ASC),
  CONSTRAINT `fk_Journalpost_Vårdkontakt`
    FOREIGN KEY (`vårdkontakt_id`)
    REFERENCES `Vårdkontakt` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Journalpost_Personal`
    FOREIGN KEY (`skapad_av`)
    REFERENCES `Personal` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
) ENGINE = InnoDB;


-- En journalpost kan ha MÅNGA diagnoser (1:N)
-- ställd_av är den Personal som ställde diagnosen (kan skilja från journalpostens skapad_av)
CREATE TABLE IF NOT EXISTS `Diagnos` (
  `id`             INT         NOT NULL AUTO_INCREMENT,
  `journalpost_id` INT         NOT NULL,  -- FK -> Journalpost
  `ställd_av`      INT         NOT NULL,  -- FK -> Personal
  `icd10_kod`      VARCHAR(10) NOT NULL,
  `diagnos_namn`   VARCHAR(100) NULL,
  `beskrivning`    TEXT        NULL,
  `datum`          DATE        NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_Diagnos_Journalpost_idx` (`journalpost_id` ASC),
  INDEX `fk_Diagnos_Personal_idx`    (`ställd_av` ASC),
  CONSTRAINT `fk_Diagnos_Journalpost`
    FOREIGN KEY (`journalpost_id`)
    REFERENCES `Journalpost` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Diagnos_Personal`
    FOREIGN KEY (`ställd_av`)
    REFERENCES `Personal` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
) ENGINE = InnoDB;


-- En journalpost kan ha MÅNGA åtgärder (1:N)
CREATE TABLE IF NOT EXISTS `Åtgärd` (
  `id`             INT          NOT NULL AUTO_INCREMENT,
  `journalpost_id` INT          NOT NULL,  -- FK -> Journalpost
  `åtgärd_av`      INT          NOT NULL,  -- FK -> Personal
  `beskrivning`    VARCHAR(255) NOT NULL,
  `utförd_datum`   DATE         NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_Åtgärd_Journalpost_idx` (`journalpost_id` ASC),
  INDEX `fk_Åtgärd_Personal_idx`    (`åtgärd_av` ASC),
  CONSTRAINT `fk_Åtgärd_Journalpost`
    FOREIGN KEY (`journalpost_id`)
    REFERENCES `Journalpost` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Åtgärd_Personal`
    FOREIGN KEY (`åtgärd_av`)
    REFERENCES `Personal` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
) ENGINE = InnoDB;


-- =====================================================
-- SÄKERHET & SPÅRBARHET
-- =====================================================

-- AuditLog loggar varje händelse - inga FK:er här med flit
-- eftersom loggar aldrig ska blockeras av att personal/patient raderas
-- personal_id och patient_id är INT NULL för att matcha sina källtabeller
CREATE TABLE IF NOT EXISTS `AuditLog` (
  `id`          INT          NOT NULL AUTO_INCREMENT,
  `personal_id` INT          NULL,      -- vem utförde händelsen
  `patient_id`  INT          NULL,      -- vilken patient gällde det
  `händelse`    ENUM('LÄST', 'SKAPAT', 'ÄNDRAT', 'RADERAT') NOT NULL,
  `entitet`     VARCHAR(45)  NOT NULL,  -- t.ex. "Journalpost", "Diagnos"
  `entitet_id`  INT          NULL,      -- id på den berörda raden
  `tidpunkt`    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ip_adress`   VARCHAR(45)  NULL,
  PRIMARY KEY (`id`)
  -- Inga FK:er på AuditLog - loggar ska bevaras även om personal/patient tas bort
) ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
