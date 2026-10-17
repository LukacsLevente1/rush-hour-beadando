# Rush Hour NxM – Java 21 konzolos beadandó

Két céljárműves, parancssoros Rush Hour játék Java 21 és Maven használatával.

## Követelmények
- Java JDK 21 és Maven 3.9+
- A függőségeket a Maven tölti le első futtatáskor.

## Fordítás és futtatás
A projekt gyökérkönyvtárában:

```bash
mvn clean install
java -jar target/rush-hour-1.0.0-jar-with-dependencies.jar
```

Pálya megadása indításkor:

```bash
java -jar target/rush-hour-1.0.0-jar-with-dependencies.jar palya.json
```

## Parancsok
- `board` – tábla megjelenítése
- `move A+2` vagy `move H-1` – jármű mozgatása; a `+` jobbra/lefelé, a `-` balra/felfelé mozgat
- `load palya.json` – pálya betöltése
- `save allas.json` – aktuális állás mentése JSON-fájlba
- `scores` – legjobb győztes eredmények listázása
- `help` – súgó
- `quit` – kilépés

A koordináták a JSON-fájlban nullától indexeltek. A járművek `id`, `row`, `col`, `length`, `orientation`, `humanTarget`, `aiTarget` mezőkkel adhatók meg. Pontosan egy `humanTarget` és egy `aiTarget` szükséges. A H céljármű jobbra, a B céljármű balra hagyja el a táblát. A gép a lehetséges lépések közül véletlenszerűen választ.

A játékos neve, lépésszáma és győzelmi eredménye a futtatási könyvtárban létrejövő `rushhour.db` SQLite-adatbázisba kerül. A `mvn verify` lefuttatja a teszteket és létrehozza a JaCoCo lefedettségi jelentést a `target/site/jacoco` könyvtárban.
