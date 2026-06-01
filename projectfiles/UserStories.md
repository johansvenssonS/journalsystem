# User Stories — Journalsystem

Stories är grupperade per roll och prioriterade inom varje grupp.
Status: `[ ]` ej påbörjad &nbsp;|&nbsp; `[~]` pågående &nbsp;|&nbsp; `[x]` klar

---


## ️ Aveldning
- [ ] **US-1** — En avdelning kopplas till Patient,läkare osv. .
- [ ] **US-2** — En Läkare/sjuksköterska/undersköterska ska bara se info kopplat till patienter på deras avdelning.
## 👨‍⚕️ Läkare

> Har bred åtkomst — kan läsa hela journalen, ställa diagnoser och ordinera åtgärder.

- [ ] **US-10** — Som läkare vill jag kunna söka upp en patient via personnummer så att jag snabbt hittar rätt person.
- [ ] **US-11** — Som läkare vill jag kunna se en patients alla vårdkontakter och tillhörande journalposter så att jag får en samlad bild av patientens vårdhistorik.
- [ ] **US-12** — Som läkare vill jag kunna skapa en ny vårdkontakt för en patient på min avdelning så att vården kan dokumenteras.
- [ ] **US-13** — Som läkare vill jag kunna skapa en journalpost av typen ANTECKNING, UNDERSÖKNING eller OPERATION kopplad till en vårdkontakt.
- [ ] **US-14** — Som läkare vill jag kunna ställa en diagnos med ICD-10-kod kopplad till en journalpost så att diagnosen är korrekt dokumenterad.
- [ ] **US-15** — Som läkare vill jag kunna registrera en åtgärd kopplad till en journalpost så att det som gjorts är spårbart.
- [ ] **US-16** — Som läkare vill jag kunna se en patients allergier och blodgrupp direkt när jag öppnar deras profil så att jag inte missar kritisk information.
- [ ] **US-17** — Som läkare vill jag kunna skriva ut en patient (sätta status UTSKRIVEN på vårdkontakten) med ett utskrivningsdatum.

---

## 🩺 Sjuksköterska

> Kan läsa journaler och dokumentera omvårdnad, men inte ställa diagnoser.

- [ ] **US-20** — Som sjuksköterska vill jag kunna se alla patienter som är inskrivna på min avdelning så att jag har överblick över mitt ansvarsområde.
- [ ] **US-21** — Som sjuksköterska vill jag kunna läsa en patients journalposter och diagnoser så att jag kan ge rätt vård.
- [ ] **US-22** — Som sjuksköterska vill jag kunna skapa en journalpost av typen ANTECKNING så att omvårdnadsåtgärder dokumenteras.
- [ ] **US-23** — Som sjuksköterska vill jag kunna registrera en åtgärd jag utfört (t.ex. medicingivning) kopplad till en journalpost.
- [ ] **US-24** — Som sjuksköterska ska jag *inte* kunna ställa diagnoser — systemet ska neka detta med ett tydligt felmeddelande.

---

## 🧑‍⚕️ Undersköterska

> Ser begränsad information — tillräckligt för att utföra praktiska omvårdnadsuppgifter.

- [ ] **US-30** — Som undersköterska vill jag kunna se vilka patienter som är inskrivna på min avdelning med namn och rumsnummer.
- [ ] **US-31** — Som undersköterska ska jag *inte* kunna se diagnoser eller journalanteckningar — systemet ska neka åtkomst.

---
## Patient 

- [ ] **US-32** — Som patient vill jag se mina journalposter, vårdkontaker, diagnoser etc.
- [ ] **US-33** — Som patient vill jag se mina uppgifter användarnamn,email,adress
- [ ] **US-34** — Som patient vill jag kunna ändra mina uppgifter användarnamn,email,adress
- [ ] **US-35** — Som patient vill jag kunna se mina bokningar. 

## 🗂️ Receptionist / Vårdadministratör

> Hanterar bokningar och kontaktinformation, ser ingen medicinsk data.

- [ ] **US-40** — Som receptionist vill jag kunna registrera en ny patient i systemet med namn, personnummer och kontaktuppgifter.
- [ ] **US-41** — Som receptionist vill jag kunna söka upp en patient och se deras kontaktuppgifter och nödkontakt.
- [ ] **US-42** — Som receptionist ska jag *inte* kunna se journalposter, diagnoser eller medicinsk data — systemet ska neka åtkomst.
- [ ] **US-43** — Som receptionist vill jag kunna uppdatera en patients kontaktuppgifter (adress, telefon, email).
- [ ] **US-44** — Som receptionist vill jag kunna boka in patient till möte med läkare.

---

## 🔍 Audit & Säkerhet & Admin

> Krav som gäller systemet i stort, inte en specifik användarroll.

- [ ] **US-50** — Som systemet vill jag logga varje gång en journalpost läses eller ändras, med tidpunkt, användare.
- [ ] **US-51** — Som systemet vill jag neka åtkomst till patientdata om den inloggade personalen tillhör en annan avdelning och det inte finns en aktiv vårdkontakt som motiverar åtkomsten.
- [ ] **US-52** — Som systemet vill jag returnera HTTP 403 med ett beskrivande felmeddelande när en användare försöker utföra en handling de inte har behörighet till.
- [ ] **US-53** — Som administratör vill jag kunna se audit-loggen för en specifik patient — vem som öppnat journalen och när.

---

## 🔗 Externa integrationer (mockas)

> Integrationer mot externa system som apoteket. Implementeras med mockade endpoints.

- [ ] **US-60** — Som läkare vill jag kunna skriva ett recept kopplat till en patient och ett läkemedel så att apoteket kan hämta det.
- [ ] **US-61** — Som systemet vill jag exponera ett endpoint som ett apotekssystem kan anropa för att hämta aktiva recept för en given patient.
- [ ] **US-62** — Som systemet vill jag kunna skicka en remiss från en avdelning till en annan och ta emot svar på remissen.