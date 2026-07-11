package it.mycraft.powerlib.bukkit.item;

import be.seeseemelk.mockbukkit.MockBukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * MockBukkit-backed tests for {@link ItemBuilder}.
 *
 * <p>Two builder paths are deliberately not covered here because they don't run under
 * MockBukkit-v1.20:
 * <ul>
 *   <li>{@code setMaterial(int)} / {@code setMaterial(int,int)} resolve through {@link LegacyItemAPI},
 *       whose static initializer calls {@code Material.valueOf("GRASS")}. On a real 1.20 server the
 *       legacy {@code GRASS} alias still exists, but MockBukkit's Material enum renamed it to
 *       {@code SHORT_GRASS}, so the enum fails to load.</li>
 *   <li>{@code clone(ItemStack)} for non-AIR stacks constructs a {@code de.tr7zw} {@code NBTItem},
 *       which needs real NMS reflection MockBukkit does not provide. Only the null/AIR early-returns
 *       (which never reach NBT) are tested.</li>
 * </ul>
 */
class ItemBuilderTest {

    @BeforeEach
    void setUp() {
        MockBukkit.mock();
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void defaultBuildIsASingleStone() {
        ItemStack stack = new ItemBuilder().build();
        assertThat(stack.getType()).isEqualTo(Material.STONE);
        assertThat(stack.getAmount()).isEqualTo(1);
    }

    @Test
    void setMaterialFromEnum() {
        ItemStack stack = new ItemBuilder().setMaterial(Material.DIAMOND_SWORD).build();
        assertThat(stack.getType()).isEqualTo(Material.DIAMOND_SWORD);
    }

    @Test
    void setMaterialFromShortStringStaysVerbatim() {
        // A short (<=11 chars) material string is kept as-is and resolved at build time.
        ItemStack stack = new ItemBuilder().setMaterial("DIAMOND").build();
        assertThat(stack.getType()).isEqualTo(Material.DIAMOND);
    }

    @Test
    void setMaterialFromLongStringIsNormalizedViaEnum() {
        // A long (>11 chars) material string goes through the Enums.getIfPresent path.
        ItemStack stack = new ItemBuilder().setMaterial("DIAMOND_CHESTPLATE").build();
        assertThat(stack.getType()).isEqualTo(Material.DIAMOND_CHESTPLATE);
    }

    @Test
    void setMaterialFromInvalidStringFallsBackToStone() {
        ItemStack stack = new ItemBuilder().setMaterial("NOT_A_REAL_MATERIAL_NAME").build();
        assertThat(stack.getType()).isEqualTo(Material.STONE);
    }

    @Test
    void nexoMaterialStringIsKeptVerbatimAndResolvedAtBuildTime() {
        // Regression: "nexo:<id>" is longer than 11 chars and is not a Material constant, so the
        // enum-normalisation branch used to drop it and build() fell back to STONE. The prefix must
        // survive setMaterial and reach build(), which (with Nexo absent here) yields the BARRIER
        // fallback rather than a silent stone.
        ItemBuilder builder = new ItemBuilder().setMaterial("nexo:badge_card");
        assertThat(builder.getMaterial()).isEqualTo("nexo:badge_card");
        assertThat(builder.build().getType()).isEqualTo(Material.BARRIER);
    }

    @Test
    void playerHeadKeepsItsOwner() {
        // Regression: setPlayerHead stored the owner on an internal SkullMeta that build() never
        // read, so every head came out ownerless.
        OfflinePlayer owner = MockBukkit.getMock().addPlayer("Notch");
        ItemStack stack = new ItemBuilder().setPlayerHead(owner.getUniqueId()).build();

        assertThat(stack.getType()).isEqualTo(Material.PLAYER_HEAD);
        // MockBukkit's SkullMeta only round-trips the owner's name (it re-derives an offline UUID on
        // read), so the identity is asserted by name; what matters here is that an owner survives at all.
        OfflinePlayer applied = ((SkullMeta) stack.getItemMeta()).getOwningPlayer();
        assertThat(applied).isNotNull();
        assertThat(applied.getName()).isEqualTo("Notch");
    }

    @Test
    void nameIsApplied() {
        ItemStack stack = new ItemBuilder().setMaterial(Material.STONE).setName("Hello").build();
        assertThat(stack.getItemMeta().getDisplayName()).isEqualTo("Hello");
    }

    @Test
    void loreFromVarargsIsApplied() {
        ItemStack stack = new ItemBuilder().setMaterial(Material.STONE)
                .setLore("line one", "line two").build();
        assertThat(stack.getItemMeta().getLore()).containsExactly("line one", "line two");
    }

    @Test
    void loreFromListIsApplied() {
        ItemStack stack = new ItemBuilder().setMaterial(Material.STONE)
                .setLore(Arrays.asList("a", "b")).build();
        assertThat(stack.getItemMeta().getLore()).containsExactly("a", "b");
    }

    @Test
    void amountIsApplied() {
        ItemStack stack = new ItemBuilder().setMaterial(Material.STONE).setAmount(16).build();
        assertThat(stack.getAmount()).isEqualTo(16);
    }

    @Test
    void enchantmentIsApplied() {
        ItemStack stack = new ItemBuilder().setMaterial(Material.DIAMOND_SWORD)
                .setEnchantment(Enchantment.DAMAGE_ALL, 5).build();
        assertThat(stack.getItemMeta().getEnchantLevel(Enchantment.DAMAGE_ALL)).isEqualTo(5);
    }

    @Test
    void glowingAddsHiddenEnchantWhenNoOtherEnchants() {
        ItemStack stack = new ItemBuilder().setMaterial(Material.STONE).setGlowing(true).build();
        ItemMeta meta = stack.getItemMeta();
        assertThat(meta.hasEnchant(Enchantment.DURABILITY)).isTrue();
        assertThat(meta.getItemFlags()).contains(ItemFlag.HIDE_ENCHANTS);
    }

    @Test
    void glowingIsNotAddedWhenExplicitEnchantsPresent() {
        // When the builder already carries enchantments, the glow fallback is skipped.
        ItemStack stack = new ItemBuilder().setMaterial(Material.DIAMOND_SWORD)
                .setGlowing(true)
                .setEnchantment(Enchantment.DAMAGE_ALL, 1)
                .build();
        assertThat(stack.getItemMeta().getItemFlags()).isEmpty();
    }

    @Test
    void customModelDataIsApplied() {
        ItemStack stack = new ItemBuilder().setMaterial(Material.STONE)
                .setCustomModelData(1234).build();
        assertThat(stack.getItemMeta().getCustomModelData()).isEqualTo(1234);
    }

    @Test
    void placeholdersAreReplacedLiterallyInNameAndLore() {
        ItemStack stack = new ItemBuilder().setMaterial(Material.STONE)
                .setName("Coins: {count}")
                .setLore("Total: {count}")
                .addPlaceHolder("{count}", "$5 (50%)")
                .build();
        assertThat(stack.getItemMeta().getDisplayName()).isEqualTo("Coins: $5 (50%)");
        assertThat(stack.getItemMeta().getLore()).containsExactly("Total: $5 (50%)");
    }

    @Test
    void addBuildStepRunsAtBuildTime() {
        AtomicBoolean ran = new AtomicBoolean(false);
        ItemStack stack = new ItemBuilder().setMaterial(Material.STONE)
                .addBuildStep(meta -> {
                    ran.set(true);
                    meta.setDisplayName("from-step");
                })
                .build();
        assertThat(ran.get()).isTrue();
        assertThat(stack.getItemMeta().getDisplayName()).isEqualTo("from-step");
    }

    @Test
    void coloredArmorBuildSetsLeatherColor() {
        ItemStack armor = new ItemBuilder().setMaterial(Material.LEATHER_CHESTPLATE)
                .setName("Red Armor")
                .coloredArmorBuild(255, 0, 0);
        LeatherArmorMeta meta = (LeatherArmorMeta) armor.getItemMeta();
        assertThat(meta.getColor()).isEqualTo(Color.fromRGB(255, 0, 0));
        assertThat(meta.getDisplayName()).isEqualTo("Red Armor");
    }

    @Test
    void coloredArmorBuildIgnoresOutOfRangeRgb() {
        // Out-of-range RGB must be swallowed (the armor is simply left uncoloured), not thrown.
        ItemStack armor = new ItemBuilder().setMaterial(Material.LEATHER_BOOTS)
                .coloredArmorBuild(999, -1, 0);
        assertThat(armor.getType()).isEqualTo(Material.LEATHER_BOOTS);
    }

    @Test
    void cloneProducesIndependentCopy() {
        ItemBuilder original = new ItemBuilder().setMaterial(Material.DIAMOND_SWORD)
                .setName("Original")
                .setLore("orig-lore")
                .setEnchantment(Enchantment.DAMAGE_ALL, 1);
        ItemBuilder copy = original.clone();
        // Mutate the copy; the original must be untouched.
        copy.setName("Copy").setLore("copy-lore").setEnchantment(Enchantment.DURABILITY, 3);

        ItemStack origStack = original.build();
        ItemStack copyStack = copy.build();

        assertThat(origStack.getItemMeta().getDisplayName()).isEqualTo("Original");
        assertThat(origStack.getItemMeta().getLore()).containsExactly("orig-lore");
        assertThat(origStack.getItemMeta().hasEnchant(Enchantment.DURABILITY)).isFalse();

        assertThat(copyStack.getItemMeta().getDisplayName()).isEqualTo("Copy");
        assertThat(copyStack.getItemMeta().getLore()).containsExactly("copy-lore");
        assertThat(copyStack.getItemMeta().getEnchantLevel(Enchantment.DURABILITY)).isEqualTo(3);
    }

    @Test
    void cloneNullStackIsANoOp() {
        ItemStack stack = new ItemBuilder().setMaterial(Material.DIAMOND).clone(null).build();
        assertThat(stack.getType()).isEqualTo(Material.DIAMOND);
    }

    @Test
    void cloneAirStackIsANoOp() {
        ItemStack stack = new ItemBuilder().setMaterial(Material.DIAMOND)
                .clone(new ItemStack(Material.AIR)).build();
        assertThat(stack.getType()).isEqualTo(Material.DIAMOND);
    }

    @Test
    void metadataIsApplied() {
        ItemStack stack = new ItemBuilder().setMaterial(Material.STONE).setMetaData((short) 2).build();
        assertThat(stack.getDurability()).isEqualTo((short) 2);
    }

    @Test
    void gettersReflectConfiguredState() {
        ItemBuilder builder = new ItemBuilder()
                .setMaterial(Material.STONE)
                .setName("N")
                .setLore("L")
                .setAmount(5)
                .setCustomModelData(7)
                .setGlowing(true);
        assertThat(builder.getMaterial()).isEqualTo("STONE");
        assertThat(builder.getName()).isEqualTo("N");
        assertThat(builder.getLore()).containsExactly("L");
        assertThat(builder.getAmount()).isEqualTo(5);
        assertThat(builder.getCustomModelData()).isEqualTo(7);
        assertThat(builder.isGlowing()).isTrue();
    }

    @Test
    void buildingTwiceIsStable() {
        ItemBuilder builder = new ItemBuilder().setMaterial(Material.STONE).setName("Repeat");
        ItemStack first = builder.build();
        ItemStack second = builder.build();
        assertThat(first.getType()).isEqualTo(second.getType());
        assertThat(second.getItemMeta().getDisplayName()).isEqualTo("Repeat");
    }

    @Test
    void nullPlaceholderValueRendersAsSentinelWithoutThrowing() {
        // Regression: a placeholder key present with a null value (e.g. OfflinePlayer#getName())
        // used to NPE in build(), because getOrDefault only guards the absent-key case.
        ItemStack stack = new ItemBuilder()
                .setMaterial(Material.PAPER)
                .setName("Owner: %owner%")
                .setLore("Holder: %owner%")
                .addPlaceHolder("%owner%", null)
                .build();

        ItemMeta meta = stack.getItemMeta();
        assertThat(meta.getDisplayName()).isEqualTo("Owner: NULL");
        assertThat(meta.getLore()).containsExactly("Holder: NULL");
    }
}
