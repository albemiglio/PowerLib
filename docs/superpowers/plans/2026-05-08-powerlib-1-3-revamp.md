# PowerLib 1.3.0 Revamp — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Port the GitLab fork's value (Nexo bridge, ItemsAdder hardening, PDC support, audience shading fix) into upstream PowerLib without breaking consumer API or runtime support for MC 1.16+, then add four revamp features (MiniMessage, DataComponent helpers, multi-platform fluent commands, paged inventory) and a tests module.

**Architecture:** Multi-module Maven (existing `common`/`bukkit`/`bungee`/`velocity` etc. plus new `minimessage`, `components`, `commands-api`, `commands-bukkit`, `commands-bungee`, `commands-velocity`, `tests`). Compile target stays Spigot 1.19.4 to keep runtime compat 1.16+; renamed 1.20.5+ constants are routed through a `RegistryCompat` runtime-lookup helper. Each new feature is in its own module so consumers import only what they need.

**Tech Stack:** Java 16, Maven multi-module, Lombok 1.18.30, Spigot API 1.19.4 (compile, runtime ≥1.16), Adventure 4.x (already in deps), MiniMessage 4.x, NBT-API 2.15.5, Nexo 1.0.0 (provided), Paper API 1.20.4 (provided, reflection-only), JUnit 5 + Mockito 5 + MockBukkit 3 + AssertJ.

**Reference spec:** `docs/superpowers/specs/2026-05-08-powerlib-1-3-revamp-design.md` (commit `212b90d`). Read it before starting — it's the source of truth for every API decision below.

**Working directory:** `C:\Users\migli\Desktop\IntelliJ Projects\PowerLib` on branch `master`. If you want isolation, create a worktree before starting; otherwise proceed on `master`.

**Commit hygiene:** No `Co-Authored-By` trailer. Plain commits.

---

## File structure overview

### New files
```
.gitignore                                                              [modify]
mvnw, mvnw.cmd, .mvn/wrapper/maven-wrapper.{jar,properties}             [add via mvn wrapper:wrapper]

bukkit/src/main/java/it/mycraft/powerlib/bukkit/
├── compat/
│   ├── RegistryCompat.java                  [new] runtime lookup for renamed enchant/potion constants
│   ├── ItemsAdderBridge.java                [new] reflection-based IA integration
│   └── NexoSupport.java                     [new] dedup `isPluginEnabled("Nexo")` + Nexo item builder
├── events/
│   └── NexoFurnitureInteractEvent.java      [new]
├── listeners/
│   └── NexoListener.java                    [new]
├── utils/
│   └── NexoUtils.java                       [new]
├── item/
│   ├── ItemBuilder.java                     [modify] additive methods + reroute via helpers
│   ├── LegacyPotionAPI.java                 [modify] lazy lookup via RegistryCompat
│   └── applier/
│       ├── PersistentDataApplier.java       [new]
│       └── PersistentEntry.java             [new] record
└── inventory/
    ├── PagedInventoryBuilder.java           [new]
    ├── NavigationLayout.java                [new]
    └── internal/PagedInventoryListener.java [new] package-private

bukkit/src/main/java/it/mycraft/powerlib/bukkit/PowerLib.java           [modify] register Nexo + paged listener

common/src/main/java/it/mycraft/powerlib/common/chat/
├── BukkitAudience.java                       [modify] shading-safe package derivation
├── BungeeAudience.java                       [modify] same
├── VelocityAudience.java                     [modify] same + restore CommandSource
└── Message.java                              [modify] add Message.miniMessage(...) factory

minimessage/                                  [new module]
├── pom.xml
└── src/main/java/it/mycraft/powerlib/minimessage/MiniMessageSupport.java

components/                                   [new module, MC 1.20.5+ only]
├── pom.xml
└── src/main/java/it/mycraft/powerlib/components/ComponentBuilder.java

commands-api/                                 [new module]
├── pom.xml
└── src/main/java/it/mycraft/powerlib/commands/
    ├── CommandBuilder.java                  abstract
    ├── CommandContext.java
    ├── CommandSender.java                   interface
    ├── Argument.java
    └── Args.java                            factory

commands-bukkit/                              [new module]
├── pom.xml
└── src/main/java/it/mycraft/powerlib/commands/bukkit/
    ├── BukkitCommandBuilder.java
    ├── PaperBrigadierAdapter.java           reflection
    └── BukkitCommandFallback.java           CommandExecutor + TabCompleter

commands-bungee/                              [new module]
└── (similar layout)

commands-velocity/                            [new module]
└── (similar layout)

tests/                                        [new module, deploy disabled]
├── pom.xml
└── src/test/java/it/mycraft/powerlib/...     test classes per topic
```

### Modified files (besides those listed above)
```
pom.xml                       [modify] modules, lombok bump, version
bukkit/pom.xml                [modify] Nexo dep + repos, NBT-API bump
bukkit-plugin/pom.xml         [modify] lombok bump
bukkit-plugin/src/main/resources/plugin.yml   [modify] softdepend: [Nexo], version
bungee-plugin/pom.xml         [modify] lombok bump
all/pom.xml                   [modify] add new modules (except components)
README.md                     [modify] new modules, compatibility matrix
```

---

## Task 1: Cleanup repo and add Maven Wrapper

**Files:**
- Modify: `.gitignore`
- Create: `mvnw`, `mvnw.cmd`, `.mvn/wrapper/maven-wrapper.properties`, `.mvn/wrapper/maven-wrapper.jar`
- Untrack (if present): any `*.iml`, `**/target/**`, `.idea/**`

- [ ] **Step 1: Replace `.gitignore`**

Replace the entire contents of `.gitignore` with:
```gitignore
# Compiled class file
*.class

# Log file
*.log

# BlueJ files
*.ctxt

# Mobile Tools for Java (J2ME)
.mtj.tmp/

# Package Files #
*.jar
*.war
*.nar
*.ear
*.zip
*.tar.gz
*.rar

# JVM crash logs
hs_err_pid*

# Maven build output
target/
dependency-reduced-pom.xml

# IDE
.idea/
*.iml
*.iws
*.ipr
.vscode/
.settings/
.project
.classpath

# Anti-regression: nested wrapper artefacts seen in the GitLab fork
.mvn/.mvn/
.mvn/.mvn/.mvn/

# OS
.DS_Store
Thumbs.db
```

- [ ] **Step 2: Generate Maven Wrapper**

Run from project root (replace your local Maven 3.9.x as appropriate):
```bash
mvn -N io.takari:maven:wrapper -Dmaven=3.9.9
```
Expected: creates `mvnw`, `mvnw.cmd`, `.mvn/wrapper/maven-wrapper.properties`, `.mvn/wrapper/maven-wrapper.jar`. If the takari plugin is unavailable, fall back to `mvn wrapper:wrapper -Dmaven=3.9.9`.

- [ ] **Step 3: Untrack any artefacts already committed**

```bash
git rm -r --cached --ignore-unmatch .idea
git rm --cached --ignore-unmatch all/powerlib-all.iml bukkit/powerlib-bukkit.iml bukkit-plugin/powerlib-bukkit-plugin.iml bungee/powerlib-bungee.iml bungee-plugin/powerlib-bungee-plugin.iml common/powerlib-common.iml velocity/powerlib-velocity.iml velocity-plugin/powerlib-velocity-plugin.iml
git rm -r --cached --ignore-unmatch all/target bukkit/target bukkit-plugin/target bungee/target bungee-plugin/target common/target velocity/target velocity-plugin/target
```
Expected: each command either removes a tracked file or prints nothing if absent. The `--ignore-unmatch` keeps it from failing on absent paths.

- [ ] **Step 4: Verify status**

```bash
git status --short
```
Expected: shows `.gitignore` modified, new wrapper files, and untracked artefacts gone.

- [ ] **Step 5: Commit**

```bash
git add .gitignore mvnw mvnw.cmd .mvn
git commit -m "chore: ignore build artifacts and add maven wrapper"
```

---

## Task 2: Bump Lombok 1.18.22 → 1.18.30

**Files:**
- Modify: `pom.xml`, `bukkit-plugin/pom.xml`, `bungee-plugin/pom.xml`

- [ ] **Step 1: Edit root `pom.xml`**

Find:
```xml
            <version>1.18.22</version>
```
inside the `<dependency><artifactId>lombok</artifactId>` block, change to:
```xml
            <version>1.18.30</version>
```

- [ ] **Step 2: Edit `bukkit-plugin/pom.xml`**

Find:
```xml
                            <version>1.18.22</version>
```
inside the `<annotationProcessorPaths>` lombok path, change to `1.18.30`.

- [ ] **Step 3: Edit `bungee-plugin/pom.xml`**

Same change as Step 2.

- [ ] **Step 4: Build to verify**

```bash
./mvnw clean install -DskipTests
```
Expected: `BUILD SUCCESS` for all modules.

- [ ] **Step 5: Commit**

```bash
git add pom.xml bukkit-plugin/pom.xml bungee-plugin/pom.xml
git commit -m "chore: bump lombok 1.18.22 -> 1.18.30"
```

---

## Task 3: Bump NBT-API 2.14.2-SNAPSHOT → 2.15.5

**Files:**
- Modify: `bukkit/pom.xml`

- [ ] **Step 1: Edit `bukkit/pom.xml`**

Find:
```xml
        <dependency>
            <groupId>de.tr7zw</groupId>
            <artifactId>item-nbt-api</artifactId>
            <version>2.14.2-SNAPSHOT</version>
            <scope>compile</scope>
        </dependency>
```
Change `<version>` to `2.15.5`.

- [ ] **Step 2: Build to verify**

```bash
./mvnw -pl bukkit -am clean install -DskipTests
```
Expected: `BUILD SUCCESS`. If 2.15.5 introduces incompatible API changes (unlikely; tr7zw's library has a stable surface), revert to `2.14.2` (a release version) and document in the commit message.

- [ ] **Step 3: Commit**

```bash
git add bukkit/pom.xml
git commit -m "chore: bump nbt-api 2.14.2-SNAPSHOT -> 2.15.5"
```

---

## Task 4: Scaffold `tests` module

**Files:**
- Create: `tests/pom.xml`, `tests/src/test/java/it/mycraft/powerlib/SmokeTest.java`
- Modify: root `pom.xml` (add `<module>tests</module>`)

- [ ] **Step 1: Create `tests/pom.xml`**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <groupId>it.mycraft</groupId>
        <artifactId>powerlib</artifactId>
        <version>1.2.16-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>powerlib-tests</artifactId>

    <properties>
        <junit.version>5.10.2</junit.version>
        <mockito.version>5.11.0</mockito.version>
        <mockbukkit.version>3.91.1</mockbukkit.version>
        <assertj.version>3.25.3</assertj.version>
    </properties>

    <repositories>
        <repository>
            <id>spigot-repo</id>
            <url>https://hub.spigotmc.org/nexus/content/repositories/snapshots/</url>
        </repository>
        <repository>
            <id>codemc-repo</id>
            <url>https://repo.codemc.org/repository/maven-public/</url>
        </repository>
    </repositories>

    <dependencies>
        <dependency>
            <groupId>it.mycraft</groupId>
            <artifactId>powerlib-common</artifactId>
            <version>${project.parent.version}</version>
        </dependency>
        <dependency>
            <groupId>it.mycraft</groupId>
            <artifactId>powerlib-bukkit</artifactId>
            <version>${project.parent.version}</version>
        </dependency>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <version>${junit.version}</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.mockito</groupId>
            <artifactId>mockito-core</artifactId>
            <version>${mockito.version}</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.mockito</groupId>
            <artifactId>mockito-junit-jupiter</artifactId>
            <version>${mockito.version}</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.assertj</groupId>
            <artifactId>assertj-core</artifactId>
            <version>${assertj.version}</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>com.github.seeseemelk</groupId>
            <artifactId>MockBukkit-v1.20</artifactId>
            <version>${mockbukkit.version}</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.spigotmc</groupId>
            <artifactId>spigot-api</artifactId>
            <version>1.20.4-R0.1-SNAPSHOT</version>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>3.2.5</version>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-deploy-plugin</artifactId>
                <version>3.1.1</version>
                <configuration>
                    <skip>true</skip>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

> Note on MockBukkit version: `MockBukkit-v1.20` covers MC 1.20.x and is sufficient for `RegistryCompat` cache testing because the tests inject the Enchantment / PotionEffectType directly. If a specific test needs a 1.21 server-mock, switch to `MockBukkit-v1.21` for that test class only.

- [ ] **Step 2: Add the module in root `pom.xml`**

In root `pom.xml`'s `<modules>` block, append after `<module>all</module>`:
```xml
        <module>tests</module>
```

- [ ] **Step 3: Create the smoke test**

`tests/src/test/java/it/mycraft/powerlib/SmokeTest.java`:
```java
package it.mycraft.powerlib;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SmokeTest {

    @Test
    void junitIsWorking() {
        assertThat(1 + 1).isEqualTo(2);
    }
}
```

- [ ] **Step 4: Run the smoke test**

```bash
./mvnw -pl tests -am test
```
Expected: `BUILD SUCCESS`, 1 test passed.

- [ ] **Step 5: Commit**

```bash
git add pom.xml tests
git commit -m "test: scaffold tests module with junit5, mockito, mockbukkit, assertj"
```

---

## Task 5: Add `RegistryCompat` and re-route `LegacyPotionAPI`/`ItemBuilder`

**Files:**
- Create: `bukkit/src/main/java/it/mycraft/powerlib/bukkit/compat/RegistryCompat.java`
- Modify: `bukkit/src/main/java/it/mycraft/powerlib/bukkit/item/LegacyPotionAPI.java`
- Modify: `bukkit/src/main/java/it/mycraft/powerlib/bukkit/item/ItemBuilder.java`
- Test: `tests/src/test/java/it/mycraft/powerlib/bukkit/compat/RegistryCompatTest.java`

- [ ] **Step 1: Write the failing test**

Create `tests/src/test/java/it/mycraft/powerlib/bukkit/compat/RegistryCompatTest.java`:
```java
package it.mycraft.powerlib.bukkit.compat;

import be.seeseemelk.mockbukkit.MockBukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionEffectType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RegistryCompatTest {

    @BeforeEach
    void setUp() {
        MockBukkit.mock();
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void glowEnchantReturnsAnEnchantment() {
        Enchantment glow = RegistryCompat.glowEnchant();
        assertThat(glow).isNotNull();
    }

    @Test
    void potionEffectStrengthResolves() {
        PotionEffectType strength = RegistryCompat.potionEffect("strength");
        assertThat(strength).isNotNull();
    }

    @Test
    void potionEffectSlownessResolves() {
        assertThat(RegistryCompat.potionEffect("slowness")).isNotNull();
    }

    @Test
    void potionEffectJumpBoostResolves() {
        assertThat(RegistryCompat.potionEffect("jump_boost")).isNotNull();
    }

    @Test
    void potionEffectInstantDamageResolves() {
        assertThat(RegistryCompat.potionEffect("instant_damage")).isNotNull();
    }

    @Test
    void enchantmentLookupIsCached() {
        Enchantment first = RegistryCompat.enchantment("unbreaking");
        Enchantment second = RegistryCompat.enchantment("unbreaking");
        assertThat(first).isSameAs(second);
    }
}
```

- [ ] **Step 2: Run test, verify FAIL**

```bash
./mvnw -pl tests test -Dtest=RegistryCompatTest
```
Expected: compile failure ("cannot find symbol RegistryCompat").

- [ ] **Step 3: Create `RegistryCompat`**

`bukkit/src/main/java/it/mycraft/powerlib/bukkit/compat/RegistryCompat.java`:
```java
package it.mycraft.powerlib.bukkit.compat;

import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionEffectType;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Resolves Enchantment and PotionEffectType constants by canonical key,
 * supporting both pre-1.20.5 names (DURABILITY, INCREASE_DAMAGE, ...) and
 * post-1.20.5 names (UNBREAKING, STRENGTH, ...).
 *
 * Lookups are cached after first resolution. Caller passes the *canonical*
 * minecraft key (e.g. "unbreaking", "strength", "slowness", "jump_boost",
 * "instant_damage") and gets back whatever the running server exposes.
 */
public final class RegistryCompat {

    private static final Logger LOGGER = Logger.getLogger(RegistryCompat.class.getName());

    private static final Map<String, Enchantment> ENCHANT_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, PotionEffectType> POTION_CACHE = new ConcurrentHashMap<>();

    /** Mapping from canonical key to legacy field name(s) — first match wins. */
    private static final Map<String, String[]> ENCHANT_LEGACY = Map.of(
            "unbreaking", new String[]{"UNBREAKING", "DURABILITY"}
    );

    private static final Map<String, String[]> POTION_LEGACY = Map.of(
            "strength",        new String[]{"STRENGTH",        "INCREASE_DAMAGE"},
            "slowness",        new String[]{"SLOWNESS",        "SLOW"},
            "jump_boost",      new String[]{"JUMP_BOOST",      "JUMP"},
            "instant_damage",  new String[]{"INSTANT_DAMAGE",  "HARM"},
            "instant_health",  new String[]{"INSTANT_HEALTH",  "HEAL"}
    );

    private RegistryCompat() {}

    public static Enchantment glowEnchant() {
        return enchantment("unbreaking");
    }

    public static Enchantment enchantment(String canonicalKey) {
        return ENCHANT_CACHE.computeIfAbsent(canonicalKey, RegistryCompat::resolveEnchant);
    }

    public static PotionEffectType potionEffect(String canonicalKey) {
        return POTION_CACHE.computeIfAbsent(canonicalKey, RegistryCompat::resolvePotion);
    }

    private static Enchantment resolveEnchant(String key) {
        // 1) Modern Registry API (1.20.x+)
        try {
            Enchantment fromRegistry = Registry.ENCHANTMENT.get(NamespacedKey.minecraft(key));
            if (fromRegistry != null) return fromRegistry;
        } catch (Throwable ignored) {
            // Registry.ENCHANTMENT might not exist on very old versions.
        }
        // 2) Enchantment.getByKey (1.13+)
        try {
            Enchantment byKey = Enchantment.getByKey(NamespacedKey.minecraft(key));
            if (byKey != null) return byKey;
        } catch (Throwable ignored) {
        }
        // 3) Reflection on legacy field names
        String[] candidates = ENCHANT_LEGACY.getOrDefault(key, new String[]{key.toUpperCase()});
        for (String fieldName : candidates) {
            try {
                Field f = Enchantment.class.getField(fieldName);
                Object value = f.get(null);
                if (value instanceof Enchantment) {
                    return (Enchantment) value;
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                LOGGER.log(Level.FINE, "Enchantment field " + fieldName + " not found", e);
            }
        }
        throw new IllegalStateException("Cannot resolve Enchantment for key: " + key);
    }

    private static PotionEffectType resolvePotion(String key) {
        // 1) Modern getByKey (1.20.x+ supports keys for most types)
        try {
            PotionEffectType byKey = PotionEffectType.getByKey(NamespacedKey.minecraft(key));
            if (byKey != null) return byKey;
        } catch (Throwable ignored) {
        }
        // 2) Reflection on legacy + new field names
        String[] candidates = POTION_LEGACY.getOrDefault(key, new String[]{key.toUpperCase()});
        for (String fieldName : candidates) {
            try {
                Field f = PotionEffectType.class.getField(fieldName);
                Object value = f.get(null);
                if (value instanceof PotionEffectType) {
                    return (PotionEffectType) value;
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                LOGGER.log(Level.FINE, "PotionEffectType field " + fieldName + " not found", e);
            }
        }
        // 3) getByName fallback (deprecated but surviving)
        for (String fieldName : candidates) {
            PotionEffectType byName = PotionEffectType.getByName(fieldName);
            if (byName != null) return byName;
        }
        throw new IllegalStateException("Cannot resolve PotionEffectType for key: " + key);
    }
}
```

- [ ] **Step 4: Run tests, verify PASS**

```bash
./mvnw -pl bukkit -am clean install -DskipTests
./mvnw -pl tests test -Dtest=RegistryCompatTest
```
Expected: 6 tests passed.

- [ ] **Step 5: Reroute `LegacyPotionAPI` to use canonical keys**

Open `bukkit/src/main/java/it/mycraft/powerlib/bukkit/item/LegacyPotionAPI.java`. The current enum constants reference `PotionEffectType.INCREASE_DAMAGE`, `SLOW`, `JUMP`, `HARM`. Refactor:

1. Change the constructor signature to accept a *string canonical key* instead of a `PotionEffectType`:
   ```java
   // Before:
   LegacyPotionAPI(String name, int id, int data, boolean extended, int amplifier, boolean splash, PotionEffectType type, int duration, String displayName) { ... }
   // After:
   LegacyPotionAPI(String name, int id, int data, boolean extended, int amplifier, boolean splash, String canonicalKey, int duration, String displayName) { ... }
   ```
2. Store `canonicalKey` instead of `type`. Add a getter that resolves lazily:
   ```java
   public PotionEffectType getType() {
       return RegistryCompat.potionEffect(canonicalKey);
   }
   ```
3. Update each enum entry. Mapping table:
    - `PotionEffectType.INCREASE_DAMAGE` → `"strength"`
    - `PotionEffectType.SLOW` → `"slowness"`
    - `PotionEffectType.JUMP` → `"jump_boost"`
    - `PotionEffectType.HARM` → `"instant_damage"`
    - `PotionEffectType.HEAL` → `"instant_health"`
    - All other `PotionEffectType.X` → `"x_lowercase"` (e.g. `WATER_BREATHING` → `"water_breathing"`)
4. Add `import it.mycraft.powerlib.bukkit.compat.RegistryCompat;`. Remove `import org.bukkit.potion.PotionEffectType;` if `getType` is the only use.

- [ ] **Step 6: Reroute `ItemBuilder.build()` glow**

In `bukkit/src/main/java/it/mycraft/powerlib/bukkit/item/ItemBuilder.java`, find:
```java
            if (glowing && enchantments.isEmpty()) {
                itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
```
Replace `Enchantment.DURABILITY` with `RegistryCompat.glowEnchant()`. Add import:
```java
import it.mycraft.powerlib.bukkit.compat.RegistryCompat;
```

- [ ] **Step 7: Build and run all tests**

```bash
./mvnw clean install -DskipTests
./mvnw -pl tests test
```
Expected: `BUILD SUCCESS`, all tests pass.

- [ ] **Step 8: Commit**

```bash
git add bukkit/src/main/java/it/mycraft/powerlib/bukkit/compat/RegistryCompat.java \
        bukkit/src/main/java/it/mycraft/powerlib/bukkit/item/LegacyPotionAPI.java \
        bukkit/src/main/java/it/mycraft/powerlib/bukkit/item/ItemBuilder.java \
        tests/src/test/java/it/mycraft/powerlib/bukkit/compat/RegistryCompatTest.java
git commit -m "feat(bukkit): add RegistryCompat for renamed enchant/potion constants"
```

---

## Task 6: Extract `ItemsAdderBridge` and harden ItemsAdder integration

**Files:**
- Create: `bukkit/src/main/java/it/mycraft/powerlib/bukkit/compat/ItemsAdderBridge.java`
- Modify: `bukkit/src/main/java/it/mycraft/powerlib/bukkit/item/ItemBuilder.java`
- Test: `tests/src/test/java/it/mycraft/powerlib/bukkit/compat/ItemsAdderBridgeTest.java`

- [ ] **Step 1: Write the failing test**

`tests/src/test/java/it/mycraft/powerlib/bukkit/compat/ItemsAdderBridgeTest.java`:
```java
package it.mycraft.powerlib.bukkit.compat;

import be.seeseemelk.mockbukkit.MockBukkit;
import it.mycraft.powerlib.common.objects.Pair;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class ItemsAdderBridgeTest {

    @BeforeEach
    void setUp() { MockBukkit.mock(); }

    @AfterEach
    void tearDown() { MockBukkit.unmock(); }

    @Test
    void isAvailableReturnsFalseWhenItemsAdderPluginNotLoaded() {
        assertThat(ItemsAdderBridge.isAvailable()).isFalse();
    }

    @Test
    void extractDataReturnsEmptyWhenItemsAdderUnavailable() {
        ItemStack stack = new ItemStack(Material.STONE);
        Optional<Pair<String,String>> data = ItemsAdderBridge.extractData(stack);
        assertThat(data).isEmpty();
    }

    @Test
    void buildItemReturnsEmptyWhenItemsAdderUnavailable() {
        Optional<ItemStack> built = ItemsAdderBridge.buildItem("namespace:id", 1);
        assertThat(built).isEmpty();
    }
}
```

- [ ] **Step 2: Run test, verify FAIL**

```bash
./mvnw -pl tests test -Dtest=ItemsAdderBridgeTest
```
Expected: compile error, `ItemsAdderBridge` not found.

- [ ] **Step 3: Create `ItemsAdderBridge`**

`bukkit/src/main/java/it/mycraft/powerlib/bukkit/compat/ItemsAdderBridge.java`:
```java
package it.mycraft.powerlib.bukkit.compat;

import it.mycraft.powerlib.common.objects.Pair;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Single home for ItemsAdder reflection. Falls back gracefully when ItemsAdder
 * is not present on the server.
 */
public final class ItemsAdderBridge {

    private static final Logger LOGGER = Logger.getLogger(ItemsAdderBridge.class.getName());

    private static volatile Boolean availableCache;
    private static Class<?> customStackClass;
    private static Method byItemStackMethod;
    private static Method getNamespacedIDMethod;
    private static Method customStackGetItemStackMethod;
    private static Method getInstanceMethod;

    static {
        try {
            customStackClass = Class.forName("dev.lone.itemsadder.api.CustomStack");
            byItemStackMethod = customStackClass.getMethod("byItemStack", ItemStack.class);
            getNamespacedIDMethod = customStackClass.getMethod("getNamespacedID");
            customStackGetItemStackMethod = customStackClass.getMethod("getItemStack");
            getInstanceMethod = customStackClass.getMethod("getInstance", String.class);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            LOGGER.log(Level.FINE, "ItemsAdder API not on classpath", e);
        }
    }

    private ItemsAdderBridge() {}

    public static boolean isAvailable() {
        Boolean cached = availableCache;
        if (cached != null) return cached;
        boolean enabled = customStackClass != null
                && Bukkit.getPluginManager().isPluginEnabled("ItemsAdder");
        availableCache = enabled;
        return enabled;
    }

    /** Reset the availability cache. Public for tests. */
    public static void resetAvailabilityCache() {
        availableCache = null;
    }

    public static Optional<Pair<String, String>> extractData(ItemStack itemStack) {
        if (!isAvailable() || itemStack == null) return Optional.empty();
        try {
            Object customStack = byItemStackMethod.invoke(null, itemStack);
            if (customStack == null) return Optional.empty();
            String namespacedId = (String) getNamespacedIDMethod.invoke(customStack);
            if (namespacedId == null || !namespacedId.contains(":")) return Optional.empty();
            String[] split = namespacedId.split(":", 2);
            return Optional.of(new Pair<>(split[0], split[1]));
        } catch (ReflectiveOperationException e) {
            LOGGER.log(Level.WARNING, "ItemsAdder extractData failed", e);
            return Optional.empty();
        }
    }

    public static Optional<ItemStack> buildItem(String namespacedId, int amount) {
        if (!isAvailable()) return Optional.empty();
        try {
            Object customStack = getInstanceMethod.invoke(null, namespacedId);
            if (customStack == null) return Optional.empty();
            ItemStack stack = (ItemStack) customStackGetItemStackMethod.invoke(customStack);
            if (stack == null) return Optional.empty();
            stack.setAmount(amount);
            return Optional.of(stack);
        } catch (ReflectiveOperationException e) {
            LOGGER.log(Level.WARNING, "ItemsAdder buildItem failed", e);
            return Optional.empty();
        }
    }
}
```

- [ ] **Step 4: Run tests, verify PASS**

```bash
./mvnw -pl bukkit -am clean install -DskipTests
./mvnw -pl tests test -Dtest=ItemsAdderBridgeTest
```
Expected: 3 tests passed.

- [ ] **Step 5: Refactor `ItemBuilder.clone(ItemStack)` and `ItemBuilder.build()` to use the bridge**

In `ItemBuilder.java`:

A) In `clone(ItemStack)`, replace the `NBTItem nbtItem = new NBTItem(itemStack); NBTCompound comp = nbtItem.getCompound("itemsadder"); ...` block with:
```java
        ItemsAdderBridge.extractData(itemStack).ifPresent(pair -> {
            this.itemsAdderData = new Pair<>(Optional.of(pair.getLeft()), Optional.of(pair.getRight()));
        });
```

B) In `build()`, find the existing `if (material.startsWith("itemsadder:") && isUsingItemsAdder())` branch (or wherever the IA path constructs the stack — exact line varies by current code state). Replace its body with:
```java
                String customItem = material.substring("itemsadder:".length());
                Optional<ItemStack> built = ItemsAdderBridge.buildItem(customItem, amount);
                itemStack = built.orElseGet(() -> new ItemStack(Material.BARRIER, amount, metadata));
```

C) Replace `isUsingItemsAdder()` method body to delegate:
```java
    public static boolean isUsingItemsAdder() {
        return ItemsAdderBridge.isAvailable();
    }
```

D) Add import:
```java
import it.mycraft.powerlib.bukkit.compat.ItemsAdderBridge;
```

E) Keep the post-build NBT compound write inside a try/catch that logs:
```java
        if (itemsAdderData.getLeft().isPresent() && itemsAdderData.getRight().isPresent()) {
            try {
                NBTItem nbtItem = new NBTItem(itemStack);
                NBTCompound comp = nbtItem.getOrCreateCompound("itemsadder");
                comp.setString("namespace", itemsAdderData.getLeft().get());
                comp.setString("id", itemsAdderData.getRight().get());
                nbtItem.applyNBT(itemStack);
            } catch (Exception e) {
                Bukkit.getLogger().warning("PowerLib: failed to apply ItemsAdder NBT: " + e.getMessage());
            }
        }
```

- [ ] **Step 6: Verify build and tests**

```bash
./mvnw clean install -DskipTests
./mvnw -pl tests test
```
Expected: `BUILD SUCCESS`, tests green.

- [ ] **Step 7: Commit**

```bash
git add bukkit/src/main/java/it/mycraft/powerlib/bukkit/compat/ItemsAdderBridge.java \
        bukkit/src/main/java/it/mycraft/powerlib/bukkit/item/ItemBuilder.java \
        tests/src/test/java/it/mycraft/powerlib/bukkit/compat/ItemsAdderBridgeTest.java
git commit -m "feat(bukkit): extract ItemsAdderBridge and harden ItemsAdder integration"
```

---

## Task 7: Add `ItemBuilder.setPersistentData` via `PersistentDataApplier`

**Files:**
- Create: `bukkit/src/main/java/it/mycraft/powerlib/bukkit/item/applier/PersistentEntry.java`
- Create: `bukkit/src/main/java/it/mycraft/powerlib/bukkit/item/applier/PersistentDataApplier.java`
- Modify: `bukkit/src/main/java/it/mycraft/powerlib/bukkit/item/ItemBuilder.java`
- Test: `tests/src/test/java/it/mycraft/powerlib/bukkit/item/PersistentDataTest.java`

- [ ] **Step 1: Write the failing test**

```java
package it.mycraft.powerlib.bukkit.item;

import be.seeseemelk.mockbukkit.MockBukkit;
import it.mycraft.powerlib.bukkit.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PersistentDataTest {

    private Plugin plugin;

    @BeforeEach
    void setUp() {
        MockBukkit.mock();
        plugin = MockBukkit.createMockPlugin("PowerLibTest");
    }

    @AfterEach
    void tearDown() { MockBukkit.unmock(); }

    @Test
    void setPersistentDataRoundTrip() {
        NamespacedKey key = new NamespacedKey(plugin, "owner");

        ItemStack stack = new ItemBuilder()
                .setMaterial(Material.DIAMOND_SWORD)
                .setPersistentData(key, PersistentDataType.STRING, "alice")
                .build();

        String value = stack.getItemMeta().getPersistentDataContainer()
                .get(key, PersistentDataType.STRING);
        assertThat(value).isEqualTo("alice");
    }

    @Test
    void multiplePersistentDataKeysCoexist() {
        NamespacedKey nameKey = new NamespacedKey(plugin, "name");
        NamespacedKey countKey = new NamespacedKey(plugin, "count");

        ItemStack stack = new ItemBuilder()
                .setMaterial(Material.DIAMOND_SWORD)
                .setPersistentData(nameKey, PersistentDataType.STRING, "excalibur")
                .setPersistentData(countKey, PersistentDataType.INTEGER, 42)
                .build();

        var pdc = stack.getItemMeta().getPersistentDataContainer();
        assertThat(pdc.get(nameKey, PersistentDataType.STRING)).isEqualTo("excalibur");
        assertThat(pdc.get(countKey, PersistentDataType.INTEGER)).isEqualTo(42);
    }
}
```

- [ ] **Step 2: Run test, verify FAIL**

```bash
./mvnw -pl tests test -Dtest=PersistentDataTest
```
Expected: compile failure (`setPersistentData` not found on `ItemBuilder`).

- [ ] **Step 3: Create `PersistentEntry`**

`bukkit/src/main/java/it/mycraft/powerlib/bukkit/item/applier/PersistentEntry.java`:
```java
package it.mycraft.powerlib.bukkit.item.applier;

import org.bukkit.persistence.PersistentDataType;

/**
 * Type-safe pairing of a PersistentDataType and its value.
 * The wildcard erasure on the map side is unavoidable; this record keeps the
 * generics tight at construction site.
 */
public record PersistentEntry<T, Z>(PersistentDataType<T, Z> type, Z value) {
}
```

- [ ] **Step 4: Create `PersistentDataApplier`**

`bukkit/src/main/java/it/mycraft/powerlib/bukkit/item/applier/PersistentDataApplier.java`:
```java
package it.mycraft.powerlib.bukkit.item.applier;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Map;

public final class PersistentDataApplier {

    private PersistentDataApplier() {}

    public static void apply(ItemMeta meta, Map<NamespacedKey, PersistentEntry<?, ?>> data) {
        if (meta == null || data == null || data.isEmpty()) return;
        PersistentDataContainer container = meta.getPersistentDataContainer();
        for (Map.Entry<NamespacedKey, PersistentEntry<?, ?>> e : data.entrySet()) {
            applyOne(container, e.getKey(), e.getValue());
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void applyOne(PersistentDataContainer container, NamespacedKey key, PersistentEntry<?, ?> entry) {
        // Erasure: the value's runtime type matches the type token by construction.
        PersistentDataType type = entry.type();
        container.set(key, type, entry.value());
    }
}
```

- [ ] **Step 5: Modify `ItemBuilder`**

In `ItemBuilder.java`:

A) Add field next to other state:
```java
    private final Map<NamespacedKey, PersistentEntry<?, ?>> persistentData = new LinkedHashMap<>();
```

B) Add public method (anywhere in the builder API surface, suggest near `setCustomModelData`):
```java
    /**
     * Stores a typed persistent data entry that will be applied to the
     * built item's PersistentDataContainer.
     */
    public <T, Z> ItemBuilder setPersistentData(NamespacedKey key, PersistentDataType<T, Z> type, Z value) {
        persistentData.put(key, new PersistentEntry<>(type, value));
        return this;
    }
```

C) In `build()`, after the `customModelData` block and before `itemStack.setItemMeta(itemMeta)`, add:
```java
            PersistentDataApplier.apply(itemMeta, persistentData);
```

D) Add imports:
```java
import it.mycraft.powerlib.bukkit.item.applier.PersistentDataApplier;
import it.mycraft.powerlib.bukkit.item.applier.PersistentEntry;
import java.util.LinkedHashMap;
```

- [ ] **Step 6: Run tests, verify PASS**

```bash
./mvnw -pl bukkit -am clean install -DskipTests
./mvnw -pl tests test -Dtest=PersistentDataTest
```
Expected: 2 tests passed.

- [ ] **Step 7: Commit**

```bash
git add bukkit/src/main/java/it/mycraft/powerlib/bukkit/item/applier/ \
        bukkit/src/main/java/it/mycraft/powerlib/bukkit/item/ItemBuilder.java \
        tests/src/test/java/it/mycraft/powerlib/bukkit/item/PersistentDataTest.java
git commit -m "feat(bukkit): add ItemBuilder.setPersistentData via PersistentDataApplier"
```

---

## Task 8: Add Nexo bridge

**Files:**
- Create: `bukkit/src/main/java/it/mycraft/powerlib/bukkit/compat/NexoSupport.java`
- Create: `bukkit/src/main/java/it/mycraft/powerlib/bukkit/utils/NexoUtils.java`
- Create: `bukkit/src/main/java/it/mycraft/powerlib/bukkit/events/NexoFurnitureInteractEvent.java`
- Create: `bukkit/src/main/java/it/mycraft/powerlib/bukkit/listeners/NexoListener.java`
- Modify: `bukkit/src/main/java/it/mycraft/powerlib/bukkit/PowerLib.java`
- Modify: `bukkit/src/main/java/it/mycraft/powerlib/bukkit/item/ItemBuilder.java`
- Modify: `bukkit/pom.xml` (Nexo dep + repos)
- Modify: `bukkit-plugin/src/main/resources/plugin.yml` (`softdepend: [Nexo]`)
- Test: `tests/src/test/java/it/mycraft/powerlib/bukkit/compat/NexoSupportTest.java`

- [ ] **Step 1: Update `bukkit/pom.xml` (repos + dep)**

In `<repositories>` add:
```xml
        <repository>
            <id>nexomc-repo</id>
            <url>https://repo.nexomc.com/releases</url>
        </repository>
        <repository>
            <id>alessiodp-repo</id>
            <url>https://repo.alessiodp.com/releases/</url>
        </repository>
```
In `<dependencies>` add (as the first dependency, before `powerlib-common`):
```xml
        <dependency>
            <groupId>com.nexomc</groupId>
            <artifactId>nexo</artifactId>
            <version>1.0.0</version>
            <scope>provided</scope>
        </dependency>
```

- [ ] **Step 2: Update `plugin.yml`**

`bukkit-plugin/src/main/resources/plugin.yml` (final content):
```yaml
name: PowerLib
authors: [AlbeMiglio, FranFrau]
main: it.mycraft.powerlib.bukkit.PowerLibPlugin
version: 1.2.16-SNAPSHOT
softdepend: [Nexo]
```

> Do NOT add `api-version`. We keep compat with pre-1.13 plugin format consumers.

- [ ] **Step 3: Write the failing test**

`tests/src/test/java/it/mycraft/powerlib/bukkit/compat/NexoSupportTest.java`:
```java
package it.mycraft.powerlib.bukkit.compat;

import be.seeseemelk.mockbukkit.MockBukkit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NexoSupportTest {

    @BeforeEach
    void setUp() { MockBukkit.mock(); NexoSupport.resetAvailabilityCache(); }

    @AfterEach
    void tearDown() { MockBukkit.unmock(); }

    @Test
    void isAvailableFalseWhenNexoNotLoaded() {
        assertThat(NexoSupport.isAvailable()).isFalse();
    }

    @Test
    void buildItemReturnsEmptyWhenNexoNotLoaded() {
        assertThat(NexoSupport.buildItem("test", 1)).isEmpty();
    }
}
```

- [ ] **Step 4: Run test, verify FAIL**

```bash
./mvnw -pl tests test -Dtest=NexoSupportTest
```
Expected: compile failure.

- [ ] **Step 5: Create `NexoSupport`**

`bukkit/src/main/java/it/mycraft/powerlib/bukkit/compat/NexoSupport.java`:
```java
package it.mycraft.powerlib.bukkit.compat;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class NexoSupport {

    private static final Logger LOGGER = Logger.getLogger(NexoSupport.class.getName());

    private static volatile Boolean availableCache;
    private static Class<?> nexoItemsClass;
    private static Method itemFromIdMethod;
    private static Method nexoItemBuilderBuildMethod;

    static {
        try {
            nexoItemsClass = Class.forName("com.nexomc.nexo.api.NexoItems");
            itemFromIdMethod = nexoItemsClass.getMethod("itemFromId", String.class);
            // Returned object's #build() method
            Class<?> nexoItemBuilderClass = Class.forName("com.nexomc.nexo.items.ItemBuilder");
            nexoItemBuilderBuildMethod = nexoItemBuilderClass.getMethod("build");
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            LOGGER.log(Level.FINE, "Nexo API not on classpath", e);
        }
    }

    private NexoSupport() {}

    public static boolean isAvailable() {
        Boolean cached = availableCache;
        if (cached != null) return cached;
        boolean enabled = nexoItemsClass != null
                && Bukkit.getPluginManager().isPluginEnabled("Nexo");
        availableCache = enabled;
        return enabled;
    }

    public static void resetAvailabilityCache() {
        availableCache = null;
    }

    public static Optional<ItemStack> buildItem(String nexoId, int amount) {
        if (!isAvailable()) return Optional.empty();
        try {
            Object nexoBuilder = itemFromIdMethod.invoke(null, nexoId);
            if (nexoBuilder == null) return Optional.empty();
            ItemStack stack = (ItemStack) nexoItemBuilderBuildMethod.invoke(nexoBuilder);
            if (stack == null) return Optional.empty();
            stack.setAmount(amount);
            return Optional.of(stack);
        } catch (ReflectiveOperationException e) {
            LOGGER.log(Level.WARNING, "Nexo buildItem failed for id=" + nexoId, e);
            return Optional.empty();
        }
    }
}
```

- [ ] **Step 6: Create `NexoUtils`**

`bukkit/src/main/java/it/mycraft/powerlib/bukkit/utils/NexoUtils.java`:
```java
package it.mycraft.powerlib.bukkit.utils;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class NexoUtils {

    private static final Logger LOGGER = Logger.getLogger(NexoUtils.class.getName());

    private static Method nexoFurnFromEntity;
    private static Method nexoFurnFromBlock;
    private static Method nexoBlockFromBlock;
    private static Method nexoIdFromItem;

    static {
        nexoFurnFromEntity = safeMethod("com.nexomc.nexo.api.NexoFurniture", "furnitureFromEntity", Entity.class);
        nexoFurnFromBlock  = firstNonNull(
                safeMethod("com.nexomc.nexo.api.NexoFurniture", "furnitureMechanic", Block.class),
                safeMethod("com.nexomc.nexo.api.NexoFurniture", "furnitureFromBlock", Block.class)
        );
        nexoBlockFromBlock = safeMethod("com.nexomc.nexo.api.NexoBlocks", "blockFromBlock", Block.class);
        nexoIdFromItem     = safeMethod("com.nexomc.nexo.api.NexoItems", "idFromItem", ItemStack.class);
    }

    private NexoUtils() {}

    public static String getNexoId(Block block) {
        if (block == null) return null;
        String id = invokeIdFromMechanic(nexoBlockFromBlock, block);
        if (id != null) return id;
        return invokeIdFromMechanic(nexoFurnFromBlock, block);
    }

    public static String getNexoId(Entity entity) {
        if (entity == null) return null;
        return invokeIdFromMechanic(nexoFurnFromEntity, entity);
    }

    public static String getNexoId(ItemStack item) {
        if (nexoIdFromItem == null || item == null || item.getType().isAir()) return null;
        try {
            return (String) nexoIdFromItem.invoke(null, item);
        } catch (ReflectiveOperationException e) {
            LOGGER.log(Level.FINE, "Nexo idFromItem reflection failed", e);
            return null;
        }
    }

    public static boolean isNexoItem(ItemStack item, String nexoId) {
        String id = getNexoId(item);
        return id != null && id.equalsIgnoreCase(nexoId);
    }

    private static String invokeIdFromMechanic(Method staticAccessor, Object arg) {
        if (staticAccessor == null) return null;
        try {
            Object mechanic = staticAccessor.invoke(null, arg);
            if (mechanic == null) return null;
            Method getId = mechanic.getClass().getMethod("getItemID");
            return (String) getId.invoke(mechanic);
        } catch (ReflectiveOperationException e) {
            LOGGER.log(Level.FINE, "Nexo mechanic reflection failed", e);
            return null;
        }
    }

    private static Method safeMethod(String className, String name, Class<?>... params) {
        try {
            return Class.forName(className).getMethod(name, params);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            LOGGER.log(Level.FINE, "Nexo method " + className + "#" + name + " not available", e);
            return null;
        }
    }

    private static Method firstNonNull(Method a, Method b) {
        return a != null ? a : b;
    }
}
```

- [ ] **Step 7: Create `NexoFurnitureInteractEvent`**

`bukkit/src/main/java/it/mycraft/powerlib/bukkit/events/NexoFurnitureInteractEvent.java`:
```java
package it.mycraft.powerlib.bukkit.events;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class NexoFurnitureInteractEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final String furnitureId;
    private final Object nexoFurniture;

    @Setter
    private boolean cancelled;

    public NexoFurnitureInteractEvent(Player player, String furnitureId, Object nexoFurniture) {
        this.player = player;
        this.furnitureId = furnitureId;
        this.nexoFurniture = nexoFurniture;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
```

- [ ] **Step 8: Create `NexoListener`**

`bukkit/src/main/java/it/mycraft/powerlib/bukkit/listeners/NexoListener.java`:
```java
package it.mycraft.powerlib.bukkit.listeners;

import it.mycraft.powerlib.bukkit.events.NexoFurnitureInteractEvent;
import it.mycraft.powerlib.bukkit.utils.NexoUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.Plugin;

import java.util.logging.Level;

public class NexoListener implements Listener {

    private final Plugin plugin;

    public NexoListener(Plugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        plugin.getLogger().info("Nexo Furniture Bridge enabled.");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getClickedBlock() == null) return;

        try {
            String furnitureId = NexoUtils.getNexoId(event.getClickedBlock());
            if (furnitureId == null || furnitureId.isEmpty()) return;

            NexoFurnitureInteractEvent powerEvent =
                    new NexoFurnitureInteractEvent(event.getPlayer(), furnitureId, event.getClickedBlock());
            Bukkit.getPluginManager().callEvent(powerEvent);

            if (powerEvent.isCancelled()) {
                event.setCancelled(true);
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error detecting Nexo block interaction", e);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getRightClicked() == null) return;

        try {
            String furnitureId = NexoUtils.getNexoId(event.getRightClicked());
            if (furnitureId == null || furnitureId.isEmpty()) return;

            NexoFurnitureInteractEvent powerEvent =
                    new NexoFurnitureInteractEvent(event.getPlayer(), furnitureId, event.getRightClicked());
            Bukkit.getPluginManager().callEvent(powerEvent);

            if (powerEvent.isCancelled()) {
                event.setCancelled(true);
            }
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Error detecting Nexo entity interaction", e);
        }
    }
}
```

- [ ] **Step 9: Modify `PowerLib.java`**

Final content:
```java
package it.mycraft.powerlib.bukkit;

import it.mycraft.powerlib.bukkit.compat.NexoSupport;
import it.mycraft.powerlib.bukkit.listeners.NexoListener;
import lombok.NonNull;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.plugin.Plugin;

public class PowerLib {

    private static BukkitAudiences adventure;

    public static void inject(Plugin plugin) {
        adventure = BukkitAudiences.create(plugin);
        new NexoListener(plugin);
    }

    public static boolean isNexoAvailable() {
        return NexoSupport.isAvailable();
    }

    public static @NonNull BukkitAudiences adventure() {
        if (adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return adventure;
    }
}
```

- [ ] **Step 10: Modify `ItemBuilder` for `nexo:` prefix**

A) Replace `isUsingNexo` (or add if absent):
```java
    public static boolean isUsingNexo() {
        return NexoSupport.isAvailable();
    }
```

B) In `setMaterial(String material)`, modify the prefix-aware branch to include `nexo:`:
```java
        if (material.length() > 11 && !material.startsWith("itemsadder:") && !material.startsWith("nexo:")) {
            // existing enum lookup...
```

C) In `build()`, add a `nexo:` branch alongside the `itemsadder:` one:
```java
            } else if (material.startsWith("nexo:")) {
                String customItem = material.substring("nexo:".length());
                Optional<ItemStack> built = NexoSupport.buildItem(customItem, amount);
                itemStack = built.orElseGet(() -> new ItemStack(Material.BARRIER, amount, metadata));
            }
```

D) Imports:
```java
import it.mycraft.powerlib.bukkit.compat.NexoSupport;
```

- [ ] **Step 11: Build and run tests**

```bash
./mvnw clean install -DskipTests
./mvnw -pl tests test
```
Expected: `BUILD SUCCESS`, all tests pass.

- [ ] **Step 12: Commit**

```bash
git add bukkit/pom.xml \
        bukkit/src/main/java/it/mycraft/powerlib/bukkit/compat/NexoSupport.java \
        bukkit/src/main/java/it/mycraft/powerlib/bukkit/utils/NexoUtils.java \
        bukkit/src/main/java/it/mycraft/powerlib/bukkit/events/NexoFurnitureInteractEvent.java \
        bukkit/src/main/java/it/mycraft/powerlib/bukkit/listeners/NexoListener.java \
        bukkit/src/main/java/it/mycraft/powerlib/bukkit/PowerLib.java \
        bukkit/src/main/java/it/mycraft/powerlib/bukkit/item/ItemBuilder.java \
        bukkit-plugin/src/main/resources/plugin.yml \
        tests/src/test/java/it/mycraft/powerlib/bukkit/compat/NexoSupportTest.java
git commit -m "feat(bukkit): add Nexo bridge (event, listener, utils, item prefix)"
```

---

## Task 9: Audience shading-safe + restore Velocity `CommandSource`

**Files:**
- Modify: `common/src/main/java/it/mycraft/powerlib/common/chat/BukkitAudience.java`
- Modify: `common/src/main/java/it/mycraft/powerlib/common/chat/BungeeAudience.java`
- Modify: `common/src/main/java/it/mycraft/powerlib/common/chat/VelocityAudience.java`

- [ ] **Step 1: Edit `BukkitAudience.java`**

Constructor body becomes:
```java
        try {
            String packageName = BukkitAudience.class.getPackage().getName();
            String adapterPackage = packageName.replace(".common.chat", ".bukkit.adapters");
            audienceAdapterClass = Class.forName(adapterPackage + ".AudienceAdapter");
            commandSenderClass = Class.forName("org.bukkit.command.CommandSender");
        } catch (ClassNotFoundException e) {
            sendError();
        }
```

- [ ] **Step 2: Edit `BungeeAudience.java`**

Same shape, with `bungee` and `net.md_5.bungee.api.CommandSender`:
```java
        try {
            String packageName = BungeeAudience.class.getPackage().getName();
            String adapterPackage = packageName.replace(".common.chat", ".bungee.adapters");
            audienceAdapterClass = Class.forName(adapterPackage + ".AudienceAdapter");
            commandSenderClass = Class.forName("net.md_5.bungee.api.CommandSender");
        } catch (ClassNotFoundException e) {
            sendError();
        }
```

- [ ] **Step 3: Edit `VelocityAudience.java`**

```java
        try {
            String packageName = VelocityAudience.class.getPackage().getName();
            String adapterPackage = packageName.replace(".common.chat", ".velocity.adapters");
            audienceAdapterClass = Class.forName(adapterPackage + ".AudienceAdapter");
            commandSenderClass = Class.forName("com.velocitypowered.api.command.CommandSource");
        } catch (ClassNotFoundException e) {
            sendError();
        }
```

> Note: this restores `CommandSource` (which is correct). The fork mistakenly set this to `ProxyServer`.

- [ ] **Step 4: Build to verify**

```bash
./mvnw clean install -DskipTests
```
Expected: `BUILD SUCCESS`.

- [ ] **Step 5: Commit**

```bash
git add common/src/main/java/it/mycraft/powerlib/common/chat/BukkitAudience.java \
        common/src/main/java/it/mycraft/powerlib/common/chat/BungeeAudience.java \
        common/src/main/java/it/mycraft/powerlib/common/chat/VelocityAudience.java
git commit -m "fix(common): make audience adapter package shading-safe and restore Velocity CommandSource"
```

---

## Task 10: Add `powerlib-minimessage` module

**Files:**
- Create: `minimessage/pom.xml`
- Create: `minimessage/src/main/java/it/mycraft/powerlib/minimessage/MiniMessageSupport.java`
- Modify: root `pom.xml` (add module), `all/pom.xml` (add dep)
- Modify: `common/src/main/java/it/mycraft/powerlib/common/chat/Message.java` (add `miniMessage` factory)
- Modify: `bukkit/pom.xml` (depend on `powerlib-minimessage`), `bukkit/src/main/java/it/mycraft/powerlib/bukkit/item/ItemBuilder.java` (add `setNameMini`/`setLoreMini`/`addLoreMini`)
- Test: `tests/src/test/java/it/mycraft/powerlib/minimessage/MiniMessageSupportTest.java`, `tests/pom.xml` (add dep)

> Decision: depending on `powerlib-minimessage` from `powerlib-bukkit` brings `adventure-text-minimessage` into the bukkit consumer's classpath transitively. That's an additive change — `bukkit` already brings Adventure. If you want a *zero-transitive* approach, drop the `setNameMini`/`addLoreMini` methods from `ItemBuilder` and require consumers to use `MiniMessageSupport.parseLegacy(...)` themselves before passing to `setName`/`addLore`. The plan takes the transitive path because it's better DX.

- [ ] **Step 1: Create `minimessage/pom.xml`**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <groupId>it.mycraft</groupId>
        <artifactId>powerlib</artifactId>
        <version>1.2.16-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>powerlib-minimessage</artifactId>

    <dependencies>
        <dependency>
            <groupId>it.mycraft</groupId>
            <artifactId>powerlib-common</artifactId>
            <version>${project.parent.version}</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>net.kyori</groupId>
            <artifactId>adventure-text-minimessage</artifactId>
            <version>4.17.0</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>net.kyori</groupId>
            <artifactId>adventure-text-serializer-legacy</artifactId>
            <version>4.17.0</version>
            <scope>compile</scope>
        </dependency>
    </dependencies>
</project>
```

- [ ] **Step 2: Create `MiniMessageSupport.java`**

`minimessage/src/main/java/it/mycraft/powerlib/minimessage/MiniMessageSupport.java`:
```java
package it.mycraft.powerlib.minimessage;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public final class MiniMessageSupport {

    private static final MiniMessage MINI = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY =
            LegacyComponentSerializer.legacySection();

    private MiniMessageSupport() {}

    public static Component parse(String input) {
        if (input == null) return Component.empty();
        return MINI.deserialize(input);
    }

    public static String parseLegacy(String input) {
        if (input == null) return "";
        return LEGACY.serialize(parse(input));
    }
}
```

- [ ] **Step 3: Add module to root `pom.xml`**

In `<modules>`, before `<module>tests</module>`:
```xml
        <module>minimessage</module>
```

- [ ] **Step 4: Add tests dep**

In `tests/pom.xml`, in `<dependencies>` add:
```xml
        <dependency>
            <groupId>it.mycraft</groupId>
            <artifactId>powerlib-minimessage</artifactId>
            <version>${project.parent.version}</version>
        </dependency>
```

- [ ] **Step 5: Write failing test**

`tests/src/test/java/it/mycraft/powerlib/minimessage/MiniMessageSupportTest.java`:
```java
package it.mycraft.powerlib.minimessage;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MiniMessageSupportTest {

    @Test
    void parsePlainTextProducesSameText() {
        assertThat(MiniMessageSupport.parse("hello").toString()).contains("hello");
    }

    @Test
    void parseLegacyHandlesGradient() {
        String legacy = MiniMessageSupport.parseLegacy("<gradient:red:gold>Ciao</gradient>");
        // Expect a string containing legacy section codes (§) for color
        assertThat(legacy).contains("§");
        assertThat(legacy).contains("Ciao");
    }

    @Test
    void parseLegacyOnNullReturnsEmpty() {
        assertThat(MiniMessageSupport.parseLegacy(null)).isEmpty();
    }
}
```

Run:
```bash
./mvnw -pl minimessage,tests -am clean install -DskipTests
./mvnw -pl tests test -Dtest=MiniMessageSupportTest
```
Expected: 3 tests passed.

- [ ] **Step 6: Add `Message.miniMessage(String)` factory**

In `common/src/main/java/it/mycraft/powerlib/common/chat/Message.java`, add (no removal of existing constructors):

A) Note: the `common` module does NOT depend on `minimessage` (would create a cycle / pull a dep into common). The factory uses *reflection* to call MiniMessageSupport so common stays decoupled:
```java
    /**
     * Builds a Message whose content is a MiniMessage tag-set, parsed and
     * rendered as legacy section codes. Requires `powerlib-minimessage` on
     * the runtime classpath, otherwise falls back to literal text.
     */
    public static Message miniMessage(String input) {
        return new Message(invokeMini(input), false);
    }

    public static Message miniMessage(String... lines) {
        java.util.List<String> rendered = new java.util.ArrayList<>(lines.length);
        for (String line : lines) rendered.add(invokeMini(line));
        return new Message(rendered, false);
    }

    private static String invokeMini(String input) {
        try {
            Class<?> clazz = Class.forName("it.mycraft.powerlib.minimessage.MiniMessageSupport");
            return (String) clazz.getMethod("parseLegacy", String.class).invoke(null, input);
        } catch (ReflectiveOperationException e) {
            return input == null ? "" : input;
        }
    }
```

- [ ] **Step 7: Add MiniMessage convenience methods to `ItemBuilder`**

In `bukkit/pom.xml`, add as compile dep (after `powerlib-common`):
```xml
        <dependency>
            <groupId>it.mycraft</groupId>
            <artifactId>powerlib-minimessage</artifactId>
            <version>${project.parent.version}</version>
            <scope>compile</scope>
        </dependency>
```

In `ItemBuilder.java`, add (anywhere appropriate near `setName`/`setLore`):
```java
    import it.mycraft.powerlib.minimessage.MiniMessageSupport;

    public ItemBuilder setNameMini(String miniInput) {
        return setName(MiniMessageSupport.parseLegacy(miniInput));
    }

    public ItemBuilder setLoreMini(java.util.List<String> miniInputs) {
        java.util.List<String> rendered = new java.util.ArrayList<>(miniInputs.size());
        for (String s : miniInputs) rendered.add(MiniMessageSupport.parseLegacy(s));
        return setLore(rendered);
    }

    public ItemBuilder addLoreMini(String... miniInputs) {
        for (String s : miniInputs) addLore(MiniMessageSupport.parseLegacy(s));
        return this;
    }
```

- [ ] **Step 8: Update `all/pom.xml` to include the new module**

In `all/pom.xml` `<dependencies>`, add (next to other powerlib-* deps):
```xml
        <dependency>
            <groupId>it.mycraft</groupId>
            <artifactId>powerlib-minimessage</artifactId>
            <version>${project.parent.version}</version>
            <scope>compile</scope>
        </dependency>
```

- [ ] **Step 9: Build and test**

```bash
./mvnw clean install -DskipTests
./mvnw -pl tests test
```
Expected: green.

- [ ] **Step 10: Commit**

```bash
git add minimessage pom.xml all/pom.xml bukkit/pom.xml \
        bukkit/src/main/java/it/mycraft/powerlib/bukkit/item/ItemBuilder.java \
        common/src/main/java/it/mycraft/powerlib/common/chat/Message.java \
        tests/pom.xml \
        tests/src/test/java/it/mycraft/powerlib/minimessage/MiniMessageSupportTest.java
git commit -m "feat: add powerlib-minimessage module and MiniMessage helpers in Message/ItemBuilder"
```

---

## Task 11: Add `powerlib-components` module (DataComponent helpers, MC 1.20.5+)

**Files:**
- Create: `components/pom.xml`, `components/src/main/java/it/mycraft/powerlib/components/ComponentBuilder.java`
- Create: `components/README.md` (states 1.20.5+ requirement)
- Modify: root `pom.xml` (add module)
- Modify: `bukkit/src/main/java/it/mycraft/powerlib/bukkit/item/ItemBuilder.java` (add `addBuildStep`)
- Test: `tests/src/test/java/it/mycraft/powerlib/components/ComponentBuilderTest.java`, `tests/pom.xml` (add dep)

> Note: `components` does NOT go in `all` because it raises the runtime baseline. Document in README.

- [ ] **Step 1: Create `components/pom.xml`**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <groupId>it.mycraft</groupId>
        <artifactId>powerlib</artifactId>
        <version>1.2.16-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>powerlib-components</artifactId>

    <repositories>
        <repository>
            <id>spigot-repo</id>
            <url>https://hub.spigotmc.org/nexus/content/repositories/snapshots/</url>
        </repository>
    </repositories>

    <dependencies>
        <dependency>
            <groupId>it.mycraft</groupId>
            <artifactId>powerlib-bukkit</artifactId>
            <version>${project.parent.version}</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>org.spigotmc</groupId>
            <artifactId>spigot-api</artifactId>
            <version>1.20.5-R0.1-SNAPSHOT</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>
</project>
```

- [ ] **Step 2: Create `components/README.md`**

```markdown
# powerlib-components

DataComponent fluent helpers for `ItemBuilder`. **Requires Minecraft server 1.20.5+** at runtime.

## Why a separate module?
DataComponent API (`ItemMeta#setFood`, `ItemMeta#setTool`, etc.) was introduced in Spigot 1.20.5.
Putting these helpers in the base `powerlib-bukkit` module would force the whole library
to compile against 1.20.5+, dropping support for 1.16-1.20.4 servers. This module is
opt-in: import it only if your plugin targets 1.20.5+.

## Usage

```java
ItemBuilder builder = new ItemBuilder().setMaterial(Material.APPLE);
ComponentBuilder.create()
        .food(4, 1.2f, true)
        .applyTo(builder);
ItemStack stack = builder.build();
```
```

- [ ] **Step 3: Create `ComponentBuilder.java`**

`components/src/main/java/it/mycraft/powerlib/components/ComponentBuilder.java`:
```java
package it.mycraft.powerlib.components;

import it.mycraft.powerlib.bukkit.item.ItemBuilder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Fluent helpers around DataComponent setters introduced in Spigot 1.20.5.
 * Each fluent call accumulates a Consumer<ItemMeta> that is applied lazily
 * via #applyTo().
 *
 * <p><strong>Runtime requirement:</strong> Minecraft 1.20.5+. Calling fluent
 * methods on older servers will compile (the JVM doesn't resolve methods
 * eagerly) but applyTo will throw NoSuchMethodError at first invocation.</p>
 */
public final class ComponentBuilder {

    private final List<Consumer<ItemMeta>> steps = new ArrayList<>();

    private ComponentBuilder() {}

    public static ComponentBuilder create() {
        return new ComponentBuilder();
    }

    public ComponentBuilder unbreakable(boolean enabled) {
        steps.add(meta -> meta.setUnbreakable(enabled));
        return this;
    }

    public ComponentBuilder enchantmentGlintOverride(boolean enabled) {
        steps.add(meta -> meta.setEnchantmentGlintOverride(enabled));
        return this;
    }

    public ComponentBuilder customModelData(int data) {
        steps.add(meta -> meta.setCustomModelData(data));
        return this;
    }

    public ComponentBuilder food(int nutrition, float saturation, boolean canAlwaysEat) {
        steps.add(meta -> {
            org.bukkit.inventory.meta.components.FoodComponent food = meta.getFood();
            food.setNutrition(nutrition);
            food.setSaturation(saturation);
            food.setCanAlwaysEat(canAlwaysEat);
            meta.setFood(food);
        });
        return this;
    }

    public ComponentBuilder toolDamagePerBlock(int damage) {
        steps.add(meta -> {
            org.bukkit.inventory.meta.components.ToolComponent tool = meta.getTool();
            tool.setDamagePerBlock(damage);
            meta.setTool(tool);
        });
        return this;
    }

    public void applyTo(ItemBuilder builder) {
        builder.addBuildStep(meta -> steps.forEach(s -> s.accept(meta)));
    }

    public void applyTo(ItemStack stack) {
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) return;
        steps.forEach(s -> s.accept(meta));
        stack.setItemMeta(meta);
    }
}
```

- [ ] **Step 4: Add `addBuildStep` to `ItemBuilder`**

In `ItemBuilder.java`:

A) Add field:
```java
    private final List<Consumer<ItemMeta>> buildSteps = new ArrayList<>();
```
(`Consumer` import: `java.util.function.Consumer`.)

B) Add method:
```java
    public ItemBuilder addBuildStep(Consumer<ItemMeta> step) {
        buildSteps.add(step);
        return this;
    }
```

C) In `build()`, after `PersistentDataApplier.apply(itemMeta, persistentData);` and before `itemStack.setItemMeta(itemMeta)`:
```java
            for (Consumer<ItemMeta> step : buildSteps) {
                step.accept(itemMeta);
            }
```

- [ ] **Step 5: Add module to root `pom.xml`**

In `<modules>`, after `<module>minimessage</module>`:
```xml
        <module>components</module>
```

> Do NOT add to `all/pom.xml`.

- [ ] **Step 6: Add tests dep**

In `tests/pom.xml`, add (and bump the test-time spigot-api to 1.20.5 to allow `ComponentBuilder` to compile in test classpath):
```xml
        <dependency>
            <groupId>it.mycraft</groupId>
            <artifactId>powerlib-components</artifactId>
            <version>${project.parent.version}</version>
            <scope>test</scope>
        </dependency>
```
And replace the existing test-time `spigot-api 1.20.4` dep with `1.20.5-R0.1-SNAPSHOT`.

- [ ] **Step 7: Write the test**

`tests/src/test/java/it/mycraft/powerlib/components/ComponentBuilderTest.java`:
```java
package it.mycraft.powerlib.components;

import be.seeseemelk.mockbukkit.MockBukkit;
import it.mycraft.powerlib.bukkit.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ComponentBuilderTest {

    @BeforeEach
    void setUp() { MockBukkit.mock(); }

    @AfterEach
    void tearDown() { MockBukkit.unmock(); }

    @Test
    void unbreakableIsApplied() {
        ItemBuilder builder = new ItemBuilder().setMaterial(Material.DIAMOND_PICKAXE);
        ComponentBuilder.create().unbreakable(true).applyTo(builder);
        ItemStack stack = builder.build();
        assertThat(stack.getItemMeta().isUnbreakable()).isTrue();
    }

    @Test
    void customModelDataIsApplied() {
        ItemBuilder builder = new ItemBuilder().setMaterial(Material.DIAMOND);
        ComponentBuilder.create().customModelData(1234).applyTo(builder);
        ItemStack stack = builder.build();
        assertThat(stack.getItemMeta().getCustomModelData()).isEqualTo(1234);
    }
}
```

- [ ] **Step 8: Build and test**

```bash
./mvnw clean install -DskipTests
./mvnw -pl tests test -Dtest=ComponentBuilderTest
```
Expected: 2 tests passed.

- [ ] **Step 9: Commit**

```bash
git add components pom.xml tests/pom.xml \
        bukkit/src/main/java/it/mycraft/powerlib/bukkit/item/ItemBuilder.java \
        tests/src/test/java/it/mycraft/powerlib/components/ComponentBuilderTest.java
git commit -m "feat: add powerlib-components module (DataComponent helpers, requires MC 1.20.5+)"
```

---

## Task 12: Add `commands-api` and `commands-bukkit` modules

**Files:**
- Create: `commands-api/pom.xml`, `commands-api/src/main/java/it/mycraft/powerlib/commands/{CommandBuilder,CommandContext,CommandSender,Argument,Args}.java`
- Create: `commands-bukkit/pom.xml`, `commands-bukkit/src/main/java/it/mycraft/powerlib/commands/bukkit/{BukkitCommandBuilder,BukkitCommandContext,BukkitCommandSender,PaperBrigadierAdapter,BukkitCommandFallback}.java`
- Modify: root `pom.xml` (add modules), `all/pom.xml` (add deps)
- Test: `tests/src/test/java/it/mycraft/powerlib/commands/{CommandBuilderTest,BukkitCommandTest}.java`, `tests/pom.xml` (add deps)

This is the largest commit. Take it slow.

- [ ] **Step 1: Create `commands-api/pom.xml`**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0">
    <parent>
        <groupId>it.mycraft</groupId>
        <artifactId>powerlib</artifactId>
        <version>1.2.16-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>
    <artifactId>powerlib-commands-api</artifactId>

    <dependencies>
        <dependency>
            <groupId>it.mycraft</groupId>
            <artifactId>powerlib-common</artifactId>
            <version>${project.parent.version}</version>
            <scope>compile</scope>
        </dependency>
    </dependencies>
</project>
```

- [ ] **Step 2: Create the API classes**

`commands-api/src/main/java/it/mycraft/powerlib/commands/CommandSender.java`:
```java
package it.mycraft.powerlib.commands;

/** Platform-agnostic abstraction over a command issuer. */
public interface CommandSender {
    String getName();
    boolean hasPermission(String permission);
    void sendMessage(String message);
    Object raw(); // platform-native sender for advanced use
}
```

`commands-api/src/main/java/it/mycraft/powerlib/commands/CommandContext.java`:
```java
package it.mycraft.powerlib.commands;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public final class CommandContext {

    private final Map<String, Object> values = new LinkedHashMap<>();

    public CommandContext put(String name, Object value) {
        values.put(name, value);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(String name, Class<T> type) {
        Object v = values.get(name);
        if (v == null || !type.isInstance(v)) return Optional.empty();
        return Optional.of((T) v);
    }

    public Map<String, Object> all() {
        return java.util.Collections.unmodifiableMap(values);
    }
}
```

`commands-api/src/main/java/it/mycraft/powerlib/commands/Argument.java`:
```java
package it.mycraft.powerlib.commands;

import java.util.Optional;

public final class Argument<T> {

    private final String name;
    private final java.util.function.Function<String, Optional<T>> parser;
    private final Class<T> type;
    private boolean optional;

    public Argument(String name, Class<T> type, java.util.function.Function<String, Optional<T>> parser) {
        this.name = name;
        this.type = type;
        this.parser = parser;
    }

    public Argument<T> optional() { this.optional = true; return this; }

    public String getName() { return name; }
    public Class<T> getType() { return type; }
    public boolean isOptional() { return optional; }
    public Optional<T> parse(String raw) { return parser.apply(raw); }
}
```

`commands-api/src/main/java/it/mycraft/powerlib/commands/Args.java`:
```java
package it.mycraft.powerlib.commands;

import java.util.Optional;
import java.util.function.Function;

public final class Args {
    private Args() {}

    public static Argument<String> string(String name) {
        return new Argument<>(name, String.class, Optional::ofNullable);
    }

    public static Argument<Integer> integer(String name) {
        return new Argument<>(name, Integer.class, raw -> {
            try { return Optional.of(Integer.parseInt(raw)); }
            catch (NumberFormatException e) { return Optional.empty(); }
        });
    }

    public static Argument<Boolean> bool(String name) {
        return new Argument<>(name, Boolean.class, raw -> {
            if ("true".equalsIgnoreCase(raw)) return Optional.of(Boolean.TRUE);
            if ("false".equalsIgnoreCase(raw)) return Optional.of(Boolean.FALSE);
            return Optional.empty();
        });
    }

    public static <T> Argument<T> custom(String name, Class<T> type, Function<String, Optional<T>> parser) {
        return new Argument<>(name, type, parser);
    }
}
```

`commands-api/src/main/java/it/mycraft/powerlib/commands/CommandBuilder.java`:
```java
package it.mycraft.powerlib.commands;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public abstract class CommandBuilder {

    protected final String name;
    protected String description = "";
    protected String permission;
    protected final List<Argument<?>> arguments = new ArrayList<>();
    protected BiConsumer<CommandSender, CommandContext> executor;
    protected BiFunction<CommandSender, CommandContext, List<String>> tabComplete;
    protected final Map<String, CommandBuilder> subcommands = new LinkedHashMap<>();

    protected CommandBuilder(String name) {
        this.name = name;
    }

    public CommandBuilder description(String desc) { this.description = desc; return this; }
    public CommandBuilder permission(String perm) { this.permission = perm; return this; }
    public CommandBuilder argument(Argument<?> arg) { this.arguments.add(arg); return this; }
    public CommandBuilder executor(BiConsumer<CommandSender, CommandContext> exec) { this.executor = exec; return this; }
    public CommandBuilder tabComplete(BiFunction<CommandSender, CommandContext, List<String>> tab) { this.tabComplete = tab; return this; }
    public CommandBuilder subcommand(String name, Consumer<CommandBuilder> config) {
        CommandBuilder sub = newChild(name);
        config.accept(sub);
        subcommands.put(name, sub);
        return this;
    }

    /** Subclasses produce same-platform child builders. */
    protected abstract CommandBuilder newChild(String name);

    /** Platform-specific registration entrypoint. Receives a platform plugin/proxy handle. */
    public abstract void register(Object pluginOrProxy);

    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getPermission() { return permission; }
    public List<Argument<?>> getArguments() { return arguments; }
    public BiConsumer<CommandSender, CommandContext> getExecutor() { return executor; }
    public BiFunction<CommandSender, CommandContext, List<String>> getTabComplete() { return tabComplete; }
    public Map<String, CommandBuilder> getSubcommands() { return subcommands; }
}
```

- [ ] **Step 3: Create `commands-bukkit/pom.xml`**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0">
    <parent>
        <groupId>it.mycraft</groupId>
        <artifactId>powerlib</artifactId>
        <version>1.2.16-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>
    <artifactId>powerlib-commands-bukkit</artifactId>

    <repositories>
        <repository>
            <id>spigot-repo</id>
            <url>https://hub.spigotmc.org/nexus/content/repositories/snapshots/</url>
        </repository>
        <repository>
            <id>paper</id>
            <url>https://repo.papermc.io/repository/maven-public/</url>
        </repository>
    </repositories>

    <dependencies>
        <dependency>
            <groupId>it.mycraft</groupId>
            <artifactId>powerlib-commands-api</artifactId>
            <version>${project.parent.version}</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>it.mycraft</groupId>
            <artifactId>powerlib-bukkit</artifactId>
            <version>${project.parent.version}</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>org.spigotmc</groupId>
            <artifactId>spigot-api</artifactId>
            <version>1.19.4-R0.1-SNAPSHOT</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>
</project>
```

> Note: we do NOT compile against `paper-api`. Paper-Brigadier is detected at runtime via reflection.

- [ ] **Step 4: Create the Bukkit impl classes**

`commands-bukkit/src/main/java/it/mycraft/powerlib/commands/bukkit/BukkitCommandSender.java`:
```java
package it.mycraft.powerlib.commands.bukkit;

import it.mycraft.powerlib.commands.CommandSender;

public final class BukkitCommandSender implements CommandSender {

    private final org.bukkit.command.CommandSender raw;

    public BukkitCommandSender(org.bukkit.command.CommandSender raw) {
        this.raw = raw;
    }

    @Override public String getName() { return raw.getName(); }
    @Override public boolean hasPermission(String permission) {
        return permission == null || raw.hasPermission(permission);
    }
    @Override public void sendMessage(String message) { raw.sendMessage(message); }
    @Override public Object raw() { return raw; }
}
```

`commands-bukkit/src/main/java/it/mycraft/powerlib/commands/bukkit/BukkitCommandFallback.java`:
```java
package it.mycraft.powerlib.commands.bukkit;

import it.mycraft.powerlib.commands.Argument;
import it.mycraft.powerlib.commands.CommandBuilder;
import it.mycraft.powerlib.commands.CommandContext;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.command.TabCompleter;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class BukkitCommandFallback {

    private BukkitCommandFallback() {}

    static void register(Plugin plugin, CommandBuilder builder) {
        BukkitCommand cmd = new BukkitCommand(builder.getName()) {
            @Override
            public boolean execute(org.bukkit.command.CommandSender bukkitSender, String label, String[] args) {
                BukkitCommandSender sender = new BukkitCommandSender(bukkitSender);
                if (builder.getPermission() != null && !sender.hasPermission(builder.getPermission())) {
                    sender.sendMessage("§cNo permission.");
                    return true;
                }
                CommandContext ctx = parseArgs(builder, args);
                if (builder.getExecutor() != null) {
                    builder.getExecutor().accept(sender, ctx);
                }
                return true;
            }

            @Override
            public List<String> tabComplete(org.bukkit.command.CommandSender bukkitSender, String alias, String[] args) {
                if (builder.getTabComplete() == null) return Collections.emptyList();
                return builder.getTabComplete().apply(new BukkitCommandSender(bukkitSender), parseArgs(builder, args));
            }
        };
        cmd.setDescription(builder.getDescription());
        if (builder.getPermission() != null) cmd.setPermission(builder.getPermission());

        getCommandMap().register(plugin.getName().toLowerCase(), cmd);
    }

    private static CommandContext parseArgs(CommandBuilder builder, String[] raw) {
        CommandContext ctx = new CommandContext();
        List<Argument<?>> args = builder.getArguments();
        for (int i = 0; i < args.size() && i < raw.length; i++) {
            Argument<?> arg = args.get(i);
            arg.parse(raw[i]).ifPresent(v -> ctx.put(arg.getName(), v));
        }
        return ctx;
    }

    private static CommandMap getCommandMap() {
        try {
            Field field = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            field.setAccessible(true);
            return (CommandMap) field.get(Bukkit.getServer());
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Cannot access Bukkit CommandMap", e);
        }
    }
}
```

`commands-bukkit/src/main/java/it/mycraft/powerlib/commands/bukkit/PaperBrigadierAdapter.java`:
```java
package it.mycraft.powerlib.commands.bukkit;

import it.mycraft.powerlib.commands.CommandBuilder;
import org.bukkit.plugin.Plugin;

/**
 * Reflective adapter for Paper's Brigadier API (1.20.4+).
 * Detection: io.papermc.paper.command.brigadier.Commands present on classpath.
 *
 * For now this adapter delegates back to the BukkitCommand fallback because
 * Paper-Brigadier wiring requires per-event-loop registration via the
 * lifecycle manager — implementing that fully is a follow-up. The detection
 * still exists so the fallback is explicit and future-extension is easy.
 */
final class PaperBrigadierAdapter {

    private PaperBrigadierAdapter() {}

    static boolean isAvailable() {
        try {
            Class.forName("io.papermc.paper.command.brigadier.Commands");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    static void register(Plugin plugin, CommandBuilder builder) {
        // TODO(future): use LifecycleEventManager to register a Brigadier literal node.
        // For now, fall through.
        BukkitCommandFallback.register(plugin, builder);
    }
}
```

`commands-bukkit/src/main/java/it/mycraft/powerlib/commands/bukkit/BukkitCommandBuilder.java`:
```java
package it.mycraft.powerlib.commands.bukkit;

import it.mycraft.powerlib.commands.CommandBuilder;
import org.bukkit.plugin.Plugin;

public final class BukkitCommandBuilder extends CommandBuilder {

    private BukkitCommandBuilder(String name) {
        super(name);
    }

    public static BukkitCommandBuilder create(String name) {
        return new BukkitCommandBuilder(name);
    }

    @Override
    protected CommandBuilder newChild(String name) {
        return new BukkitCommandBuilder(name);
    }

    @Override
    public void register(Object pluginRaw) {
        if (!(pluginRaw instanceof Plugin)) {
            throw new IllegalArgumentException("Bukkit registration expects an org.bukkit.plugin.Plugin");
        }
        Plugin plugin = (Plugin) pluginRaw;
        if (PaperBrigadierAdapter.isAvailable()) {
            PaperBrigadierAdapter.register(plugin, this);
        } else {
            BukkitCommandFallback.register(plugin, this);
        }
    }
}
```

- [ ] **Step 5: Add modules to root `pom.xml`**

In `<modules>`, after `<module>components</module>`:
```xml
        <module>commands-api</module>
        <module>commands-bukkit</module>
```

- [ ] **Step 6: Add tests deps and tests**

In `tests/pom.xml`:
```xml
        <dependency>
            <groupId>it.mycraft</groupId>
            <artifactId>powerlib-commands-api</artifactId>
            <version>${project.parent.version}</version>
        </dependency>
        <dependency>
            <groupId>it.mycraft</groupId>
            <artifactId>powerlib-commands-bukkit</artifactId>
            <version>${project.parent.version}</version>
        </dependency>
```

`tests/src/test/java/it/mycraft/powerlib/commands/CommandBuilderTest.java`:
```java
package it.mycraft.powerlib.commands;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CommandBuilderTest {

    @Test
    void argsIntegerParsesValid() {
        Argument<Integer> arg = Args.integer("count");
        assertThat(arg.parse("42")).contains(42);
    }

    @Test
    void argsIntegerRejectsInvalid() {
        Argument<Integer> arg = Args.integer("count");
        assertThat(arg.parse("not-a-number")).isEmpty();
    }

    @Test
    void argsBoolHandlesBothCases() {
        assertThat(Args.bool("flag").parse("true")).contains(true);
        assertThat(Args.bool("flag").parse("FALSE")).contains(false);
    }

    @Test
    void contextStoresAndRetrievesValuesByType() {
        CommandContext ctx = new CommandContext().put("count", 10).put("name", "alice");
        assertThat(ctx.get("count", Integer.class)).contains(10);
        assertThat(ctx.get("name", String.class)).contains("alice");
        assertThat(ctx.get("count", String.class)).isEmpty();
    }
}
```

`tests/src/test/java/it/mycraft/powerlib/commands/bukkit/BukkitCommandTest.java`:
```java
package it.mycraft.powerlib.commands.bukkit;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.MockPlugin;
import it.mycraft.powerlib.commands.Args;
import org.bukkit.Bukkit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

class BukkitCommandTest {

    private MockPlugin plugin;

    @BeforeEach
    void setUp() {
        MockBukkit.mock();
        plugin = MockBukkit.createMockPlugin("PowerLibTest");
    }

    @AfterEach
    void tearDown() { MockBukkit.unmock(); }

    @Test
    void executorIsInvokedWithParsedArgs() {
        AtomicReference<Integer> received = new AtomicReference<>();
        BukkitCommandBuilder.create("count")
                .argument(Args.integer("n"))
                .executor((sender, ctx) -> received.set(ctx.get("n", Integer.class).orElse(-1)))
                .register(plugin);

        Bukkit.dispatchCommand(MockBukkit.getMock().getConsoleSender(), "count 7");

        assertThat(received.get()).isEqualTo(7);
    }
}
```

- [ ] **Step 7: Build and test**

```bash
./mvnw clean install -DskipTests
./mvnw -pl tests test
```
Expected: green.

- [ ] **Step 8: Commit**

```bash
git add commands-api commands-bukkit pom.xml tests/pom.xml \
        tests/src/test/java/it/mycraft/powerlib/commands
git commit -m "feat: add powerlib-commands-api and powerlib-commands-bukkit modules"
```

---

## Task 13: Add `commands-bungee` and `commands-velocity` modules

**Files:**
- Create: `commands-bungee/pom.xml`, `commands-bungee/src/main/java/it/mycraft/powerlib/commands/bungee/{BungeeCommandBuilder,BungeeCommandSender}.java`
- Create: `commands-velocity/pom.xml`, `commands-velocity/src/main/java/it/mycraft/powerlib/commands/velocity/{VelocityCommandBuilder,VelocityCommandSender}.java`
- Modify: root `pom.xml` (add modules), `all/pom.xml`

- [ ] **Step 1: `commands-bungee/pom.xml`**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0">
    <parent>
        <groupId>it.mycraft</groupId>
        <artifactId>powerlib</artifactId>
        <version>1.2.16-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>
    <artifactId>powerlib-commands-bungee</artifactId>

    <repositories>
        <repository>
            <id>bungeecord-repo</id>
            <url>https://oss.sonatype.org/content/repositories/snapshots</url>
        </repository>
    </repositories>

    <dependencies>
        <dependency>
            <groupId>it.mycraft</groupId>
            <artifactId>powerlib-commands-api</artifactId>
            <version>${project.parent.version}</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>it.mycraft</groupId>
            <artifactId>powerlib-bungee</artifactId>
            <version>${project.parent.version}</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>net.md-5</groupId>
            <artifactId>bungeecord-api</artifactId>
            <version>1.19-R0.1-SNAPSHOT</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>
</project>
```

- [ ] **Step 2: `BungeeCommandSender.java` and `BungeeCommandBuilder.java`**

`BungeeCommandSender.java`:
```java
package it.mycraft.powerlib.commands.bungee;

import it.mycraft.powerlib.commands.CommandSender;

public final class BungeeCommandSender implements CommandSender {
    private final net.md_5.bungee.api.CommandSender raw;
    public BungeeCommandSender(net.md_5.bungee.api.CommandSender raw) { this.raw = raw; }
    @Override public String getName() { return raw.getName(); }
    @Override public boolean hasPermission(String permission) { return permission == null || raw.hasPermission(permission); }
    @Override public void sendMessage(String message) { raw.sendMessage(net.md_5.bungee.api.chat.TextComponent.fromLegacyText(message)); }
    @Override public Object raw() { return raw; }
}
```

`BungeeCommandBuilder.java`:
```java
package it.mycraft.powerlib.commands.bungee;

import it.mycraft.powerlib.commands.Argument;
import it.mycraft.powerlib.commands.CommandBuilder;
import it.mycraft.powerlib.commands.CommandContext;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.Collections;
import java.util.List;

public final class BungeeCommandBuilder extends CommandBuilder {

    private BungeeCommandBuilder(String name) { super(name); }

    public static BungeeCommandBuilder create(String name) { return new BungeeCommandBuilder(name); }

    @Override
    protected CommandBuilder newChild(String name) { return new BungeeCommandBuilder(name); }

    @Override
    public void register(Object pluginRaw) {
        if (!(pluginRaw instanceof Plugin)) {
            throw new IllegalArgumentException("Bungee registration expects a net.md_5.bungee.api.plugin.Plugin");
        }
        Plugin plugin = (Plugin) pluginRaw;
        Command cmd = new BungeeAdapter(this);
        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, cmd);
    }

    private static class BungeeAdapter extends Command implements TabExecutor {
        private final CommandBuilder builder;
        BungeeAdapter(CommandBuilder builder) {
            super(builder.getName(), builder.getPermission());
            this.builder = builder;
        }

        @Override
        public void execute(net.md_5.bungee.api.CommandSender bungeeSender, String[] args) {
            BungeeCommandSender sender = new BungeeCommandSender(bungeeSender);
            if (builder.getPermission() != null && !sender.hasPermission(builder.getPermission())) {
                sender.sendMessage("§cNo permission.");
                return;
            }
            CommandContext ctx = parseArgs(args);
            if (builder.getExecutor() != null) builder.getExecutor().accept(sender, ctx);
        }

        @Override
        public Iterable<String> onTabComplete(net.md_5.bungee.api.CommandSender bungeeSender, String[] args) {
            if (builder.getTabComplete() == null) return Collections.emptyList();
            return builder.getTabComplete().apply(new BungeeCommandSender(bungeeSender), parseArgs(args));
        }

        private CommandContext parseArgs(String[] raw) {
            CommandContext ctx = new CommandContext();
            List<Argument<?>> args = builder.getArguments();
            for (int i = 0; i < args.size() && i < raw.length; i++) {
                Argument<?> arg = args.get(i);
                arg.parse(raw[i]).ifPresent(v -> ctx.put(arg.getName(), v));
            }
            return ctx;
        }
    }
}
```

- [ ] **Step 3: `commands-velocity/pom.xml`**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0">
    <parent>
        <groupId>it.mycraft</groupId>
        <artifactId>powerlib</artifactId>
        <version>1.2.16-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>
    <artifactId>powerlib-commands-velocity</artifactId>

    <repositories>
        <repository>
            <id>papermc-repo</id>
            <url>https://repo.papermc.io/repository/maven-public/</url>
        </repository>
    </repositories>

    <dependencies>
        <dependency>
            <groupId>it.mycraft</groupId>
            <artifactId>powerlib-commands-api</artifactId>
            <version>${project.parent.version}</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>it.mycraft</groupId>
            <artifactId>powerlib-velocity</artifactId>
            <version>${project.parent.version}</version>
            <scope>compile</scope>
        </dependency>
        <dependency>
            <groupId>com.velocitypowered</groupId>
            <artifactId>velocity-api</artifactId>
            <version>3.3.0-SNAPSHOT</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>
</project>
```

- [ ] **Step 4: `VelocityCommandSender.java` and `VelocityCommandBuilder.java`**

`VelocityCommandSender.java`:
```java
package it.mycraft.powerlib.commands.velocity;

import com.velocitypowered.api.command.CommandSource;
import it.mycraft.powerlib.commands.CommandSender;
import net.kyori.adventure.text.Component;

public final class VelocityCommandSender implements CommandSender {
    private final CommandSource raw;
    public VelocityCommandSender(CommandSource raw) { this.raw = raw; }
    @Override public String getName() {
        return raw instanceof com.velocitypowered.api.proxy.Player
                ? ((com.velocitypowered.api.proxy.Player) raw).getUsername()
                : "CONSOLE";
    }
    @Override public boolean hasPermission(String permission) { return permission == null || raw.hasPermission(permission); }
    @Override public void sendMessage(String message) { raw.sendMessage(Component.text(message)); }
    @Override public Object raw() { return raw; }
}
```

`VelocityCommandBuilder.java`:
```java
package it.mycraft.powerlib.commands.velocity;

import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.ProxyServer;
import it.mycraft.powerlib.commands.Argument;
import it.mycraft.powerlib.commands.CommandBuilder;
import it.mycraft.powerlib.commands.CommandContext;

import java.util.Collections;
import java.util.List;

public final class VelocityCommandBuilder extends CommandBuilder {

    private VelocityCommandBuilder(String name) { super(name); }
    public static VelocityCommandBuilder create(String name) { return new VelocityCommandBuilder(name); }

    @Override
    protected CommandBuilder newChild(String name) { return new VelocityCommandBuilder(name); }

    @Override
    public void register(Object proxyRaw) {
        if (!(proxyRaw instanceof ProxyServer)) {
            throw new IllegalArgumentException("Velocity registration expects a ProxyServer");
        }
        ProxyServer proxy = (ProxyServer) proxyRaw;
        CommandManager manager = proxy.getCommandManager();
        CommandMeta meta = manager.metaBuilder(getName()).plugin(this).build();
        manager.register(meta, new VelocityAdapter(this));
    }

    private static class VelocityAdapter implements SimpleCommand {
        private final CommandBuilder builder;
        VelocityAdapter(CommandBuilder builder) { this.builder = builder; }

        @Override
        public void execute(Invocation invocation) {
            VelocityCommandSender sender = new VelocityCommandSender(invocation.source());
            if (builder.getPermission() != null && !sender.hasPermission(builder.getPermission())) {
                sender.sendMessage("§cNo permission.");
                return;
            }
            CommandContext ctx = parseArgs(invocation.arguments());
            if (builder.getExecutor() != null) builder.getExecutor().accept(sender, ctx);
        }

        @Override
        public List<String> suggest(Invocation invocation) {
            if (builder.getTabComplete() == null) return Collections.emptyList();
            return builder.getTabComplete().apply(new VelocityCommandSender(invocation.source()), parseArgs(invocation.arguments()));
        }

        private CommandContext parseArgs(String[] raw) {
            CommandContext ctx = new CommandContext();
            List<Argument<?>> args = builder.getArguments();
            for (int i = 0; i < args.size() && i < raw.length; i++) {
                Argument<?> arg = args.get(i);
                arg.parse(raw[i]).ifPresent(v -> ctx.put(arg.getName(), v));
            }
            return ctx;
        }
    }
}
```

- [ ] **Step 5: Add modules to root pom**

```xml
        <module>commands-bungee</module>
        <module>commands-velocity</module>
```

- [ ] **Step 6: Update `all/pom.xml`**

Add (compile scope) `powerlib-commands-api`, `powerlib-commands-bukkit`, `powerlib-commands-bungee`, `powerlib-commands-velocity`. Do NOT add `powerlib-components`.

- [ ] **Step 7: Build to verify**

```bash
./mvnw clean install -DskipTests
```
Expected: `BUILD SUCCESS`. (No new tests in this commit; Bungee/Velocity registration is hard to mock without bringing the platform server up — leave for integration testing.)

- [ ] **Step 8: Commit**

```bash
git add commands-bungee commands-velocity pom.xml all/pom.xml
git commit -m "feat: add powerlib-commands-bungee and powerlib-commands-velocity modules"
```

---

## Task 14: Add `PagedInventoryBuilder`

**Files:**
- Create: `bukkit/src/main/java/it/mycraft/powerlib/bukkit/inventory/PagedInventoryBuilder.java`
- Create: `bukkit/src/main/java/it/mycraft/powerlib/bukkit/inventory/NavigationLayout.java`
- Create: `bukkit/src/main/java/it/mycraft/powerlib/bukkit/inventory/internal/PagedInventoryListener.java`
- Modify: `bukkit/src/main/java/it/mycraft/powerlib/bukkit/PowerLib.java` (register listener once)
- Test: `tests/src/test/java/it/mycraft/powerlib/bukkit/inventory/PagedInventoryTest.java`

- [ ] **Step 1: `NavigationLayout.java`**

```java
package it.mycraft.powerlib.bukkit.inventory;

public final class NavigationLayout {

    private final int prevSlot;
    private final int nextSlot;

    private NavigationLayout(int prev, int next) {
        this.prevSlot = prev;
        this.nextSlot = next;
    }

    public static NavigationLayout bottomRow(int rows) {
        int last = rows * 9;
        return new NavigationLayout(last - 9, last - 1);
    }

    public static NavigationLayout custom(int prev, int next) {
        return new NavigationLayout(prev, next);
    }

    public int getPrevSlot() { return prevSlot; }
    public int getNextSlot() { return nextSlot; }
}
```

- [ ] **Step 2: `PagedInventoryListener.java` (package-private)**

```java
package it.mycraft.powerlib.bukkit.inventory.internal;

import it.mycraft.powerlib.bukkit.inventory.PagedInventoryBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class PagedInventoryListener implements Listener {

    private static final Map<UUID, OpenedPagedInventory> OPEN = new HashMap<>();

    public static void track(Player player, OpenedPagedInventory state) {
        OPEN.put(player.getUniqueId(), state);
    }

    public static OpenedPagedInventory get(Player player) {
        return OPEN.get(player.getUniqueId());
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        OpenedPagedInventory state = OPEN.get(player.getUniqueId());
        if (state == null || !state.getInventory().equals(event.getInventory())) return;
        event.setCancelled(true);
        state.handleClick(player, event);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        OPEN.remove(event.getPlayer().getUniqueId());
    }
}
```

> `OpenedPagedInventory` is the holder built by `PagedInventoryBuilder.open()`. It encapsulates the items, current page, and click handlers, and knows how to re-render and how to dispatch a click. Define it package-private alongside the builder. Implementation outline:

```java
package it.mycraft.powerlib.bukkit.inventory.internal;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public final class OpenedPagedInventory {
    private final Inventory inventory;
    private final List<ItemStack> items;
    private final int rows;
    private final BiFunction<ItemStack, Integer, ItemStack> renderer;
    private final Map<Integer, BiConsumer<Player, InventoryClickEvent>> slotHandlers;
    private final int prevSlot, nextSlot;
    private final BiConsumer<Player, Integer> onPageChange;
    private final ItemStack filler;
    private int page = 0;

    public OpenedPagedInventory(Inventory inventory, List<ItemStack> items, int rows,
                                BiFunction<ItemStack, Integer, ItemStack> renderer,
                                Map<Integer, BiConsumer<Player, InventoryClickEvent>> slotHandlers,
                                int prevSlot, int nextSlot,
                                BiConsumer<Player, Integer> onPageChange,
                                ItemStack filler) {
        this.inventory = inventory;
        this.items = items;
        this.rows = rows;
        this.renderer = renderer;
        this.slotHandlers = slotHandlers;
        this.prevSlot = prevSlot;
        this.nextSlot = nextSlot;
        this.onPageChange = onPageChange;
        this.filler = filler;
    }

    public Inventory getInventory() { return inventory; }

    public void render(Player player) {
        inventory.clear();
        int contentSlots = (rows - 1) * 9; // last row reserved for nav
        int startIndex = page * contentSlots;
        for (int i = 0; i < contentSlots; i++) {
            int idx = startIndex + i;
            if (idx >= items.size()) {
                if (filler != null) inventory.setItem(i, filler);
                continue;
            }
            ItemStack visual = renderer != null ? renderer.apply(items.get(idx), idx) : items.get(idx);
            inventory.setItem(i, visual);
        }
        // navigation icons (page handlers populate slotHandlers)
    }

    public void handleClick(Player player, InventoryClickEvent event) {
        int slot = event.getRawSlot();
        if (slot == prevSlot) {
            if (page > 0) { page--; render(player); if (onPageChange != null) onPageChange.accept(player, page); }
            return;
        }
        if (slot == nextSlot) {
            page++;
            render(player);
            if (onPageChange != null) onPageChange.accept(player, page);
            return;
        }
        BiConsumer<Player, InventoryClickEvent> h = slotHandlers.get(slot);
        if (h != null) h.accept(player, event);
    }
}
```

- [ ] **Step 3: `PagedInventoryBuilder.java`**

```java
package it.mycraft.powerlib.bukkit.inventory;

import it.mycraft.powerlib.bukkit.inventory.internal.OpenedPagedInventory;
import it.mycraft.powerlib.bukkit.inventory.internal.PagedInventoryListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public final class PagedInventoryBuilder {

    private final int rows;
    private final String title;
    private List<ItemStack> items = new ArrayList<>();
    private BiFunction<ItemStack, Integer, ItemStack> renderer;
    private NavigationLayout navigation;
    private final Map<Integer, BiConsumer<Player, InventoryClickEvent>> slotHandlers = new HashMap<>();
    private BiConsumer<Player, Integer> onPageChange;
    private ItemStack filler;

    private PagedInventoryBuilder(int rows, String title) {
        this.rows = rows;
        this.title = title;
    }

    public static PagedInventoryBuilder create(int rows, String title) {
        if (rows < 2 || rows > 6) throw new IllegalArgumentException("rows must be between 2 and 6");
        return new PagedInventoryBuilder(rows, title);
    }

    public PagedInventoryBuilder items(List<ItemStack> items) { this.items = new ArrayList<>(items); return this; }
    public PagedInventoryBuilder renderer(BiFunction<ItemStack, Integer, ItemStack> renderer) { this.renderer = renderer; return this; }
    public PagedInventoryBuilder navigation(NavigationLayout layout) { this.navigation = layout; return this; }
    public PagedInventoryBuilder onClick(int slot, BiConsumer<Player, InventoryClickEvent> handler) { slotHandlers.put(slot, handler); return this; }
    public PagedInventoryBuilder onPageChange(BiConsumer<Player, Integer> h) { this.onPageChange = h; return this; }
    public PagedInventoryBuilder filler(ItemStack filler) { this.filler = filler; return this; }

    public void open(Player player) {
        NavigationLayout nav = navigation != null ? navigation : NavigationLayout.bottomRow(rows);
        Inventory inv = Bukkit.createInventory(null, rows * 9, title);
        OpenedPagedInventory state = new OpenedPagedInventory(
                inv, items, rows, renderer, slotHandlers, nav.getPrevSlot(), nav.getNextSlot(), onPageChange, filler);
        PagedInventoryListener.track(player, state);
        state.render(player);
        player.openInventory(inv);
    }
}
```

- [ ] **Step 4: Register listener in `PowerLib.inject`**

In `PowerLib.java`, modify `inject(Plugin)`:
```java
    public static void inject(Plugin plugin) {
        adventure = BukkitAudiences.create(plugin);
        new NexoListener(plugin);
        Bukkit.getPluginManager().registerEvents(new PagedInventoryListener(), plugin);
    }
```
Add imports.

- [ ] **Step 5: Make `OpenedPagedInventory` and the listener accessible**

`PagedInventoryListener` is in `internal` package and currently package-private. To register it from `PowerLib` (different package), make `PagedInventoryListener` public, but keep `OpenedPagedInventory` accessible only via `track(...)`/`get(...)` static methods.

Adjust visibility accordingly: `public final class PagedInventoryListener` and `public final class OpenedPagedInventory`.

- [ ] **Step 6: Write the test**

`tests/src/test/java/it/mycraft/powerlib/bukkit/inventory/PagedInventoryTest.java`:
```java
package it.mycraft.powerlib.bukkit.inventory;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PagedInventoryTest {

    private PlayerMock player;

    @BeforeEach
    void setUp() {
        MockBukkit.mock();
        player = MockBukkit.getMock().addPlayer();
    }

    @AfterEach
    void tearDown() { MockBukkit.unmock(); }

    @Test
    void firstPageRendersFirstItems() {
        List<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < 100; i++) items.add(new ItemStack(Material.STONE, 1));
        PagedInventoryBuilder.create(6, "Test").items(items).open(player);

        assertThat(player.getOpenInventory().getTopInventory().getItem(0)).isNotNull();
    }
}
```

- [ ] **Step 7: Build and test**

```bash
./mvnw clean install -DskipTests
./mvnw -pl tests test -Dtest=PagedInventoryTest
```
Expected: green.

- [ ] **Step 8: Commit**

```bash
git add bukkit/src/main/java/it/mycraft/powerlib/bukkit/inventory \
        bukkit/src/main/java/it/mycraft/powerlib/bukkit/PowerLib.java \
        tests/src/test/java/it/mycraft/powerlib/bukkit/inventory
git commit -m "feat(bukkit): add PagedInventoryBuilder with slot handlers and pagination"
```

---

## Task 15: Bump version to 1.3.0-SNAPSHOT and update README

**Files:**
- Modify: every `pom.xml` (root + 13 modules) and `bukkit-plugin/src/main/resources/plugin.yml`
- Modify: `README.md`

- [ ] **Step 1: Bump version everywhere**

Run from project root:
```bash
./mvnw versions:set -DnewVersion=1.3.0-SNAPSHOT -DgenerateBackupPoms=false -DprocessAllModules
```
Expected: every `pom.xml` updated. Verify with:
```bash
git diff --stat
```

- [ ] **Step 2: Bump `plugin.yml`**

Edit `bukkit-plugin/src/main/resources/plugin.yml`:
```yaml
version: 1.3.0-SNAPSHOT
```

- [ ] **Step 3: Update `README.md`**

Replace the version `1.2.16-SNAPSHOT` everywhere in `README.md` with `1.3.0-SNAPSHOT`. After the existing platform list (`bukkit`, `bungee`, `velocity`, `all`), add a section:

```markdown
## New modules in 1.3.0

| Module | Purpose | Min MC version |
|--------|---------|----------------|
| `powerlib-minimessage` | MiniMessage parsing helpers, MiniMessage-aware `ItemBuilder` and `Message` factories. | 1.16+ |
| `powerlib-components` | DataComponent fluent helpers (`food`, `tool`, `unbreakable`, ...). | **1.20.5+** |
| `powerlib-commands-api` | Multi-platform fluent command builder (interfaces + types). | 1.16+ |
| `powerlib-commands-bukkit` | Bukkit binding for the command builder. Detects Paper-Brigadier at runtime. | 1.16+ |
| `powerlib-commands-bungee` | BungeeCord binding. | — |
| `powerlib-commands-velocity` | Velocity binding (native Brigadier). | — |

Import them the same way as the platform modules. Each is independent — pick what you need.
```

Add a Nexo note next to the `bukkit` paragraph:

```markdown
### Nexo softdepend

PowerLib 1.3.0 ships a Nexo bridge: drop the plugin into a server with `Nexo` installed and PowerLib will fire a `NexoFurnitureInteractEvent` when players interact with Nexo furniture, plus accept the `nexo:<id>` material prefix in `ItemBuilder.setMaterial(...)`. Nexo is a `softdepend`: PowerLib runs fine without it.
```

- [ ] **Step 4: Build and test everything**

```bash
./mvnw clean install
```
Expected: every module builds and `mvn test` runs the `tests` module successfully.

- [ ] **Step 5: Verify dependency tree**

```bash
./mvnw -pl bukkit dependency:tree
```
Expected: no new mandatory runtime deps beyond what was already there. Nexo should appear as `provided`. ItemsAdder same.

- [ ] **Step 6: Commit**

```bash
git add -A
git commit -m "chore: bump version to 1.3.0-SNAPSHOT and update README with new modules"
```

---

## Self-review

**Spec coverage check:**
- Nexo bridge → Task 8 ✓
- ItemsAdder hardening → Task 6 ✓
- PDC support → Task 7 ✓
- API renames via RegistryCompat → Task 5 ✓
- Audience shading + Velocity bug fix → Task 9 ✓
- Lombok bump → Task 2 ✓
- NBT-API bump → Task 3 ✓
- Cleanup + wrapper → Task 1 ✓
- MiniMessage module → Task 10 ✓
- Components module → Task 11 ✓
- Commands modules (api + bukkit + bungee + velocity) → Tasks 12, 13 ✓
- PagedInventoryBuilder → Task 14 ✓
- Tests module → Task 4 ✓
- Version bump + README → Task 15 ✓

All design sections covered.

**Placeholder scan:** no TBD/TODO except one explicitly marked `// TODO(future)` inside `PaperBrigadierAdapter` describing the deliberate fallback-only behaviour for now. That's a documented known-limit, not a plan placeholder.

**Type consistency:** the methods named in earlier tasks are referenced consistently in later tasks (`addBuildStep`, `setPersistentData`, `RegistryCompat.glowEnchant`, `ItemsAdderBridge.extractData`, `NexoSupport.buildItem`, `MiniMessageSupport.parseLegacy`, `CommandBuilder.executor`/`getExecutor`, `PagedInventoryListener.track`).

**Known design tension flagged for executor:**
- The plan's MiniMessage path makes `Message.miniMessage(...)` use reflection so `common` doesn't depend on `minimessage`. If you prefer making `common` declare an *optional* dep on `minimessage` (with `<optional>true</optional>`), say so during execution and adjust Task 10 step 6 accordingly.
- `PaperBrigadierAdapter` currently delegates to the Bukkit fallback. Real Paper-Brigadier wiring (LifecycleEventManager + literal commands) is a follow-up. The detection scaffold is in place so swapping in the real impl later is one file.

---

## Execution Handoff

Plan complete and saved to `docs/superpowers/plans/2026-05-08-powerlib-1-3-revamp.md`. Two execution options:

**1. Subagent-Driven (recommended)** — I dispatch a fresh subagent per task, review between tasks, fast iteration.

**2. Inline Execution** — I execute the tasks in this session using `executing-plans`, with batch execution and checkpoints for review.

Which approach?
