# Rush Hour – Java beadandó

## Projekt bemutatása

A Rush Hour egy Java 21-ben készült, konzolos logikai játék, amelyben egy NxM méretű táblán különböző járműveket kell mozgatni.

A játékos célja, hogy az **A jelű autót** a tábla jobb oldalán található kijárathoz juttassa. A játékban számítógépes ellenfél is szerepel, amely a **C jelű céljárművel** próbál kijutni a bal oldalon.

## Felhasznált technológiák

- Java 21
- Maven
- JUnit 5
- Mockito
- JaCoCo
- Checkstyle
- SQLite JDBC
- SLF4J és Logback
- Jackson JSON-feldolgozó
- Java DOM XML-feldolgozó

## A program funkciói

- NxM méretű játéktábla kezelése
- Vízszintes és függőleges járművek
- Szabályos és szabálytalan lépések ellenőrzése
- Játékos és számítógépes ellenfél
- Győzelem és játéklezárás kezelése
- Pályák betöltése TXT, XML és JSON formátumból
- Eredmények tárolása SQLite-adatbázisban
- Ranglista megjelenítése
- Naplózás Logback segítségével
- Automatikus tesztelés JUnit és Mockito használatával

## A játék indítása

A projekt gyökérkönyvtárában a következő paranccsal fordítható:

```bash
mvn clean package
```

A futtatható, függőségeket is tartalmazó JAR indítása:

```bash
java -jar target/rush-hour-1.0-SNAPSHOT-jar-with-dependencies.jar
```

A program elindulása után meg kell adni a játékos nevét, majd ki kell választani a pálya formátumát.

## Pályaválasztás

A program három formátumot támogat:

| Választás | Formátum | Pályafájl |
|---|---|---|
| 1 | TXT | `level1.txt` |
| 2 | XML | `level1.xml` |
| 3 | JSON | `level1.json` |

A pályafájlok a `src/main/resources/levels` mappában találhatók.

## Játékparancsok

| Parancs | Jelentés |
|---|---|
| `A 1` | Az A autó mozgatása egy mezővel jobbra |
| `A -1` | Az A autó mozgatása egy mezővel balra |
| `B 1` | A B jármű mozgatása egy mezővel lefelé |
| `B -1` | A B jármű mozgatása egy mezővel felfelé |
| `exit A` | Kilépés a pályáról, ha az A autó a kijáratnál van |
| `scores` | Ranglista megjelenítése |
| `quit` | Játék bezárása |

A járművek csak a saját tájolásuknak megfelelő irányban mozoghatnak, és nem haladhatnak át más járműveken.

## Adatbázis és naplózás

A program SQLite-adatbázisban tárolja a játékosok győzelmeinek számát.

Az adatbázisfájl neve: `rushhour.db`.

A naplózás a Logback könyvtárral történik. A naplófájl helye: `logs/rushhour.log`.

Ezek a futás közben létrejövő fájlok nem kerülnek feltöltésre a GitHubra.

## Tesztelés

A projekt JUnit 5 és Mockito segítségével tesztelhető.

A teljes tesztcsomag futtatása:

```bash
mvn clean verify
```

A projektben **63 automatikus teszt** található.

A JaCoCo által mért eredmények:

- Utasításlefedettség: **82%**
- Elágazáslefedettség: **70%**

A lefedettségi jelentés helye:

`target/site/jacoco/index.html`

## Kódminőség

A projekt Checkstyle-ellenőrzést is tartalmaz.

Futtatása:

```bash
mvn checkstyle:check
```

A szabályokat a projekt gyökerében található `checkstyle.xml` fájl határozza meg.

## Projekt felépítése

```text
rush-hour/
├── pom.xml
├── README.md
├── checkstyle.xml
├── .gitignore
└── src/
    ├── main/
    │   ├── java/hu/nye/progtech/rushhour/
    │   │   ├── Main.java
    │   │   ├── model/
    │   │   ├── service/
    │   │   ├── persistence/
    │   │   └── ui/
    │   └── resources/
    │       ├── levels/
    │       │   ├── level1.txt
    │       │   ├── level1.xml
    │       │   └── level1.json
    │       └── logback.xml
    └── test/
        └── java/hu/nye/progtech/rushhour/
```

## Összegzés

A projekt célja a Java objektumorientált programozás, a fájlkezelés, az adatbázis-kezelés, az automatikus tesztelés és a Maven-alapú projektépítés gyakorlati alkalmazása.