# Journalsystem

Ett backend-system för hantering av patientjournaler inom sjukvården, byggt med Spring Boot.

Systemet modellerar en realistisk vårdhierarki — från region och sjukhus ner till avdelning, personal och patient — med fokus på dataintegritet, rollbaserad åtkomst och full spårbarhet via audit-logging.

## Tech stack

- **Java 21** med **Spring Boot**
- **Spring Web** — REST API
- **Spring Data JPA** — databasintegration
- **Spring Security** — autentisering och rollbaserad behörighet
- **PostgreSQL** (produktion) / **H2** (utveckling)
- **Flyway** — databasmigreringar

## Projektstruktur

```
journalsystem/
├── code/               # Spring Boot-applikationen
├── projectfiles/       # Projektdokumentation
│   ├── journalERD.png          # Entitets-relationsdiagram
│   ├── schema.sql              # DDL – skapar alla tabeller
│   └── mockdata.sql            # DML – testdata
└── README.md
```

## Kom igång

1. Kör `schema.sql` mot en MySQL-databas för att skapa schemat
2. Kör `mockdata.sql` för att populera med testdata
3. Starta applikationen via `code/`

---

*Under utveckling — se user stories för aktuell status.*