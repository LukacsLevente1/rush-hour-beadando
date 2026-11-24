# Rush Hour – Java beadandó

## Projekt bemutatása

A Rush Hour egy Java 21-ben készült, konzolos logikai játék,
amelyben egy NxM méretű táblán különböző járműveket kell mozgatni.

A játékos célja, hogy az **A jelű autót** a tábla jobb oldalán
található kijárathoz juttassa. A játékban számítógépes ellenfél
is szerepel, amely a **C jelű céljárművel** próbál kijutni
a bal oldalon.

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
- Pozitív és negatív irányú mozgatás
- Ütközések és pályahatárok ellenőrzése
- Játékos és számítógépes ellenfél
- Győzelem és játéklezárás kezelése
- Pályák betöltése TXT, XML és JSON formátumból
- Egyéni TXT-pályafájl betöltése
- Aktuális játéktábla mentése TXT-fájlba
- Mentett játéktábla visszatöltése
- Koordináta- és irányalapú mozgatás
- Kisbetűs járműazonosítók elfogadása
- Eredmények tárolása SQLite-adatbázisban
- Rendezett ranglista megjelenítése
- Automatikus naplózás és naplóarchiválás
- Automatikus Checkstyle-ellenőrzés
- Automatikus tesztelés JUnit és Mockito használatával

## A játék indítása

A futtatáshoz Java 21 és Maven szükséges.

A projekt gyökérkönyvtárában a következő paranccsal
fordítható és csomagolható:

```bash
mvn clean package
```

A futtatható, függőségeket is tartalmazó JAR indítása:

```bash
java -jar target/rush-hour-1.0-SNAPSHOT-jar-with-dependencies.jar
```

A program elindulása után meg kell adni a játékos nevét,
majd ki kell választani a pálya formátumát.

## Pályaválasztás

| Választás | Formátum | Pályafájl |
|-----------|----------|-----------|
| 1 | TXT | `level1.txt` |
| 2 | XML | `level1.xml` |
| 3 | JSON | `level1.json` |
| 4 | Egyéni TXT | Felhasználó által megadott fájlútvonal |

A beépített pályafájlok a `src/main/resources/levels`
mappában találhatók.

A TXT-feldolgozó átugorja az üres sorokat, és külső
fájlútvonalról is képes pályát betölteni.

Sikertelen normál pályabetöltés esetén a program
alapértelmezett pályát használ.

## Játékparancsok

| Parancs | Jelentés |
|---------|----------|
| `A 1` | Az A autó mozgatása egy mezővel jobbra |
| `A -1` | Az A autó mozgatása egy mezővel balra |
| `B 1` | A B jármű mozgatása egy mezővel lefelé |
| `B -1` | A B jármű mozgatása egy mezővel felfelé |
| `a 1` | Kisbetűs járműazonosító használata |
| `a3 right 2` | Az a3 mezőn lévő jármű mozgatása jobbra két mezővel |
| `a3 jobb 2` | Mozgatás magyar iránymegadással |
| `exit A` | Kilépés a jobb oldali kijáraton |
| `save game.txt` | Az aktuális játéktábla mentése |
| `load game.txt` | Mentett játéktábla visszatöltése |
| `scores` | Ranglista megjelenítése |
| `help` | Elérhető parancsok megjelenítése |
| `quit` | Játék bezárása |

A járművek csak a saját tájolásuknak megfelelő irányban
mozoghatnak. Nem haladhatnak át más járműveken,
és normál mozgatással nem hagyhatják el a táblát.

A koordinátaalapú mozgatás angol és magyar
iránymegadásokat is támogat:

- `left` / `bal`
- `right` / `jobb`
- `up` / `fel`
- `down` / `le`

## Játék mentése és visszatöltése

Az aktuális játéktábla mentése:

```text
save game.txt
```

A mentett tábla visszatöltése:

```text
load game.txt
```

A mentés tartalmazza:

- A tábla sorainak és oszlopainak számát
- A járművek azonosítóit
- A járművek hosszát
- A járművek tájolását
- A járművek aktuális pozícióját
- A céljárművek megjelölését

A visszatöltés után a játék az aktuális játékos nevével,
a játékos körétől folytatható.

Sikertelen visszatöltés esetén az aktuális játéktábla
változatlan marad.

## TXT-pályafájl formátuma

Példa egy 6x6 méretű pályára:

```text
6 6
A 2 HORIZONTAL 2 0 true
B 3 VERTICAL 0 4 false
C 2 HORIZONTAL 4 2 true
```

Az első sor a tábla méretét tartalmazza.

A további sorok szerkezete:

```text
azonosito hossz tajolas sor oszlop celjarmu
```

A fájlban tárolt sor- és oszlopindexek nullától kezdődnek.

## Objektumorientált programtervezés

A projekt elkülönített modell-, szolgáltatás-,
adatkezelési és felhasználói felületi rétegekből áll.

### Model

A játéktábla, a járművek, a játékosok és a tájolások
reprezentációját tartalmazza.

A Vehicle és Player osztályok immutable objektumok:

- Final osztályok
- Private final adattagok
- Nincsenek setter metódusok
- Felülírt equals(), hashCode() és toString() metódusok

A Vehicle pozíciójának módosítása a withPosition()
metódussal, új objektum létrehozásával történik.

A Board osztály szintén rendelkezik equals(),
hashCode() és toString() metódusokkal.
A Board állapota a játék során változhat.

### Service

A GameService felelős a játékosok köreinek,
a játéklogikának és a számítógépes ellenfél
lépéseinek kezeléséért.

A számítógépes ellenfél külön táblamásolatokon
vizsgálja a lehetséges lépéseket, így a szimuláció
nem módosítja az eredeti játéktáblát.

### Persistence

A fájlok betöltéséért, a TXT-mentésekért és az
SQLite-adatbázis kezeléséért felelős réteg.

### UI

A ConsoleUI kezeli a felhasználói bemeneteket,
a játéktábla megjelenítését, a ranglistát
és a konzolos üzeneteket.

A közvetlen felhasználói konzolos kiírások
a UI-rétegben találhatók.

## Adatbázis és naplózás

A program SQLite-adatbázisban tárolja a játékosok
győzelmeinek számát.

Az adatbázisfájl neve:

`rushhour.db`

A ranglista a győzelmek száma szerint csökkenő
sorrendben jelenik meg. Azonos eredmény esetén
a játékosok neve alapján történik a rendezés.

A naplózás SLF4J és Logback használatával történik.

Az aktuális naplófájl helye:

`logs/rushhour.log`

A naplózási rendszer támogatja:

- DEBUG, INFO, WARN és ERROR naplózási szinteket
- Automatikus naplóforgatást
- 10 MB-os fájlméretküszöböt
- 30 napos archiválást
- 100 MB-os archiválási összméretkorlátot
- GZIP-tömörítést
- UTF-8 karakterkódolást

Az archivált naplók helye:

`logs/archive/`

A futás közben létrejövő adatbázis- és naplófájlokat
a .gitignore kizárja a verziókövetésből.

## Tesztelés

A projekt JUnit 5 és Mockito segítségével tesztelhető.

A teljes tesztcsomag futtatása:

```bash
mvn clean test
```

A teljes projekt ellenőrzése és telepítése:

```bash
mvn clean install
```

### Automatikus tesztek

| Tesztosztály | Tesztek száma |
|--------------|---------------|
| BoardTest | 37 |
| VehicleTest | 16 |
| PlayerTest | 14 |
| GameServiceTest | 16 |
| LevelFileTest | 14 |
| LevelFileMockitoTest | 2 |
| LevelXmlFileTest | 6 |
| LevelJsonFileTest | 7 |
| ScoreRepositoryTest | 16 |
| ConsoleUITest | 24 |
| **Összesen** | **152** |

### JaCoCo kódlefedettség

| Csomag | Utasításlefedettség | Áglefedettség |
|--------|---------------------|---------------|
| Model | 98% | 89% |
| Service | 95% | 82% |
| Persistence | 88% | 74% |
| UI | 76% | 64% |
| Main | 0% | - |
| **Összesített** | **88%** | **78%** |

A lefedettségi jelentés elkészítése:

```bash
mvn clean test jacoco:report
```

A jelentés helye:

`target/site/jacoco/index.html`

## Kódminőség

A projekt automatikus Checkstyle-ellenőrzést tartalmaz.

A Checkstyle a Maven validate fázisában fut le,
így a normál Maven build során automatikusan
ellenőrzi a forráskódot.

Szabálysértés esetén a build sikertelen lesz.

Külön is futtatható:

```bash
mvn checkstyle:check
```

A szabályokat a projekt gyökerében található
`checkstyle.xml` fájl határozza meg.

## Tanári visszajelzések alapján elvégzett javítások

### Issue #1 – Járművek mozgatása

- Negatív irányú mozgatás javítása
- Ütközések és pályahatárok ellenőrzése
- Kisbetűs járműazonosítók támogatása
- Számítógépes ellenfél bal oldali kijutásának javítása
- Mozgatási tesztek bővítése

### Issue #2 – Value Objects

- Immutable Vehicle és Player osztályok
- Setter metódus eltávolítása
- withPosition() metódus bevezetése
- equals(), hashCode() és toString() felülírása
- Független táblamásolatok használata az AI-ban
- Objektumok összehasonlításának tesztelése

### Issue #3 – Fájlkezelés

- Aktuális játéktábla mentése TXT-fájlba
- Mentett tábla visszatöltése
- Üres sorok megfelelő kezelése
- Egyéni fájlútvonal támogatása
- Fájlkezelési tesztek bővítése

### Issue #4 – Kódminőség és programstruktúra

- Automatikus Checkstyle-ellenőrzés
- Konzolos kiírások elkülönítése
- Koordinátaalapú mozgatás
- Magyar és angol iránymegadások
- SLF4J naplózás kibővítése
- Logback naplóforgatás beállítása

### Eredmények összehasonlítása

| Szempont | Eredeti | Javított |
|----------|---------|----------|
| Automatikus tesztek | 63 | 152 |
| Utasításlefedettség | 82% | 88% |
| Áglefedettség | 70% | 78% |
| Automatikus Checkstyle | Nem | Igen |
| Játéktábla mentése | Nem | Igen |
| Játéktábla visszatöltése | Nem | Igen |
| Immutable Vehicle és Player | Hiányos | Igen |
| Koordinátaalapú mozgatás | Nem | Igen |

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
    │   │   │   ├── Board.java
    │   │   │   ├── Vehicle.java
    │   │   │   ├── Player.java
    │   │   │   └── Orientation.java
    │   │   ├── service/
    │   │   │   └── GameService.java
    │   │   ├── persistence/
    │   │   │   ├── LevelFile.java
    │   │   │   ├── LevelXmlFile.java
    │   │   │   ├── LevelJsonFile.java
    │   │   │   └── ScoreRepository.java
    │   │   └── ui/
    │   │       └── ConsoleUI.java
    │   └── resources/
    │       ├── levels/
    │       │   ├── level1.txt
    │       │   ├── level1.xml
    │       │   └── level1.json
    │       └── logback.xml
    └── test/
        └── java/hu/nye/progtech/rushhour/
            ├── model/
            ├── service/
            ├── persistence/
            └── ui/
```

## Összegzés

A projekt célja a Java objektumorientált programozás,
a fájlkezelés, az adatbázis-kezelés, az automatikus
tesztelés és a Maven-alapú projektépítés gyakorlati
alkalmazása.

A tanári visszajelzések alapján a projekt
mozgatási logikája, objektummodellje, fájlkezelése,
naplózása és kódminőség-ellenőrzése továbbfejlesztésre
került.

A kibővített tesztcsomag és a magas kódlefedettség
hozzájárul a program megbízható működéséhez.