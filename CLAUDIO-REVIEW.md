# Claudio Review â€” powerlib-nexo-hook (feat/native-nexo-furniture-hook vs github/master)

**Esito:** âœ… approved Â· **score 78/100**

> Aggiunta coerente del bridge Nexo furniture-place con reflection difensiva e bump di versione da 1.3.7-SNAPSHOT a 1.3.8 su tutti i moduli; nessun problema di sicurezza rilevato.

## Findings

- ðŸ”µ **[conventions]** AGENTS.md assente nella root del repo: impossibile verificare le convenzioni di casa specifiche; il peso 'Repo conventions' è stato azzerato e ridistribuito sulle altre categorie.  
  `root`
  â†’ **Fix:** Aggiungere un AGENTS.md che documenti package base, framework, persistenza e naming per questo repo.
- ðŸ”µ **[build_correctness]** Il repo è powerlib (it.mycraft, non it.novaverse); il bump da 1.3.7-SNAPSHOT a release 1.3.8 è applicato in modo coerente su tutti i moduli, ma passare da SNAPSHOT a release fissa implica che il ciclo di sviluppo successivo dovrà reintrodurre uno SNAPSHOT.  
  `pom.xml Â· version`
  â†’ **Fix:** Confermare che 1.3.8 sia una release intenzionale e prevedere il successivo bump a 1.3.9-SNAPSHOT post-release; verificare che il deploy su repo.novaverse.it/repo mycraft sia gated su main.
- ðŸŸ¡ **[code_quality]** handleNativeInteract e handleNativeBreak sono cablati tramite hookNative e assegnati a nativeInteractHooked/nativeBreakHooked, ma per il place l'esito di hookNative viene ignorato (nessun campo nativePlaceHooked). Se il place nativo fallisse l'aggancio, non esiste fallback Bukkit per il placement (come documentato nel javadoc dell'evento), quindi la perdita è accettabile, ma l'asimmetria non è esplicitata nel codice.  
  `bukkit/src/main/java/it/mycraft/powerlib/bukkit/listeners/NexoListener.java Â· handleNativeInteract / handleNativePlace / handleNativeBreak`
  â†’ **Fix:** Aggiungere un commento inline o un campo (anche solo per log) che chiarisca che il place non ha fallback per scelta, così l'assenza di nativePlaceHooked risulta intenzionale e non una dimenticanza.
- ðŸŸ¡ **[code_quality]** furnitureLookup non è gated su AVAILABLE per scelta documentata, ma dipende dalla corretta inizializzazione di furnIsFurniture; se il metodo fosse risolto ma il provider Nexo non disponibile a runtime, invoke potrebbe restituire risultati non affidabili. Il comportamento è mitigato dal ritorno null in caso di eccezione, quindi rischio basso.  
  `bukkit/src/main/java/it/mycraft/powerlib/bukkit/utils/NexoUtils.java Â· isKnownFurniture / furnitureLookup`
  â†’ **Fix:** Verificare con un test che, quando il provider Nexo non è caricato ma furnIsFurniture è bound, isKnownFurniture ritorni false coerentemente per non far saltare il fallback Bukkit.
- ðŸ”µ **[code_quality]** location può risultare null se nexoFurniture non è un Entity e block è null; i consumer che chiamano getLocation() dovranno gestire il null. Coerente con lo stile difensivo del resto del bridge.  
  `bukkit/src/main/java/it/mycraft/powerlib/bukkit/events/NexoFurniturePlaceEvent.java Â· resolveLocation`
  â†’ **Fix:** Documentare nel javadoc del getter (o del costruttore) che location può essere null quando né l'entity né il block sono disponibili.

## Modifiche richieste

Nessuna. âœ…
