# Session 1 — Lektorske poznamky

4 hodiny, 4 bloky po ~50 min. Viz `docs/SESSION_1.md` pro ucastnicke zadani.

## Wall-clock milestones

Pripravte stopky. Behem session je normalni zpomalit — tady jsou mezniky kdy byste MELI byt:

| Cas | Kde byt |
|-----|---------|
| 0:10 | Intro + representace projektu odpalena |
| 0:45 | Blok 1 `guided` TODO hotove (Card, Status, Priority) |
| 1:40 | Blok 2 `guided` TODO hotove (extension fun, validace) |
| 2:35 | Blok 3 `guided` TODO hotove (filter/groupBy v service) |
| 3:30 | Blok 4 `guided` TODO hotove (object + companion) |
| 3:55 | Blok 4 debrief + Q&A |

Pokud v kterykoliv okamziku zaostavate o vic nez 10 min → preskocit `stretch` v aktualnim bloku, `independent` nechat na domaci ukol.

## Ocekavane otazky po blocich

### Blok 1 — datovy model

- **"Proc `data class` misto obycejne `class`?"** → ukazat co generuje (equals/hashCode/copy/toString/componentN), paralela: Java 16 `record`, C# 9 `record class`. Kotlin's copy je silnejsi (named args pro partial update).
- **"Kdy `val` a kdy `var`?"** → `val` vzdy kdyz nemenis. Tady vsechny pole jsou `val` — zmena karty = `copy()`. Paralela Java `final`, C# `readonly`.
- **"Proc `String?` s otaznikem?"** → Kotlin rozlisuje nullable vs non-null v TYPU. Java by mela `@Nullable` anotaci, C# NRT pres `string?`. Kompiler vynucuje null check.
- **"Co kdyz chci `dueDate` vzdycky mit?"** → Zmen `LocalDate?` na `LocalDate`. Pak musis nastavit default nebo vsude predavat. V tomhle workshopu ukazujeme null safety, proto nullable.

### Blok 2 — extension funkce + when

- **"Proc extension fn a ne metoda na tride?"** → Class je v cizi knihovne (simulujeme). Extension fn je jen syntax sugar pro static utility, ale cte se jako metoda. Paralela: C# extension method, Java by potrebovala `CardUtils.isOverdue(card)`.
- **"Proc `when` vyhodnoti `Priority` exhaustive?"** → Enum a sealed interface maji konecnou mnozinu hodnot — compiler umi overit pokryti. Java `switch` to umi od 17, C# patterns od 8.
- **"Co kdyz zapomenu branch?"** → Kotlin dava chybu pri compile. Ukaz: smaz jeden case, spust build, ukaz chybu. Ucastnici uvidi WHY.

### Blok 3 — collections

- **"Jak se `filter`+`map` lisi od Java Streams?"** → V Javu `list.stream().filter().map().collect(toList())`. V Kotlinu `list.filter{}.map{}` primo na List — eager. Pro lazy `asSequence()`. C# LINQ je lazy by default (IEnumerable).
- **"`it` vs pojmenovane lambda param?"** → `it` je automaticky pro 1-arg lambdu. Pro 2+ args musis pojmenovat: `{ a, b -> ... }`. Kdyz je vnoreno, pojmenuj i 1-arg aby bylo jasne.
- **"Kde je `groupBy` uzitecne?"** → Vsude kde potrebujes bucket. Vraci `Map<K, List<V>>`. Tady rendering board po sloupcich.

### Blok 4 — object singleton + validace

- **"Co je `object` vs `companion object`?"** → `object X {}` je standalone singleton. `companion object` uvnitr tridy je singleton asociovany s tridou (pristup pres `X.member`). Paralela: Java `enum` with INSTANCE, C# `static class`.
- **"Proc validujes pres `when` misto if-else?"** → Cistsi pro vic podminek, lze vracet hodnotu. Pro 2 podminky `if/else` je OK, pro 4+ kombinaci stavu `when` citelnejsi.

## Kdy zastavit a vysvetlit na tabuli

- Prvni "Val cannot be reassigned" error → stop, vysvetli `val` vs `var`, ukaz `copy()`.
- Prvni "when must be exhaustive" error → stop, ukaz `else ->` vs. pridani chybejiciho case. Preferovat pridani case (exhaustive).
- Prvni "Only safe (?.) or non-null asserted calls allowed" → stop, null safety system. `?.` `?:` pattern.
- Ktokoliv napise `!!` → ZASTAV. Vysvetli PROC se nepouziva (NPE runtime, popirate celou null safety). Ukaz `?:` alternativu.

## Skip-if-late priorities

Pokud budes behem session zaostavat, preskakujes v tomto poradi:

1. **Prvni preskocit:** vsechny `stretch` tasky (companion factory, `displayTitle` se smajlikem).
2. **Druhe preskocit:** poslednich 10 min debriefu v Bloku 4. Rovnou prejdi na souhrn a zpetnou vazbu.
3. **Treti preskocit:** polovinu `independent` tasku v Bloku 3 — necht ucastnici dokonci doma.
4. **NIKDY nepreskakuj:** `guided` tasky. Jsou to main teaching moments.

## Post-run log

Po kazdem proveseni doplnte:

```
Datum: YYYY-MM-DD
Pocet ucastniku:
Dokoncene bloky: / 4
Nejvic casu sezralo: <blok / tema>
Nejcastejsi chyba/otazka: <...>
Co funguje dobre: <...>
Co upravit priste: <...>
```
