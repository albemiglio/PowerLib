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
