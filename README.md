# Rush Hour – Java beadandó

## Projekt bemutatása

A Rush Hour egy Java 21-ben készült, konzolos logikai játék,
amelyben egy NxM méretű táblán különböző járműveket kell mozgatni.

A játékos célja, hogy az **A jelű autót** a tábla jobb oldalán
található kijárathoz juttassa. A játékban számítógépes ellenfél
is szerepel, amely a **B jelű céljárművel** próbál kijutni
a bal oldalon.

A beépített pályán a C semleges jármű.
A tulajdonosokat és a kijáratok irányát a pályaadatok
határozzák meg.

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
- Véletlenszerű gépi jármű- és lépésválasztás
- Pályaadatokból meghatározott tulajdonosok és kijáratok
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
| `B 1` | A B autó mozgatása egy mezővel jobbra |
| `B -1` | A B autó mozgatása egy mezővel balra |
| `C 1` | A C jármű mozgatása egy mezővel lefelé |
| `C -1` | A C jármű mozgatása egy mezővel felfelé |
| `a 1` | Kisbetűs járműazonosító használata |
| `a3 right 2` | Az a3 mezőn lévő jármű mozgatása jobbra két mezővel |
| `a3 jobb 2` | Mozgatás magyar iránymegadással |
| `exit` | Kijutás a saját járműhöz rendelt kijáraton |
| `exit A` | Korábbi kijutási parancs, kompatibilitási célból |
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
- A járművek tulajdonosát
- A járművek kijáratának irányát

A visszatöltés után a játék az aktuális játékos nevével,
a játékos körétől folytatható.

Sikertelen visszatöltés esetén az aktuális játéktábla
változatlan marad.

A már befejezett játék táblája nem menthető el.

## TXT-pályafájl formátuma

Példa egy 6x6 méretű pályára:

```text
6 6
A 2 HORIZONTAL 2 0 HUMAN RIGHT
C 3 VERTICAL 0 4 NEUTRAL NONE
B 2 HORIZONTAL 4 2 COMPUTER LEFT
```

Az első sor a tábla méretét tartalmazza.

A további sorok szerkezete:

```text
azonosito hossz tajolas sor oszlop tulajdonos kijarat
```

A fájlban tárolt sor- és oszlopindexek nullától kezdődnek.

### Járművek tulajdonosai

| Érték | Jelentés |
|-------|----------|
| HUMAN | Emberi játékos céljárműve |
| COMPUTER | Számítógép céljárműve |
| NEUTRAL | Semleges jármű |

### Kijáratok iránya

| Érték | Jelentés |
|-------|----------|
| LEFT | Bal oldali kijárat |
| RIGHT | Jobb oldali kijárat |
| TOP | Felső kijárat |
| BOTTOM | Alsó kijárat |
| NONE | Nincs kijárat |

A JSON-pályák `owner` és `exitEdge` mezőket,
az XML-pályák azonos nevű attribútumokat használnak.

## Kijutási és győzelmi szabályok

A kijutás külön játékművelet.

A saját céljárműnek a hozzá rendelt pályaszélen
kell állnia a sikeres kijutáshoz.

A beépített pályán:

- Az emberi játékos céljárműve az A autó.
- A számítógép céljárműve a B autó.
- A C autó semleges jármű.
- Az A autó jobb oldalon juthat ki.
- A B autó bal oldalon juthat ki.

A tulajdonosokat és a kijáratokat nem a Board vagy
a GameService osztályba beégetett járműazonosítók,
hanem a pályaadatok határozzák meg.

A játékos és a számítógép ugyanazt a kijutási
ellenőrzést használja.

Sikertelen játékosi kijutás nem zárja le a kört.

Sikeres kijutás esetén a játék befejeződik,
a program kihirdeti a győztest, elmenti az eredményt,
és megjeleníti a ranglistát.

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

A `VehicleOwner` és az `ExitEdge` enumok a tulajdonosokat
és a kijáratok irányát reprezentálják.

A Board a járműadatokból határozza meg
a megengedett kijutást.

### Service

A GameService felelős a játékosok köreinek,
a játéklogikának és a számítógépes ellenfél
lépéseinek kezeléséért.

A számítógépes ellenfél külön táblamásolatokon
vizsgálja a lehetséges lépéseket, így a szimuláció
nem módosítja az eredeti játéktáblát.

### A számítógépes ellenfél algoritmusa

A számítógép véletlenszerű lépésválasztási algoritmust
használ.

Az algoritmus működése:

1. Összegyűjti a táblán található járműveket.
2. Véletlenszerűen kiválaszt egy járművet.
3. Meghatározza a jármű szabályos lépéseit.
4. Véletlenszerűen kiválaszt egy szabályos műveletet.
5. Ha nincs szabályos művelet, másik járművet választ.
6. A kiválasztott műveletet végrehajtja.
7. Ha egyik járművel sincs szabályos művelet,
   visszaadja a kört az emberi játékosnak.

A gép saját járművének kijutása is választható
szabályos művelet.

A számítógép nem egy előre meghatározott,
mindig azonos lépéssorozatot hajt végre.

A `Random` példány konstruktoron keresztül is
átadható, ami lehetővé teszi a reprodukálható
automatikus tesztelést.

### Persistence

A fájlok betöltéséért, a TXT-mentésekért és az
SQLite-adatbázis kezeléséért felelős réteg.

Főbb osztályai:

- LevelFile
- LevelJsonFile
- LevelXmlFile
- ScoreRepository

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

### Automatikus tesztek – aktuális eredmények

A 2026. november 25-i tesztfuttatás eredménye:

| Szempont | Eredmény |
|----------|----------|
| Automatikus tesztek száma | 169 |
| Sikeres tesztek | 169 |
| Sikertelen tesztek | 0 |
| Hibák | 0 |
| Kihagyott tesztek | 0 |
| Tesztek sikeressége | 100% |
| Maven build | BUILD SUCCESS |

A Maven tesztfuttatás eredménye:

```text
Tests run: 169, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

### Tesztosztályok

| Tesztosztály | Tesztelt terület |
|--------------|-----------------|
| BoardTest | Játéktábla, mozgatás és kijutás |
| VehicleTest | Járművek és immutable objektumok |
| PlayerTest | Játékosobjektumok |
| GameServiceTest | Játéklogika és számítógépes ellenfél |
| LevelFileTest | TXT-betöltés és mentés |
| LevelFileMockitoTest | Fájlkezelés Mockito használatával |
| LevelXmlFileTest | XML-pályák betöltése |
| LevelJsonFileTest | JSON-pályák betöltése |
| ScoreRepositoryTest | Adatbázis és ranglista |
| ConsoleUITest | Konzolos felhasználói felület |

### JaCoCo kódlefedettség

A második javítási kör után mért eredmények:

| Csomag | Utasításlefedettség | Áglefedettség |
|--------|---------------------|---------------|
| Model | 92% | 78% |
| Service | 98% | 82% |
| Persistence | 84% | 66% |
| UI | 76% | 66% |
| Main | 0% | - |
| **Összesített** | **86%** | **73%** |

További lefedettségi adatok:

- Sorlefedettség: megközelítőleg 84%
- Elemzett osztályok száma: 15
- Lefedett forráskódsorok: 738 / 879
- Lefedett utasítások: 3112 / 3615
- Lefedett ágak: 367 / 499

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
- Setter metódusok eltávolítása
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

### Issue #7 – Véletlenszerű gépi lépésválasztás

- A korábbi gépi lépésválasztási algoritmus módosítása
- Véletlenszerű járműválasztás
- Véletlenszerű szabályos lépés kiválasztása
- Nem mozgatható járművek kihagyása
- Szabályos lépés hiányának kezelése
- Injektálható Random példány bevezetése
- Táblamásolatokon végzett lépésszimuláció
- Automatikus tesztek aktualizálása és bővítése
- Véletlenszerű gépi lépések gyakorlati ellenőrzése

### Issue #8 – B céljármű és adatvezérelt kijutás

- A számítógép céljárművének módosítása C-ről B-re
- Mindhárom beépített pályafájl aktualizálása
- VehicleOwner enum bevezetése
- ExitEdge enum bevezetése
- Vehicle osztály kibővítése
- Tulajdonosok és kijáratok betöltése a pályaadatokból
- Beégetett céljármű-azonosítók eltávolítása
  a Board és GameService kijutási logikájából
- Közös kijutási szabály a játékos és a gép számára
- Helytelen kijáraton történő kijutás elutasítása
- Tulajdonosok és kijáratok megőrzése TXT-mentéskor
- TXT-, JSON- és XML-feldolgozók aktualizálása
- Általános exit parancs bevezetése
- Modell-, szolgáltatás-, fájlkezelési és UI-tesztek
  aktualizálása

### Második javítási kör ellenőrzése

| Ellenőrzés | Eredmény |
|------------|----------|
| Teljes Maven build | Sikeres |
| 169 automatikus teszt | Sikeres |
| Checkstyle-ellenőrzés a Maven build során | Sikeres |
| Véletlenszerű gépi lépések | Sikeres |
| Játékos győzelme az A autóval | Sikeres |
| Számítógép győzelme a B autóval | Sikeres |
| Helytelen kijárat elutasítása | Sikeres |
| Győzelmek mentése | Sikeres |
| Ranglista frissítése | Sikeres |
| Program hibamentes leállása | Sikeres |

### Eredmények összehasonlítása

| Szempont | Eredeti | Első javítás | Második javítás |
|----------|---------|--------------|-----------------|
| Automatikus tesztek | 63 | 152 | 169 |
| Utasításlefedettség | 82% | 88% | 86% |
| Áglefedettség | 70% | 78% | 73% |
| Automatikus Checkstyle | Nem | Igen | Igen |
| Játéktábla mentése | Nem | Igen | Igen |
| Játéktábla visszatöltése | Nem | Igen | Igen |
| Immutable Vehicle és Player | Hiányos | Igen | Igen |
| Koordinátaalapú mozgatás | Nem | Igen | Igen |
| Véletlenszerű gépi lépésválasztás | Nem | Nem | Igen |
| Adatvezérelt kijutás | Nem | Nem | Igen |
| Számítógép céljárműve | C | C | B |

A második javítási körben 17 új automatikus teszttel
bővült a projekt az első javítási körhöz képest.

Az eredeti beadandóhoz képest összesen 106 új
automatikus teszt került a projektbe.

Az új programlogika bevezetésével az összesített
kódlefedettség változott, miközben a Service réteg
utasításlefedettsége 95%-ról 98%-ra emelkedett.

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
    │   │   │   ├── Orientation.java
    │   │   │   ├── VehicleOwner.java
    │   │   │   └── ExitEdge.java
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
            │   ├── BoardTest.java
            │   ├── VehicleTest.java
            │   └── PlayerTest.java
            ├── service/
            │   └── GameServiceTest.java
            ├── persistence/
            │   ├── LevelFileTest.java
            │   ├── LevelFileMockitoTest.java
            │   ├── LevelXmlFileTest.java
            │   ├── LevelJsonFileTest.java
            │   └── ScoreRepositoryTest.java
            └── ui/
                └── ConsoleUITest.java
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

A második javítási körben a véletlenszerű gépi
lépésválasztás és az adatvezérelt kijutás is megvalósult.

A játékos és a számítógép győzelmét gyakorlati
teszteléssel is ellenőriztem.

A projekt aktuális állapotában 169 automatikus
teszt sikeresen lefutott, sikertelen vagy kihagyott
teszt nélkül.

A JaCoCo jelentés alapján az összesített
utasításlefedettség 86%, az áglefedettség 73%,
a sorlefedettség pedig megközelítőleg 84%.

A teljes Maven build sikeresen lefutott.

A kibővített tesztcsomag és a magas kódlefedettség
hozzájárul a program megbízható működéséhez.