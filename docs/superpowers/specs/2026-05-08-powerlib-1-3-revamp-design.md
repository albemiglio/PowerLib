# PowerLib 1.3.0 — Revamp & GitLab Fork Port — Design

**Date:** 2026-05-08
**Author:** Alberto Migliorato
**Target version:** `1.3.0-SNAPSHOT`
**Source baseline:** `1.2.16-SNAPSHOT` (commit `420e34b` on `master`)

## Background

A private GitLab fork at `git.novaverse.it/novaverse/miscellaneous/powerlib` is 3 commits ahead of origin and adds Nexo integration, ItemsAdder hardening, `PersistentDataContainer` support, 1.20.5+ API renames, and a shading-safe audience refactor. The fork also introduces two breaking changes (Spigot API bump 1.19.4→1.21.1 and direct constant replacements) and one regression (`VelocityAudience.commandSenderClass` set to `ProxyServer`).

This spec describes how to port the fork's value into the upstream repo while:
- preserving backward compatibility for **API consumers** (no public signature changes);
- preserving backward compatibility for **Minecraft server runtime ≥ 1.16** (no `NoSuchFieldError`/`NoSuchMethodError` on older servers);
- adding four new revamp features as additive modules;
- maintaining clean code and avoiding code smells.

## Goals

1. Port every valuable change from the fork (Nexo bridge, ItemsAdder hardening, PDC support, audience shading fix), reformulated to respect the runtime 1.16+ constraint.
2. Fix the fork's bugs (`VelocityAudience.commandSenderClass`, orphan repos in `bungee/pom.xml`).
3. Add four new features as separately-importable Maven modules:
    - MiniMessage support for `Message` and `ItemBuilder`.
    - DataComponent helpers (1.20.5+, opt-in).
    - Multi-platform fluent command builder.
    - Paged InventoryBuilder with slot click handlers (additive in `bukkit`).
4. Add a `tests` module with JUnit 5 + Mockito + MockBukkit covering the new and refactored code.
5. Bump version to `1.3.0-SNAPSHOT`.

## Non-goals

- Bumping `spigot-api` compile dependency from 1.19.4 to 1.21.1 (breaks runtime 1.16+ support).
- Removing or deprecating any existing public method.
- Adding `api-version: '1.19'` to `plugin.yml` (would warn on older servers).
- Setting up CI/CD pipelines (out of scope; can be a follow-up).
- Smoke testing on real servers across MC versions (left to the user).

## Constraints

- **Consumer API compat:** every existing public method/class signature in `1.2.16-SNAPSHOT` remains valid in `1.3.0-SNAPSHOT`. Additions only.
- **Runtime compat:** the library must load and run on Spigot/Paper servers from MC 1.16 to 1.21+, except for the `powerlib-components` module (1.20.5+ only) which is opt-in.
- **Compile target:** `spigot-api 1.19.4-R0.1-SNAPSHOT` (unchanged from `1.2.16-SNAPSHOT`).
- **Java target:** 16 (unchanged).
- **Code quality:** idiomatic Java, no exception-swallowing `catch (Exception ignored)`, no god classes, helpers extracted from `ItemBuilder` and `NexoUtils`.

## Module structure

Existing modules: `common`, `bukkit`, `bukkit-plugin`, `bungee`, `bungee-plugin`, `velocity`, `velocity-plugin`, `all`.

New modules (as decided: granular):

| Module                       | Depends on                          | Purpose                                                                |
|------------------------------|-------------------------------------|------------------------------------------------------------------------|
| `powerlib-minimessage`       | `powerlib-common`                   | MiniMessage parsing helpers; integrates with `Message` and `ItemBuilder`. |
| `powerlib-components`        | `powerlib-bukkit`                   | DataComponent fluent helpers, **requires MC 1.20.5+**. Opt-in.         |
| `powerlib-commands-api`      | `powerlib-common`                   | Fluent command builder API + types.                                    |
| `powerlib-commands-bukkit`   | `commands-api` + `powerlib-bukkit`  | Bukkit impl, Paper-Brigadier with `CommandExecutor` fallback.          |
| `powerlib-commands-bungee`   | `commands-api` + `powerlib-bungee`  | Bungee impl.                                                           |
| `powerlib-commands-velocity` | `commands-api` + `powerlib-velocity`| Velocity impl (native Brigadier).                                      |
| `powerlib-tests`             | all of the above (test scope)       | JUnit 5 + Mockito + MockBukkit. `<skip>true</skip>` on deploy.         |

The `PagedInventoryBuilder` is added inline in `bukkit` (not a separate module).

The `all` module includes the new modules **except** `powerlib-components` (to keep `all` runnable on MC 1.16+).

## Backward compatibility strategy

### Consumer API
Additive only. Examples:
- `ItemBuilder.setPersistentData(NamespacedKey, PersistentDataType<T,Z>, Z)` is a new method; existing builders unchanged.
- `Message.miniMessage(String)` is a new entrypoint; `Message.create(...)` unchanged.
- `ItemBuilder.setNameMini(String)`, `ItemBuilder.addLoreMini(String...)`, `ItemBuilder.setLoreMini(List<String>)` are new; `setName` / `addLore` / `setLore` unchanged.
- `ItemBuilder.addBuildStep(Consumer<ItemMeta>)` is new; used by the `components` module to inject DataComponent application without touching `build()`.

### Runtime MC 1.16+
The fork's direct constant replacements (`Enchantment.UNBREAKING`, `PotionEffectType.STRENGTH`, ...) would `NoSuchFieldError` on MC <1.20.5 because those static fields don't exist. To support 1.16+ we introduce `RegistryCompat`:

```java
package it.mycraft.powerlib.bukkit.compat;

public final class RegistryCompat {
    private static final Map<String, Enchantment> ENCHANT_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, PotionEffectType> POTION_CACHE = new ConcurrentHashMap<>();

    public static Enchantment glowEnchant() { return enchantment("unbreaking"); }

    public static Enchantment enchantment(String key) {
        return ENCHANT_CACHE.computeIfAbsent(key, RegistryCompat::lookupEnchant);
    }

    public static PotionEffectType potionEffect(String key) {
        return POTION_CACHE.computeIfAbsent(key, RegistryCompat::lookupPotion);
    }

    private static Enchantment lookupEnchant(String key) {
        // 1) Registry.ENCHANTMENT.get(NamespacedKey.minecraft(key))   — 1.20.x+
        // 2) Enchantment.getByKey(NamespacedKey.minecraft(key))        — 1.13+
        // 3) Reflection fallback on static fields (legacy + new name)
        // throw IllegalStateException if all three fail.
    }

    private static PotionEffectType lookupPotion(String key) {
        // 1) PotionEffectType.getByKey(NamespacedKey.minecraft(key))  — 1.20.x+ (where applicable)
        // 2) PotionEffectType.getByName(legacyOrNewName)              — 1.16+
        // 3) Reflection fallback. Throw if all fail.
    }

    private RegistryCompat() {}
}
```

`LegacyPotionAPI` keeps its public enum constant names unchanged (e.g. `INCREASE_DAMAGE_0`); the `PotionEffectType type` field is populated lazily via `RegistryCompat.potionEffect("strength")`. `ItemBuilder.build()` uses `RegistryCompat.glowEnchant()` instead of `Enchantment.DURABILITY`.

### Opt-in 1.20.5+ via `powerlib-components`
The `components` module compiles against `spigot-api 1.20.5-R0.1-SNAPSHOT` (provided). Its README states the runtime requirement. It is not part of `powerlib-bukkit` and not transitive on consumers.

## Fork porting (reformulated)

### 1. Nexo bridge

Files to add (under `bukkit/src/main/java/it/mycraft/powerlib/bukkit/`):
- `events/NexoFurnitureInteractEvent.java` — wraps Nexo's furniture interact event so downstream plugins do not need a direct Nexo dependency. Cancellable. Carries `Player`, `String furnitureId`, `Object nexoFurniture`.
- `listeners/NexoListener.java` — listens to `PlayerInteractEvent` (RIGHT_CLICK_BLOCK) and `PlayerInteractEntityEvent`, uses `NexoUtils.getNexoId(...)`, dispatches `NexoFurnitureInteractEvent`. Logging via `plugin.getLogger().log(Level.WARNING, "...", e)` — no `catch (Exception ignored)`.
- `utils/NexoUtils.java` — reflection-based wrappers around `com.nexomc.nexo.api.NexoFurniture`, `NexoBlocks`, `NexoItems`. Refactored from the fork:
    - `static {}` initializer uses a private helper `safeMethod(Class, String, Class<?>...)` that returns `null` and logs at `FINE` level on `NoSuchMethodException` instead of swallowing.
    - All `catch (Exception ignored)` replaced with `catch (ReflectiveOperationException e)` that logs at `FINE`.
- `compat/NexoSupport.java` (new): centralises `Bukkit.getPluginManager().isPluginEnabled("Nexo")` so it isn't duplicated across `ItemBuilder`, `NexoListener`, `PowerLib`.

Modifications:
- `PowerLib.inject(Plugin)` registers a `NexoListener` field; adds `public static boolean isNexoAvailable()` delegating to `NexoSupport.isAvailable()`.
- `ItemBuilder.setMaterial(String)` and `ItemBuilder.build()` accept the prefix `nexo:<id>`. The build branch calls `com.nexomc.nexo.api.NexoItems.itemFromId(customItem)` (reflection in `NexoSupport.buildItem(String id, int amount)`) with `Material.BARRIER` fallback.
- `bukkit/pom.xml`: add `nexomc-repo` (`https://repo.nexomc.com/releases`) and `alessiodp-repo` (`https://repo.alessiodp.com/releases/`); add `<dependency>com.nexomc:nexo:1.0.0</dependency>` `provided`.
- `bukkit-plugin/src/main/resources/plugin.yml`: add `softdepend: [Nexo]`. Do **not** add `api-version: '1.19'` (would alienate <1.19 servers).

### 2. ItemsAdder hardening

Files to add:
- `bukkit/.../compat/ItemsAdderBridge.java` — single home for ItemsAdder reflection. Public methods:
    - `boolean isAvailable()` — delegates to `Bukkit.getPluginManager().isPluginEnabled("ItemsAdder")` (cached).
    - `Optional<Pair<String,String>> extractData(ItemStack itemStack)` — uses `dev.lone.itemsadder.api.CustomStack#byItemStack(ItemStack)` + `getNamespacedID()` via reflection.
    - `Optional<ItemStack> buildItem(String namespacedId, int amount)` — for the `itemsadder:<id>` prefix path in `ItemBuilder.build()`.

Modifications:
- `ItemBuilder.clone(ItemStack)`: replace the inline reflection block with `ItemsAdderBridge.extractData(itemStack).ifPresent(this::setItemsAdderData)`.
- `ItemBuilder.build()`: the `itemsadder:` branch already exists; reroute through `ItemsAdderBridge.buildItem(...)`.
- `ItemBuilder.build()`: keep the post-build NBT compound write inside a `try/catch` that logs (not swallows) — the fork added this and it's a real defensive measure.

### 3. `setPersistentData(NamespacedKey, PersistentDataType<T,Z>, Z)`

Files to add:
- `bukkit/.../item/applier/PersistentDataApplier.java` — `apply(ItemMeta, Map<NamespacedKey, PersistentEntry<?,?>>)`.
- A nested `record PersistentEntry<T,Z>(PersistentDataType<T,Z> type, Z value)` (Java 16 record).

Modifications:
- `ItemBuilder` adds field `Map<NamespacedKey, PersistentEntry<?,?>> persistentData = new LinkedHashMap<>();`.
- New public method:
  ```java
  public <T, Z> ItemBuilder setPersistentData(NamespacedKey key, PersistentDataType<T,Z> type, Z value) {
      persistentData.put(key, new PersistentEntry<>(type, value));
      return this;
  }
  ```
- `ItemBuilder.build()` invokes `PersistentDataApplier.apply(itemMeta, persistentData)` after meta name/lore/glow/customModelData are applied.

### 4. API rename 1.20.5+ via `RegistryCompat`

Files to add: `bukkit/.../compat/RegistryCompat.java` (already specified above).

Modifications:
- `LegacyPotionAPI`: `PotionEffectType type` field initialized lazily in the enum constructor via a string key (`"strength"`, `"slowness"`, `"jump_boost"`, `"instant_damage"`, etc.). Public enum constant names unchanged (`INCREASE_DAMAGE_0`, ...).
- `ItemBuilder.build()`: `Enchantment.DURABILITY` → `RegistryCompat.glowEnchant()`. Existing null-checks on `name`/`lore` from the fork are kept (they're correct).

### 5. Audience shading fix

Files modified (in `common/.../chat/`):
- `BukkitAudience.java`, `BungeeAudience.java`, `VelocityAudience.java`: replace hardcoded `Class.forName("it.mycraft.powerlib...")` with `Class.forName(this.getClass().getPackage().getName().replace(".common.chat", ".<platform>.adapters") + ".AudienceAdapter")`.
- **Velocity-specific:** keep `commandSenderClass = Class.forName("com.velocitypowered.api.command.CommandSource")` — the fork incorrectly set it to `ProxyServer`. We do not propagate that.

### 6. Lombok / NBT-API bumps

- root `pom.xml`: Lombok 1.18.22 → 1.18.30.
- `bukkit-plugin/pom.xml`, `bungee-plugin/pom.xml`: Lombok annotation processor path 1.18.22 → 1.18.30.
- `bukkit/pom.xml`: NBT-API 2.14.2-SNAPSHOT → 2.15.5. Verified at build time that 2.15.5 supports MC 1.16+ (tr7zw's library historically does); rollback to 2.14.x if not.

### 7. Cleanup

- `.gitignore`: add `target/`, `*.iml`, `.idea/`, `*.class`, `.mvn/.mvn*`, `dependency-reduced-pom.xml`, `.DS_Store`. Use `git rm --cached` on any currently-tracked artefacts.
- Add Maven Wrapper: `mvnw`, `mvnw.cmd`, `.mvn/wrapper/maven-wrapper.properties`, `.mvn/wrapper/maven-wrapper.jar` — generated via `mvn wrapper:wrapper`.
- Do not propagate `bungee/pom.xml`'s `paper` and `central` repos from the fork (no dependency justifies them).

### 8. Bugs not propagated

- `VelocityAudience.commandSenderClass = ProxyServer` (regression).
- `bungee/pom.xml` orphan repos.
- Nested `.mvn/.mvn/.mvn/...` wrapper artefacts.
- `target/` files committed by mistake in feature commits of the fork (cleaned up in fork's chore commit but never propagated here).

## Revamp features

### Feature A — `powerlib-minimessage`

**Public API** (`it.mycraft.powerlib.minimessage.MiniMessageSupport`):
```java
public final class MiniMessageSupport {
    public static Component parse(String input);                 // delegates to MiniMessage.miniMessage().deserialize(input)
    public static String parseLegacy(String input);              // parse() then LegacyComponentSerializer.legacySection().serialize(component)
}
```

**Integration**:
- `Message.miniMessage(String)` — new factory in `common/.../chat/Message.java`. Returns a `Message` whose internal text was already converted to legacy via `MiniMessageSupport.parseLegacy(...)`. The existing `Message.create(...)` is unchanged.
- `ItemBuilder.setNameMini(String)`, `ItemBuilder.addLoreMini(String...)`, `ItemBuilder.setLoreMini(List<String>)` — added in `bukkit/.../item/ItemBuilder.java`. They internally call `MiniMessageSupport.parseLegacy(...)` and then route to the existing `setName` / `addLore` / `setLore`.

**Maven**: new module `minimessage/pom.xml`; depends on `powerlib-common`; pulls in `net.kyori:adventure-text-minimessage:4.x` `compile`.

**Code smell prevention**: `MiniMessageSupport` is a final class with a private constructor and static methods only — not a god class, just a thin facade. The `Message` and `ItemBuilder` integration delegates without duplicating parsing logic.

### Feature B — `powerlib-components`

**Public API** (`it.mycraft.powerlib.components.ComponentBuilder`):
```java
public class ComponentBuilder {
    public static ComponentBuilder create();
    public ComponentBuilder food(int nutrition, float saturation, boolean canAlwaysEat);
    public ComponentBuilder tool(...);
    public ComponentBuilder consumable(...);
    public ComponentBuilder repairable(...);
    public ComponentBuilder attributeModifier(...);
    public ComponentBuilder unbreakable(boolean enabled);
    public ComponentBuilder enchantmentGlintOverride(boolean enabled);
    public ComponentBuilder itemName(Component name);
    public ComponentBuilder customModelData(int data);
    public void applyTo(ItemBuilder itemBuilder);   // adds a build step
    public void applyTo(ItemStack itemStack);       // applies directly
}
```

**Internals**: a list of `Consumer<ItemMeta>` (one per called fluent method) is accumulated; `applyTo(ItemBuilder)` calls `itemBuilder.addBuildStep(meta -> consumers.forEach(c -> c.accept(meta)))`.

**Maven**: new module `components/pom.xml`; depends on `powerlib-bukkit`; declares `spigot-api 1.20.5-R0.1-SNAPSHOT` `provided`. Module README states "MC 1.20.5+ runtime required". Excluded from `all`.

**Code smell prevention**: pure builder pattern, no static mutable state, no reflection (relies on the 1.20.5+ `ItemMeta` setters being present at runtime — that's the module contract).

### Feature C — Multi-platform fluent commands

**Public API** (in `commands-api`):
```java
public class CommandBuilder {
    public static CommandBuilder create(String name);
    public CommandBuilder description(String desc);
    public CommandBuilder permission(String permission);
    public CommandBuilder argument(Argument<?> arg);
    public CommandBuilder executor(BiConsumer<CommandSender, CommandContext> exec);
    public CommandBuilder tabComplete(BiFunction<CommandSender, CommandContext, List<String>> tab);
    public CommandBuilder subcommand(String name, Consumer<CommandBuilder> config);
    public abstract void register(Object plugin);
}
```

`Args` factory provides `string`, `integer`, `bool`, `enum(Class)`, `player`, `world`, plus an extension point `Args.custom(String name, Function<String, T> resolver)`.

**Bukkit impl** (`commands-bukkit`):
- `BukkitCommandBuilder` extends `CommandBuilder`. Its `register(Plugin plugin)` does:
    1. `try { Class.forName("io.papermc.paper.command.brigadier.Commands"); usePaperBrigadier = true; }` (cached) — Paper 1.20.4+.
    2. If yes → register via Paper Brigadier API (reflection so we don't compile against Paper).
    3. Else → register a `BukkitCommand` programmatically through `CommandMap` + `TabCompleter`.

**Bungee impl** (`commands-bungee`): registers via `net.md_5.bungee.api.plugin.Command` extending a shim that delegates to the fluent executor.

**Velocity impl** (`commands-velocity`): registers via Velocity's native Brigadier (`SimpleCommand` or `BrigadierCommand` factory of `com.velocitypowered.api.command.CommandManager`).

**Maven**: 4 new modules. `commands-bukkit` declares `io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT` `provided` (only used reflectively at registration time).

**Code smell prevention**: each platform module owns only its registration adapter. Argument parsing, validation, and dispatch live in `commands-api`. No platform-specific code leaks across modules.

### Feature D — `PagedInventoryBuilder` (in-tree)

**Public API** (in `bukkit/.../inventory/PagedInventoryBuilder.java`):
```java
public class PagedInventoryBuilder {
    public static PagedInventoryBuilder create(int rows, String title);
    public PagedInventoryBuilder items(List<ItemStack> items);
    public PagedInventoryBuilder renderer(BiFunction<ItemStack, Integer, ItemStack> renderer);
    public PagedInventoryBuilder navigation(NavigationLayout layout);
    public PagedInventoryBuilder onClick(int slot, BiConsumer<Player, InventoryClickEvent> handler);
    public PagedInventoryBuilder onPageChange(BiConsumer<Player, Integer> handler);
    public PagedInventoryBuilder filler(ItemStack filler);
    public void open(Player player);
}
```

`NavigationLayout` is an enum/interface (`bottomRow()`, `corners()`, `custom(int prevSlot, int nextSlot)`).

**Internals**:
- A registry `Map<UUID, OpenedPagedInventory>` keyed by the holder.
- A single internal `Listener` (registered once via `PowerLib.inject`) handles `InventoryClickEvent` and `InventoryCloseEvent`, dispatching to the right handler and cleaning up state on close.
- Navigation is implemented by the listener: clicking the configured prev/next slots calls `setPage(currentPage ± 1)` which re-renders the view.

**Maven**: no new module — added to `bukkit`.

**Code smell prevention**: the listener and the `OpenedPagedInventory` state class are package-private; only `PagedInventoryBuilder` is public. The shared listener avoids "one listener per inventory" leak.

## Test plan

New module `tests` with JUnit 5 + Mockito + MockBukkit + AssertJ. Runs locally via `mvn -pl tests test`. Deploy disabled.

**Coverage priority** (initial targets, not exhaustive):

- `common`: `Pair` equals/hashCode, `RandomDraw` distribution with fixed seed + edge cases (`weight=0`, empty list), `ColorAPI` round-trip (legacy/hex/gradient), `ConfigManager` load/save/missing keys, `JsonConfiguration` and `YamlConfiguration` round-trips.
- `bukkit` (with MockBukkit): `ItemBuilder` build paths (vanilla material, `itemsadder:` with mocked bridge, `nexo:` with mocked bridge, `setPersistentData` round-trip via `clone(ItemStack)`, glow effect, custom model data, lore placeholders); `RegistryCompat` cache + fallbacks; `LegacyPotionAPI` legacy id lookup; `NexoUtils` returning `null` cleanly when Nexo classes are absent.
- `minimessage`: `MiniMessageSupport.parse` for gradient/hover/click; `parseLegacy` round-trip.
- `commands-api` + `commands-bukkit` (with MockBukkit): registration, executor dispatch, typed argument parsing, fallback when Paper-Brigadier classes are absent, tab-complete return values.

Reflection-heavy code (`RegistryCompat`, `NexoUtils`, `ItemsAdderBridge`, `BukkitCommandBuilder.usePaperBrigadier` detection) is the highest-priority test target because that's where runtime failures hide.

## Versioning

- All `pom.xml` files: `1.2.16-SNAPSHOT` → `1.3.0-SNAPSHOT`.
- `bukkit-plugin/src/main/resources/plugin.yml`: version → `1.3.0-SNAPSHOT`.
- `README.md`: document the new modules and add a "Compatibility matrix" section listing module → MC version requirement.

## Commit sequence

Each commit must compile cleanly. Where a commit touches code with tests, those tests must pass.

1. `chore: ignore build artifacts and add maven wrapper`
2. `chore: bump lombok 1.18.22 → 1.18.30`
3. `chore: bump nbt-api 2.14.2-SNAPSHOT → 2.15.5`
4. `feat(bukkit): add RegistryCompat for renamed enchant/potion constants`
5. `feat(bukkit): extract ItemsAdderBridge and harden ItemsAdder integration`
6. `feat(bukkit): add ItemBuilder.setPersistentData via PersistentDataApplier`
7. `feat(bukkit): add Nexo bridge (event, listener, utils, item prefix)`
8. `fix(common): make audience adapter package shading-safe and restore Velocity CommandSource`
9. `feat: add powerlib-minimessage module`
10. `feat: add powerlib-components module (DataComponent helpers, requires MC 1.20.5+)`
11. `feat: add commands-api and commands-bukkit modules`
12. `feat: add commands-bungee and commands-velocity modules`
13. `feat(bukkit): add PagedInventoryBuilder with slot handlers`
14. `test: add tests module with junit5 + mockito + mockbukkit`
15. `chore: bump version to 1.3.0-SNAPSHOT and update README`

No `Co-Authored-By` trailer on any commit (per user preference).

## Verification

Before considering the revamp complete:
- `mvn clean install -DskipTests` succeeds for every module.
- `mvn -pl tests test` is green.
- `mvn dependency:tree` on `powerlib-bukkit` shows no new mandatory runtime dependency (Nexo is `provided`).
- Manual smoke test on real servers (Spigot 1.16.5, 1.19.4, 1.20.5, 1.21) is left to the user.

## Open follow-ups (out of scope for this design)

- GitHub Actions CI to run `mvn test` on PRs.
- Jacoco coverage report.
- Migration guide for consumers wanting to adopt the new modules.
- Eventual deprecation of legacy `Message.create(...)` in favour of `Message.miniMessage(...)` in a future major (2.0.0).
