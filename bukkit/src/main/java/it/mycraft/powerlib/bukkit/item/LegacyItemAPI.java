package it.mycraft.powerlib.bukkit.item;

import lombok.Getter;
import org.bukkit.Material;

/**
 * Lookup table mapping pre-1.13 numeric item IDs (and {@code id:data} variants) to their modern
 * {@link Material} and Minecraft resource name. Each constant records the legacy total ID, numeric ID,
 * data value, the modern Bukkit material, and the Minecraft name.
 */
public enum LegacyItemAPI {
    /** Legacy id {@code 2266} ({@code minecraft:record_11}) maps to {@link Material#MUSIC_DISC_11}. */
    MUSIC_DISC_11("2266", 2266, 0, "MUSIC_DISC_11", "record_11"),
    /** Legacy id {@code 2256} ({@code minecraft:record_13}) maps to {@link Material#MUSIC_DISC_13}. */
    MUSIC_DISC_13("2256", 2256, 0, "MUSIC_DISC_13", "record_13"),
    /** Legacy id {@code 447} ({@code minecraft:acacia_boat}) maps to {@link Material#ACACIA_BOAT}. */
    ACACIA_BOAT("447", 447, 0, "ACACIA_BOAT", "acacia_boat"),
    /** Legacy id {@code 430} ({@code minecraft:acacia_door}) maps to {@link Material#ACACIA_DOOR}. */
    ACACIA_DOOR("430", 430, 0, "ACACIA_DOOR", "acacia_door"),
    /** Legacy id {@code 196} ({@code minecraft:acacia_door}) maps to {@link Material#ACACIA_DOOR}. */
    ACACIA_DOOR_BLOCK("196", 196, 0, "ACACIA_DOOR", "acacia_door"),
    /** Legacy id {@code 192} ({@code minecraft:acacia_fence}) maps to {@link Material#ACACIA_FENCE}. */
    ACACIA_FENCE("192", 192, 0, "ACACIA_FENCE", "acacia_fence"),
    /** Legacy id {@code 187} ({@code minecraft:acacia_fence_gate}) maps to {@link Material#ACACIA_FENCE_GATE}. */
    ACACIA_FENCE_GATE("187", 187, 0, "ACACIA_FENCE_GATE", "acacia_fence_gate"),
    /** Legacy id {@code 161} ({@code minecraft:leaves2}) maps to {@link Material#ACACIA_LEAVES}. */
    ACACIA_LEAVES("161", 161, 0, "ACACIA_LEAVES", "leaves2"),
    /** Legacy id {@code 6:4} ({@code minecraft:sapling}) maps to {@link Material#ACACIA_SAPLING}. */
    ACACIA_SAPLING("6:4", 6, 4, "ACACIA_SAPLING", "sapling"),
    /** Legacy id {@code 162} ({@code minecraft:log2}) maps to {@link Material#ACACIA_WOOD}. */
    ACACIA_WOOD("162", 162, 0, "ACACIA_WOOD", "log2"),
    /** Legacy id {@code 5:4} ({@code minecraft:planks}) maps to {@link Material#ACACIA_PLANKS}. */
    ACACIA_WOOD_PLANK("5:4", 5, 4, "ACACIA_PLANKS", "planks"),
    /** Legacy id {@code 126:4} ({@code minecraft:wooden_slab}) maps to {@link Material#ACACIA_SLAB}. */
    ACACIA_WOOD_SLAB("126:4", 126, 4, "ACACIA_SLAB", "wooden_slab"),
    /** Legacy id {@code 163} ({@code minecraft:acacia_stairs}) maps to {@link Material#ACACIA_STAIRS}. */
    ACACIA_WOOD_STAIRS("163", 163, 0, "ACACIA_STAIRS", "acacia_stairs"),
    /** Legacy id {@code 157} ({@code minecraft:activator_rail}) maps to {@link Material#ACTIVATOR_RAIL}. */
    ACTIVATOR_RAIL("157", 157, 0, "ACTIVATOR_RAIL", "activator_rail"),
    /** Legacy id {@code 0} ({@code minecraft:air}) maps to {@link Material#AIR}. */
    AIR("0", 0, 0, "AIR", "air"),
    /** Legacy id {@code 38:2} ({@code minecraft:red_flower}) maps to {@link Material#ALLIUM}. */
    ALLIUM("38:2", 38, 2, "ALLIUM", "red_flower"),
    /** Legacy id {@code 1:5} ({@code minecraft:stone}) maps to {@link Material#ANDESITE}. */
    ANDESITE("1:5", 1, 5, "ANDESITE", "stone"),
    /** Legacy id {@code 145} ({@code minecraft:anvil}) maps to {@link Material#ANVIL}. */
    ANVIL("145", 145, 0, "ANVIL", "anvil"),
    /** Legacy id {@code 260} ({@code minecraft:apple}) maps to {@link Material#APPLE}. */
    APPLE("260", 260, 0, "APPLE", "apple"),
    /** Legacy id {@code 416} ({@code minecraft:armor_stand}) maps to {@link Material#ARMOR_STAND}. */
    ARMOR_STAND("416", 416, 0, "ARMOR_STAND", "armor_stand"),
    /** Legacy id {@code 262} ({@code minecraft:arrow}) maps to {@link Material#ARROW}. */
    ARROW("262", 262, 0, "ARROW", "arrow"),
    /** Legacy id {@code 38:3} ({@code minecraft:red_flower}) maps to {@link Material#AZURE_BLUET}. */
    AZURE_BLUET("38:3", 38, 3, "AZURE_BLUET", "red_flower"),
    /** Legacy id {@code 393} ({@code minecraft:baked_potato}) maps to {@link Material#BAKED_POTATO}. */
    BAKED_POTATO("393", 393, 0, "BAKED_POTATO", "baked_potato"),
    /** Legacy id {@code 425} ({@code minecraft:banner}) maps to {@link Material#BLACK_BANNER}. */
    BANNER("425", 425, 0, "BLACK_BANNER", "banner"),
    /** Legacy id {@code 166} ({@code minecraft:barrier}) maps to {@link Material#BARRIER}. */
    BARRIER("166", 166, 0, "BARRIER", "barrier"),
    /** Legacy id {@code 138} ({@code minecraft:beacon}) maps to {@link Material#BEACON}. */
    BEACON("138", 138, 0, "BEACON", "beacon"),
    /** Legacy id {@code 26} ({@code minecraft:bed}) maps to {@link Material#BLACK_BED}. */
    BED_BLOCK("26", 26, 0, "BLACK_BED", "bed"),
    /** Legacy id {@code 355} ({@code minecraft:bed}) maps to {@link Material#BLACK_BED}. */
    BED("355", 355, 0, "BLACK_BED", "bed"),
    /** Legacy id {@code 7} ({@code minecraft:bedrock}) maps to {@link Material#BEDROCK}. */
    BEDROCK("7", 7, 0, "BEDROCK", "bedrock"),
    /** Legacy id {@code 434} ({@code minecraft:beetroot}) maps to {@link Material#BEETROOT}. */
    BEETROOT("434", 434, 0, "BEETROOT", "beetroot"),
    /** Legacy id {@code 207} ({@code minecraft:beetroots}) maps to {@link Material#BEETROOTS}. */
    BEETROOT_BLOCK("207", 207, 0, "BEETROOTS", "beetroots"),
    /** Legacy id {@code 435} ({@code minecraft:beetroot_seeds}) maps to {@link Material#BEETROOT_SEEDS}. */
    BEETROOT_SEEDS("435", 435, 0, "BEETROOT_SEEDS", "beetroot_seeds"),
    /** Legacy id {@code 436} ({@code minecraft:beetroot_soup}) maps to {@link Material#BEETROOT_SOUP}. */
    BEETROOT_SOUP("436", 436, 0, "BEETROOT_SOUP", "beetroot_soup"),
    /** Legacy id {@code 445} ({@code minecraft:birch_boat}) maps to {@link Material#BIRCH_BOAT}. */
    BIRCH_BOAT("445", 445, 0, "BIRCH_BOAT", "birch_boat"),
    /** Legacy id {@code 428} ({@code minecraft:birch_door}) maps to {@link Material#BIRCH_DOOR}. */
    BIRCH_DOOR("428", 428, 0, "BIRCH_DOOR", "birch_door"),
    /** Legacy id {@code 194} ({@code minecraft:birch_door}) maps to {@link Material#BIRCH_DOOR}. */
    BIRCH_DOOR_BLOCK("194", 194, 0, "BIRCH_DOOR", "birch_door"),
    /** Legacy id {@code 189} ({@code minecraft:birch_fence}) maps to {@link Material#BIRCH_FENCE}. */
    BIRCH_FENCE("189", 189, 0, "BIRCH_FENCE", "birch_fence"),
    /** Legacy id {@code 184} ({@code minecraft:birch_fence_gate}) maps to {@link Material#BIRCH_FENCE_GATE}. */
    BIRCH_FENCE_GATE("184", 184, 0, "BIRCH_FENCE_GATE", "birch_fence_gate"),
    /** Legacy id {@code 18:2} ({@code minecraft:leaves}) maps to {@link Material#BIRCH_LEAVES}. */
    BIRCH_LEAVES("18:2", 18, 2, "BIRCH_LEAVES", "leaves"),
    /** Legacy id {@code 6:2} ({@code minecraft:sapling}) maps to {@link Material#BIRCH_SAPLING}. */
    BIRCH_SAPLING("6:2", 6, 2, "BIRCH_SAPLING", "sapling"),
    /** Legacy id {@code 17:2} ({@code minecraft:log}) maps to {@link Material#BIRCH_WOOD}. */
    BIRCH_WOOD("17:2", 17, 2, "BIRCH_WOOD", "log"),
    /** Legacy id {@code 5:2} ({@code minecraft:planks}) maps to {@link Material#BIRCH_PLANKS}. */
    BIRCH_WOOD_PLANK("5:2", 5, 2, "BIRCH_PLANKS", "planks"),
    /** Legacy id {@code 126:2} ({@code minecraft:wooden_slab}) maps to {@link Material#BIRCH_SLAB}. */
    BIRCH_WOOD_SLAB("126:2", 126, 2, "BIRCH_SLAB", "wooden_slab"),
    /** Legacy id {@code 135} ({@code minecraft:birch_stairs}) maps to {@link Material#BIRCH_STAIRS}. */
    BIRCH_WOOD_STAIRS("135", 135, 0, "BIRCH_STAIRS", "birch_stairs"),
    /** Legacy id {@code 171:15} ({@code minecraft:carpet}) maps to {@link Material#BLACK_CARPET}. */
    BLACK_CARPET("171:15", 171, 15, "BLACK_CARPET", "carpet"),
    /** Legacy id {@code 251:15} ({@code minecraft:concrete}) maps to {@link Material#BLACK_CONCRETE}. */
    BLACK_CONCRETE("251:15", 251, 15, "BLACK_CONCRETE", "concrete"),
    /** Legacy id {@code 252:15} ({@code minecraft:concrete_powder}) maps to {@link Material#BLACK_CONCRETE_POWDER}. */
    BLACK_CONCRETE_POWDER("252:15", 252, 15, "BLACK_CONCRETE_POWDER", "concrete_powder"),
    /** Legacy id {@code 250} ({@code minecraft:black_glazed_terracotta}) maps to {@link Material#BLACK_GLAZED_TERRACOTTA}. */
    BLACK_GLAZED_TERRACOTTA("250", 250, 0, "BLACK_GLAZED_TERRACOTTA", "black_glazed_terracotta"),
    /** Legacy id {@code 159:15} ({@code minecraft:stained_hardened_clay}) maps to {@link Material#BLACK_TERRACOTTA}. */
    BLACK_HARDENED_CLAY("159:15", 159, 15, "BLACK_TERRACOTTA", "stained_hardened_clay"),
    /** Legacy id {@code 234} ({@code minecraft:black_shulker_box}) maps to {@link Material#BLACK_SHULKER_BOX}. */
    BLACK_SHULKER_BOX("234", 234, 0, "BLACK_SHULKER_BOX", "black_shulker_box"),
    /** Legacy id {@code 95:15} ({@code minecraft:stained_glass}) maps to {@link Material#BLACK_STAINED_GLASS}. */
    BLACK_STAINED_GLASS("95:15", 95, 15, "BLACK_STAINED_GLASS", "stained_glass"),
    /** Legacy id {@code 160:15} ({@code minecraft:stained_glass_pane}) maps to {@link Material#BLACK_STAINED_GLASS_PANE}. */
    BLACK_STAINED_GLASS_PANE("160:15", 160, 15, "BLACK_STAINED_GLASS_PANE", "stained_glass_pane"),
    /** Legacy id {@code 35:15} ({@code minecraft:wool}) maps to {@link Material#BLACK_WOOL}. */
    BLACK_WOOL("35:15", 35, 15, "BLACK_WOOL", "wool"),
    /** Legacy id {@code 377} ({@code minecraft:blaze_powder}) maps to {@link Material#BLAZE_POWDER}. */
    BLAZE_POWDER("377", 377, 0, "BLAZE_POWDER", "blaze_powder"),
    /** Legacy id {@code 369} ({@code minecraft:blaze_rod}) maps to {@link Material#BLAZE_ROD}. */
    BLAZE_ROD("369", 369, 0, "BLAZE_ROD", "blaze_rod"),
    /** Legacy id {@code 173} ({@code minecraft:coal_block}) maps to {@link Material#COAL_BLOCK}. */
    BLOCK_OF_COAL("173", 173, 0, "COAL_BLOCK", "coal_block"),
    /** Legacy id {@code 2258} ({@code minecraft:record_blocks}) maps to {@link Material#MUSIC_DISC_BLOCKS}. */
    BLOCKS_DISC("2258", 2258, 0, "MUSIC_DISC_BLOCKS", "record_blocks"),
    /** Legacy id {@code 171:11} ({@code minecraft:carpet}) maps to {@link Material#BLUE_CARPET}. */
    BLUE_CARPET("171:11", 171, 11, "BLUE_CARPET", "carpet"),
    /** Legacy id {@code 251:11} ({@code minecraft:concrete}) maps to {@link Material#BLUE_CONCRETE}. */
    BLUE_CONCRETE("251:11", 251, 11, "BLUE_CONCRETE", "concrete"),
    /** Legacy id {@code 252:11} ({@code minecraft:concrete_powder}) maps to {@link Material#BLUE_CONCRETE_POWDER}. */
    BLUE_CONCRETE_POWDER("252:11", 252, 11, "BLUE_CONCRETE_POWDER", "concrete_powder"),
    /** Legacy id {@code 246} ({@code minecraft:blue_glazed_terracotta}) maps to {@link Material#BLUE_GLAZED_TERRACOTTA}. */
    BLUE_GLAZED_TERRACOTTA("246", 246, 0, "BLUE_GLAZED_TERRACOTTA", "blue_glazed_terracotta"),
    /** Legacy id {@code 159:11} ({@code minecraft:stained_hardened_clay}) maps to {@link Material#BLUE_TERRACOTTA}. */
    BLUE_HARDENED_CLAY("159:11", 159, 11, "BLUE_TERRACOTTA", "stained_hardened_clay"),
    /** Legacy id {@code 38:1} ({@code minecraft:red_flower}) maps to {@link Material#BLUE_ORCHID}. */
    BLUE_ORCHID("38:1", 38, 1, "BLUE_ORCHID", "red_flower"),
    /** Legacy id {@code 230} ({@code minecraft:blue_shulker_box}) maps to {@link Material#BLUE_SHULKER_BOX}. */
    BLUE_SHULKER_BOX("230", 230, 0, "BLUE_SHULKER_BOX", "blue_shulker_box"),
    /** Legacy id {@code 95:11} ({@code minecraft:stained_glass}) maps to {@link Material#BLUE_STAINED_GLASS}. */
    BLUE_STAINED_GLASS("95:11", 95, 11, "BLUE_STAINED_GLASS", "stained_glass"),
    /** Legacy id {@code 160:11} ({@code minecraft:stained_glass_pane}) maps to {@link Material#BLUE_STAINED_GLASS_PANE}. */
    BLUE_STAINED_GLASS_PANE("160:11", 160, 11, "BLUE_STAINED_GLASS_PANE", "stained_glass_pane"),
    /** Legacy id {@code 35:11} ({@code minecraft:wool}) maps to {@link Material#BLUE_WOOL}. */
    BLUE_WOOL("35:11", 35, 11, "BLUE_WOOL", "wool"),
    /** Legacy id {@code 352} ({@code minecraft:bone}) maps to {@link Material#BONE}. */
    BONE("352", 352, 0, "BONE", "bone"),
    /** Legacy id {@code 216} ({@code minecraft:bone_block}) maps to {@link Material#BONE_BLOCK}. */
    BONE_BLOCK("216", 216, 0, "BONE_BLOCK", "bone_block"),
    /** Legacy id {@code 351:15} ({@code minecraft:dye}) maps to {@link Material#BONE_MEAL}. */
    BONE_MEAL("351:15", 351, 15, "BONE_MEAL", "dye"),
    /** Legacy id {@code 340} ({@code minecraft:book}) maps to {@link Material#BOOK}. */
    BOOK("340", 340, 0, "BOOK", "book"),
    /** Legacy id {@code 386} ({@code minecraft:writable_book}) maps to {@link Material#WRITABLE_BOOK}. */
    BOOK_AND_QUILL("386", 386, 0, "WRITABLE_BOOK", "writable_book"),
    /** Legacy id {@code 47} ({@code minecraft:bookshelf}) maps to {@link Material#BOOKSHELF}. */
    BOOKSHELF("47", 47, 0, "BOOKSHELF", "bookshelf"),
    /** Legacy id {@code 384} ({@code minecraft:experience_bottle}) maps to {@link Material#EXPERIENCE_BOTTLE}. */
    BOTTLE_O_ENCHANTING("384", 384, 0, "EXPERIENCE_BOTTLE", "experience_bottle"),
    /** Legacy id {@code 261} ({@code minecraft:bow}) maps to {@link Material#BOW}. */
    BOW("261", 261, 0, "BOW", "bow"),
    /** Legacy id {@code 281} ({@code minecraft:bowl}) maps to {@link Material#BOWL}. */
    BOWL("281", 281, 0, "BOWL", "bowl"),
    /** Legacy id {@code 297} ({@code minecraft:bread}) maps to {@link Material#BREAD}. */
    BREAD("297", 297, 0, "BREAD", "bread"),
    /** Legacy id {@code 117} ({@code minecraft:brewing_stand}) maps to {@link Material#BREWING_STAND}. */
    BREWING_STAND_BLOCK("117", 117, 0, "BREWING_STAND", "brewing_stand"),
    /** Legacy id {@code 379} ({@code minecraft:brewing_stand}) maps to {@link Material#BREWING_STAND}. */
    BREWING_STAND("379", 379, 0, "BREWING_STAND", "brewing_stand"),
    /** Legacy id {@code 336} ({@code minecraft:brick}) maps to {@link Material#BRICK}. */
    BRICK("336", 336, 0, "BRICK", "brick"),
    /** Legacy id {@code 44:4} ({@code minecraft:stone_slab}) maps to {@link Material#BRICK_SLAB}. */
    BRICK_SLAB("44:4", 44, 4, "BRICK_SLAB", "stone_slab"),
    /** Legacy id {@code 108} ({@code minecraft:brick_stairs}) maps to {@link Material#BRICK_STAIRS}. */
    BRICK_STAIRS("108", 108, 0, "BRICK_STAIRS", "brick_stairs"),
    /** Legacy id {@code 45} ({@code minecraft:brick_block}) maps to {@link Material#BRICKS}. */
    BRICKS("45", 45, 0, "BRICKS", "brick_block"),
    /** Legacy id {@code 171:12} ({@code minecraft:carpet}) maps to {@link Material#BROWN_CARPET}. */
    BROWN_CARPET("171:12", 171, 12, "BROWN_CARPET", "carpet"),
    /** Legacy id {@code 251:12} ({@code minecraft:concrete}) maps to {@link Material#BROWN_CONCRETE}. */
    BROWN_CONCRETE("251:12", 251, 12, "BROWN_CONCRETE", "concrete"),
    /** Legacy id {@code 252:12} ({@code minecraft:concrete_powder}) maps to {@link Material#BROWN_CONCRETE_POWDER}. */
    BROWN_CONCRETE_POWDER("252:12", 252, 12, "BROWN_CONCRETE_POWDER", "concrete_powder"),
    /** Legacy id {@code 247} ({@code minecraft:brown_glazed_terracotta}) maps to {@link Material#BROWN_GLAZED_TERRACOTTA}. */
    BROWN_GLAZED_TERRACOTTA("247", 247, 0, "BROWN_GLAZED_TERRACOTTA", "brown_glazed_terracotta"),
    /** Legacy id {@code 159:12} ({@code minecraft:stained_hardened_clay}) maps to {@link Material#BROWN_TERRACOTTA}. */
    BROWN_HARDENED_CLAY("159:12", 159, 12, "BROWN_TERRACOTTA", "stained_hardened_clay"),
    /** Legacy id {@code 39} ({@code minecraft:brown_mushroom}) maps to {@link Material#BROWN_MUSHROOM}. */
    BROWN_MUSHROOM("39", 39, 0, "BROWN_MUSHROOM", "brown_mushroom"),
    /** Legacy id {@code 99} ({@code minecraft:brown_mushroom_block}) maps to {@link Material#BROWN_MUSHROOM_BLOCK}. */
    BROWN_MUSHROOM_BLOCK("99", 99, 0, "BROWN_MUSHROOM_BLOCK", "brown_mushroom_block"),
    /** Legacy id {@code 231} ({@code minecraft:brown_shulker_box}) maps to {@link Material#BROWN_SHULKER_BOX}. */
    BROWN_SHULKER_BOX("231", 231, 0, "BROWN_SHULKER_BOX", "brown_shulker_box"),
    /** Legacy id {@code 95:12} ({@code minecraft:stained_glass}) maps to {@link Material#BROWN_STAINED_GLASS}. */
    BROWN_STAINED_GLASS("95:12", 95, 12, "BROWN_STAINED_GLASS", "stained_glass"),
    /** Legacy id {@code 160:12} ({@code minecraft:stained_glass_pane}) maps to {@link Material#BROWN_STAINED_GLASS_PANE}. */
    BROWN_STAINED_GLASS_PANE("160:12", 160, 12, "BROWN_STAINED_GLASS_PANE", "stained_glass_pane"),
    /** Legacy id {@code 35:12} ({@code minecraft:wool}) maps to {@link Material#BROWN_WOOL}. */
    BROWN_WOOL("35:12", 35, 12, "BROWN_WOOL", "wool"),
    /** Legacy id {@code 325} ({@code minecraft:bucket}) maps to {@link Material#BUCKET}. */
    BUCKET("325", 325, 0, "BUCKET", "bucket"),
    /** Legacy id {@code 62} ({@code minecraft:lit_furnace}) maps to {@link Material#LEGACY_BURNING_FURNACE}. */
    BURNING_FURNACE("62", 62, 0, "LEGACY_BURNING_FURNACE", "lit_furnace"),
    /** Legacy id {@code 81} ({@code minecraft:cactus}) maps to {@link Material#CACTUS}. */
    CACTUS("81", 81, 0, "CACTUS", "cactus"),
    /** Legacy id {@code 351:2} ({@code minecraft:dye}) maps to {@link Material#GREEN_DYE}. */
    CACTUS_GREEN("351:2", 351, 2, "GREEN_DYE", "dye"),
    /** Legacy id {@code 354} ({@code minecraft:cake}) maps to {@link Material#CAKE}. */
    CAKE("354", 354, 0, "CAKE", "cake"),
    /** Legacy id {@code 92} ({@code minecraft:cake}) maps to {@link Material#CAKE}. */
    CAKE_BLOCK("92", 92, 0, "CAKE", "cake"),
    /** Legacy id {@code 391} ({@code minecraft:carrot}) maps to {@link Material#CARROT}. */
    CARROT("391", 391, 0, "CARROT", "carrot"),
    /** Legacy id {@code 398} ({@code minecraft:carrot_on_a_stick}) maps to {@link Material#CARROT_ON_A_STICK}. */
    CARROT_ON_A_STICK("398", 398, 0, "CARROT_ON_A_STICK", "carrot_on_a_stick"),
    /** Legacy id {@code 141} ({@code minecraft:carrots}) maps to {@link Material#CARROTS}. */
    CARROTS("141", 141, 0, "CARROTS", "carrots"),
    /** Legacy id {@code 2257} ({@code minecraft:record_cat}) maps to {@link Material#MUSIC_DISC_CAT}. */
    CAT_DISC("2257", 2257, 0, "MUSIC_DISC_CAT", "record_cat"),
    /** Legacy id {@code 118} ({@code minecraft:cauldron}) maps to {@link Material#CAULDRON}. */
    CAULDRON_BLOCK("118", 118, 0, "CAULDRON", "cauldron"),
    /** Legacy id {@code 380} ({@code minecraft:cauldron}) maps to {@link Material#CAULDRON}. */
    CAULDRON("380", 380, 0, "CAULDRON", "cauldron"),
    /** Legacy id {@code 211} ({@code minecraft:chain_command_block}) maps to {@link Material#CHAIN_COMMAND_BLOCK}. */
    CHAIN_COMMAND_BLOCK("211", 211, 0, "CHAIN_COMMAND_BLOCK", "chain_command_block"),
    /** Legacy id {@code 305} ({@code minecraft:chainmail_boots}) maps to {@link Material#CHAINMAIL_BOOTS}. */
    CHAINMAIL_BOOTS("305", 305, 0, "CHAINMAIL_BOOTS", "chainmail_boots"),
    /** Legacy id {@code 303} ({@code minecraft:chainmail_chestplate}) maps to {@link Material#CHAINMAIL_CHESTPLATE}. */
    CHAINMAIL_CHESTPLATE("303", 303, 0, "CHAINMAIL_CHESTPLATE", "chainmail_chestplate"),
    /** Legacy id {@code 302} ({@code minecraft:chainmail_helmet}) maps to {@link Material#CHAINMAIL_HELMET}. */
    CHAINMAIL_HELMET("302", 302, 0, "CHAINMAIL_HELMET", "chainmail_helmet"),
    /** Legacy id {@code 304} ({@code minecraft:chainmail_leggings}) maps to {@link Material#CHAINMAIL_LEGGINGS}. */
    CHAINMAIL_LEGGINGS("304", 304, 0, "CHAINMAIL_LEGGINGS", "chainmail_leggings"),
    /** Legacy id {@code 263:1} ({@code minecraft:coal}) maps to {@link Material#CHARCOAL}. */
    CHARCOAL("263:1", 263, 1, "CHARCOAL", "coal"),
    /** Legacy id {@code 54} ({@code minecraft:chest}) maps to {@link Material#CHEST}. */
    CHEST("54", 54, 0, "CHEST", "chest"),
    /** Legacy id {@code 2259} ({@code minecraft:record_chirp}) maps to {@link Material#MUSIC_DISC_CHIRP}. */
    CHIRP_DISC("2259", 2259, 0, "MUSIC_DISC_CHIRP", "record_chirp"),
    /** Legacy id {@code 155:1} ({@code minecraft:quartz_block}) maps to {@link Material#CHISELED_QUARTZ_BLOCK}. */
    CHISELED_QUARTZ_BLOCK("155:1", 155, 1, "CHISELED_QUARTZ_BLOCK", "quartz_block"),
    /** Legacy id {@code 179:1} ({@code minecraft:red_sandstone}) maps to {@link Material#CHISELED_RED_SANDSTONE}. */
    CHISELED_RED_SANDSTONE("179:1", 179, 1, "CHISELED_RED_SANDSTONE", "red_sandstone"),
    /** Legacy id {@code 24:1} ({@code minecraft:sandstone}) maps to {@link Material#CHISELED_SANDSTONE}. */
    CHISELED_SANDSTONE("24:1", 24, 1, "CHISELED_SANDSTONE", "sandstone"),
    /** Legacy id {@code 97:5} ({@code minecraft:monster_egg}) maps to {@link Material#INFESTED_CHISELED_STONE_BRICKS}. */
    CHISELED_STONE_BRICK_MONSTER_EGG("97:5", 97, 5, "INFESTED_CHISELED_STONE_BRICKS", "monster_egg"),
    /** Legacy id {@code 98:3} ({@code minecraft:stonebrick}) maps to {@link Material#CHISELED_STONE_BRICKS}. */
    CHISELED_STONE_BRICKS("98:3", 98, 3, "CHISELED_STONE_BRICKS", "stonebrick"),
    /** Legacy id {@code 200} ({@code minecraft:chorus_flower}) maps to {@link Material#CHORUS_FLOWER}. */
    CHORUS_FLOWER("200", 200, 0, "CHORUS_FLOWER", "chorus_flower"),
    /** Legacy id {@code 432} ({@code minecraft:chorus_fruit}) maps to {@link Material#CHORUS_FRUIT}. */
    CHORUS_FRUIT("432", 432, 0, "CHORUS_FRUIT", "chorus_fruit"),
    /** Legacy id {@code 199} ({@code minecraft:chorus_plant}) maps to {@link Material#CHORUS_PLANT}. */
    CHORUS_PLANT("199", 199, 0, "CHORUS_PLANT", "chorus_plant"),
    /** Legacy id {@code 337} ({@code minecraft:clay_ball}) maps to {@link Material#CLAY_BALL}. */
    CLAY("337", 337, 0, "CLAY_BALL", "clay_ball"),
    /** Legacy id {@code 82} ({@code minecraft:clay}) maps to {@link Material#CLAY}. */
    CLAY_BLOCK("82", 82, 0, "CLAY", "clay"),
    /** Legacy id {@code 347} ({@code minecraft:clock}) maps to {@link Material#CLOCK}. */
    CLOCK("347", 347, 0, "CLOCK", "clock"),
    /** Legacy id {@code 349:2} ({@code minecraft:fish}) maps to {@link Material#TROPICAL_FISH}. */
    CLOWNFISH("349:2", 349, 2, "TROPICAL_FISH", "fish"),
    /** Legacy id {@code 263} ({@code minecraft:coal}) maps to {@link Material#COAL}. */
    COAL("263", 263, 0, "COAL", "coal"),
    /** Legacy id {@code 16} ({@code minecraft:coal_ore}) maps to {@link Material#COAL_ORE}. */
    COAL_ORE("16", 16, 0, "COAL_ORE", "coal_ore"),
    /** Legacy id {@code 3:1} ({@code minecraft:dirt}) maps to {@link Material#COARSE_DIRT}. */
    COARSE_DIRT("3:1", 3, 1, "COARSE_DIRT", "dirt"),
    /** Legacy id {@code 4} ({@code minecraft:cobblestone}) maps to {@link Material#COBBLESTONE}. */
    COBBLESTONE("4", 4, 0, "COBBLESTONE", "cobblestone"),
    /** Legacy id {@code 97:1} ({@code minecraft:monster_egg}) maps to {@link Material#INFESTED_COBBLESTONE}. */
    COBBLESTONE_MONSTER_EGG("97:1", 97, 1, "INFESTED_COBBLESTONE", "monster_egg"),
    /** Legacy id {@code 44:3} ({@code minecraft:stone_slab}) maps to {@link Material#COBBLESTONE_SLAB}. */
    COBBLESTONE_SLAB("44:3", 44, 3, "COBBLESTONE_SLAB", "stone_slab"),
    /** Legacy id {@code 67} ({@code minecraft:stone_stairs}) maps to {@link Material#COBBLESTONE_STAIRS}. */
    COBBLESTONE_STAIRS("67", 67, 0, "COBBLESTONE_STAIRS", "stone_stairs"),
    /** Legacy id {@code 139} ({@code minecraft:cobblestone_wall}) maps to {@link Material#COBBLESTONE_WALL}. */
    COBBLESTONE_WALL("139", 139, 0, "COBBLESTONE_WALL", "cobblestone_wall"),
    /** Legacy id {@code 30} ({@code minecraft:web}) maps to {@link Material#COBWEB}. */
    COBWEB("30", 30, 0, "COBWEB", "web"),
    /** Legacy id {@code 351:3} ({@code minecraft:dye}) maps to {@link Material#COCOA_BEANS}. */
    COCOA_BEANS("351:3", 351, 3, "COCOA_BEANS", "dye"),
    /** Legacy id {@code 127} ({@code minecraft:cocoa}) maps to {@link Material#COCOA}. */
    COCOA("127", 127, 0, "COCOA", "cocoa"),
    /** Legacy id {@code 137} ({@code minecraft:command_block}) maps to {@link Material#COMMAND_BLOCK}. */
    COMMAND_BLOCK("137", 137, 0, "COMMAND_BLOCK", "command_block"),
    /** Legacy id {@code 345} ({@code minecraft:compass}) maps to {@link Material#COMPASS}. */
    COMPASS("345", 345, 0, "COMPASS", "compass"),
    /** Legacy id {@code 366} ({@code minecraft:cooked_chicken}) maps to {@link Material#COOKED_CHICKEN}. */
    COOKED_CHICKEN("366", 366, 0, "COOKED_CHICKEN", "cooked_chicken"),
    /** Legacy id {@code 350} ({@code minecraft:cooked_fish}) maps to {@link Material#COOKED_COD}. */
    COOKED_FISH("350", 350, 0, "COOKED_COD", "cooked_fish"),
    /** Legacy id {@code 424} ({@code minecraft:cooked_mutton}) maps to {@link Material#COOKED_MUTTON}. */
    COOKED_MUTTON("424", 424, 0, "COOKED_MUTTON", "cooked_mutton"),
    /** Legacy id {@code 320} ({@code minecraft:cooked_porkchop}) maps to {@link Material#COOKED_PORKCHOP}. */
    COOKED_PORKCHOP("320", 320, 0, "COOKED_PORKCHOP", "cooked_porkchop"),
    /** Legacy id {@code 412} ({@code minecraft:cooked_rabbit}) maps to {@link Material#COOKED_RABBIT}. */
    COOKED_RABBIT("412", 412, 0, "COOKED_RABBIT", "cooked_rabbit"),
    /** Legacy id {@code 350:1} ({@code minecraft:cooked_fish}) maps to {@link Material#COOKED_SALMON}. */
    COOKED_SALMON("350:1", 350, 1, "COOKED_SALMON", "cooked_fish"),
    /** Legacy id {@code 357} ({@code minecraft:cookie}) maps to {@link Material#COOKIE}. */
    COOKIE("357", 357, 0, "COOKIE", "cookie"),
    /** Legacy id {@code 97:4} ({@code minecraft:monster_egg}) maps to {@link Material#INFESTED_CRACKED_STONE_BRICKS}. */
    CRACKED_STONE_BRICK_MONSTER_EGG("97:4", 97, 4, "INFESTED_CRACKED_STONE_BRICKS", "monster_egg"),
    /** Legacy id {@code 98:2} ({@code minecraft:stonebrick}) maps to {@link Material#CRACKED_STONE_BRICKS}. */
    CRACKED_STONE_BRICKS("98:2", 98, 2, "CRACKED_STONE_BRICKS", "stonebrick"),
    /** Legacy id {@code 58} ({@code minecraft:crafting_table}) maps to {@link Material#CRAFTING_TABLE}. */
    CRAFTING_TABLE("58", 58, 0, "CRAFTING_TABLE", "crafting_table"),
    /** Legacy id {@code 171:9} ({@code minecraft:carpet}) maps to {@link Material#CYAN_CARPET}. */
    CYAN_CARPET("171:9", 171, 9, "CYAN_CARPET", "carpet"),
    /** Legacy id {@code 251:9} ({@code minecraft:concrete}) maps to {@link Material#CYAN_CONCRETE}. */
    CYAN_CONCRETE("251:9", 251, 9, "CYAN_CONCRETE", "concrete"),
    /** Legacy id {@code 252:9} ({@code minecraft:concrete_powder}) maps to {@link Material#CYAN_CONCRETE_POWDER}. */
    CYAN_CONCRETE_POWDER("252:9", 252, 9, "CYAN_CONCRETE_POWDER", "concrete_powder"),
    /** Legacy id {@code 351:6} ({@code minecraft:dye}) maps to {@link Material#CYAN_DYE}. */
    CYAN_DYE("351:6", 351, 6, "CYAN_DYE", "dye"),
    /** Legacy id {@code 244} ({@code minecraft:cyan_glazed_terracotta}) maps to {@link Material#CYAN_GLAZED_TERRACOTTA}. */
    CYAN_GLAZED_TERRACOTTA("244", 244, 0, "CYAN_GLAZED_TERRACOTTA", "cyan_glazed_terracotta"),
    /** Legacy id {@code 159:9} ({@code minecraft:stained_hardened_clay}) maps to {@link Material#CYAN_TERRACOTTA}. */
    CYAN_HARDENED_CLAY("159:9", 159, 9, "CYAN_TERRACOTTA", "stained_hardened_clay"),
    /** Legacy id {@code 228} ({@code minecraft:cyan_shulker_box}) maps to {@link Material#CYAN_SHULKER_BOX}. */
    CYAN_SHULKER_BOX("228", 228, 0, "CYAN_SHULKER_BOX", "cyan_shulker_box"),
    /** Legacy id {@code 95:9} ({@code minecraft:stained_glass}) maps to {@link Material#CYAN_STAINED_GLASS}. */
    CYAN_STAINED_GLASS("95:9", 95, 9, "CYAN_STAINED_GLASS", "stained_glass"),
    /** Legacy id {@code 160:9} ({@code minecraft:stained_glass_pane}) maps to {@link Material#CYAN_STAINED_GLASS_PANE}. */
    CYAN_STAINED_GLASS_PANE("160:9", 160, 9, "CYAN_STAINED_GLASS_PANE", "stained_glass_pane"),
    /** Legacy id {@code 35:9} ({@code minecraft:wool}) maps to {@link Material#CYAN_WOOL}. */
    CYAN_WOOL("35:9", 35, 9, "CYAN_WOOL", "wool"),
    /** Legacy id {@code 37} ({@code minecraft:yellow_flower}) maps to {@link Material#DANDELION}. */
    DANDELION("37", 37, 0, "DANDELION", "yellow_flower"),
    /** Legacy id {@code 351:11} ({@code minecraft:dye}) maps to {@link Material#YELLOW_DYE}. */
    DANDELION_YELLOW("351:11", 351, 11, "YELLOW_DYE", "dye"),
    /** Legacy id {@code 448} ({@code minecraft:dark_oak_boat}) maps to {@link Material#DARK_OAK_BOAT}. */
    DARK_OAK_BOAT("448", 448, 0, "DARK_OAK_BOAT", "dark_oak_boat"),
    /** Legacy id {@code 431} ({@code minecraft:dark_oak_door}) maps to {@link Material#DARK_OAK_DOOR}. */
    DARK_OAK_DOOR("431", 431, 0, "DARK_OAK_DOOR", "dark_oak_door"),
    /** Legacy id {@code 197} ({@code minecraft:dark_oak_door}) maps to {@link Material#DARK_OAK_DOOR}. */
    DARK_OAK_DOOR_BLOCK("197", 197, 0, "DARK_OAK_DOOR", "dark_oak_door"),
    /** Legacy id {@code 191} ({@code minecraft:dark_oak_fence}) maps to {@link Material#DARK_OAK_FENCE}. */
    DARK_OAK_FENCE("191", 191, 0, "DARK_OAK_FENCE", "dark_oak_fence"),
    /** Legacy id {@code 186} ({@code minecraft:dark_oak_fence_gate}) maps to {@link Material#DARK_OAK_FENCE_GATE}. */
    DARK_OAK_FENCE_GATE("186", 186, 0, "DARK_OAK_FENCE_GATE", "dark_oak_fence_gate"),
    /** Legacy id {@code 161:1} ({@code minecraft:leaves2}) maps to {@link Material#DARK_OAK_LEAVES}. */
    DARK_OAK_LEAVES("161:1", 161, 1, "DARK_OAK_LEAVES", "leaves2"),
    /** Legacy id {@code 6:5} ({@code minecraft:sapling}) maps to {@link Material#DARK_OAK_SAPLING}. */
    DARK_OAK_SAPLING("6:5", 6, 5, "DARK_OAK_SAPLING", "sapling"),
    /** Legacy id {@code 162:1} ({@code minecraft:log2}) maps to {@link Material#DARK_OAK_WOOD}. */
    DARK_OAK_WOOD("162:1", 162, 1, "DARK_OAK_WOOD", "log2"),
    /** Legacy id {@code 5:5} ({@code minecraft:planks}) maps to {@link Material#DARK_OAK_PLANKS}. */
    DARK_OAK_WOOD_PLANK("5:5", 5, 5, "DARK_OAK_PLANKS", "planks"),
    /** Legacy id {@code 126:5} ({@code minecraft:wooden_slab}) maps to {@link Material#DARK_OAK_SLAB}. */
    DARK_OAK_WOOD_SLAB("126:5", 126, 5, "DARK_OAK_SLAB", "wooden_slab"),
    /** Legacy id {@code 164} ({@code minecraft:dark_oak_stairs}) maps to {@link Material#DARK_OAK_STAIRS}. */
    DARK_OAK_WOOD_STAIRS("164", 164, 0, "DARK_OAK_STAIRS", "dark_oak_stairs"),
    /** Legacy id {@code 168:2} ({@code minecraft:prismarine}) maps to {@link Material#DARK_PRISMARINE}. */
    DARK_PRISMARINE("168:2", 168, 2, "DARK_PRISMARINE", "prismarine"),
    /** Legacy id {@code 151} ({@code minecraft:daylight_detector}) maps to {@link Material#DAYLIGHT_DETECTOR}. */
    DAYLIGHT_SENSOR("151", 151, 0, "DAYLIGHT_DETECTOR", "daylight_detector"),
    /** Legacy id {@code 32} ({@code minecraft:deadbush}) maps to {@link Material#DEAD_BUSH}. */
    DEAD_BUSH("32", 32, 0, "DEAD_BUSH", "deadbush"),
    /** Legacy id {@code 31} ({@code minecraft:tallgrass}) maps to {@link Material#TALL_GRASS}. */
    DEAD_SHRUB("31", 31, 0, "TALL_GRASS", "tallgrass"),
    /** Legacy id {@code 28} ({@code minecraft:detector_rail}) maps to {@link Material#DETECTOR_RAIL}. */
    DETECTOR_RAIL("28", 28, 0, "DETECTOR_RAIL", "detector_rail"),
    /** Legacy id {@code 264} ({@code minecraft:diamond}) maps to {@link Material#DIAMOND}. */
    DIAMOND("264", 264, 0, "DIAMOND", "diamond"),
    /** Legacy id {@code 279} ({@code minecraft:diamond_axe}) maps to {@link Material#DIAMOND_AXE}. */
    DIAMOND_AXE("279", 279, 0, "DIAMOND_AXE", "diamond_axe"),
    /** Legacy id {@code 57} ({@code minecraft:diamond_block}) maps to {@link Material#DIAMOND_BLOCK}. */
    DIAMOND_BLOCK("57", 57, 0, "DIAMOND_BLOCK", "diamond_block"),
    /** Legacy id {@code 313} ({@code minecraft:diamond_boots}) maps to {@link Material#DIAMOND_BOOTS}. */
    DIAMOND_BOOTS("313", 313, 0, "DIAMOND_BOOTS", "diamond_boots"),
    /** Legacy id {@code 311} ({@code minecraft:diamond_chestplate}) maps to {@link Material#DIAMOND_CHESTPLATE}. */
    DIAMOND_CHESTPLATE("311", 311, 0, "DIAMOND_CHESTPLATE", "diamond_chestplate"),
    /** Legacy id {@code 310} ({@code minecraft:diamond_helmet}) maps to {@link Material#DIAMOND_HELMET}. */
    DIAMOND_HELMET("310", 310, 0, "DIAMOND_HELMET", "diamond_helmet"),
    /** Legacy id {@code 293} ({@code minecraft:diamond_hoe}) maps to {@link Material#DIAMOND_HOE}. */
    DIAMOND_HOE("293", 293, 0, "DIAMOND_HOE", "diamond_hoe"),
    /** Legacy id {@code 419} ({@code minecraft:diamond_horse_armor}) maps to {@link Material#DIAMOND_HORSE_ARMOR}. */
    DIAMOND_HORSE_ARMOR("419", 419, 0, "DIAMOND_HORSE_ARMOR", "diamond_horse_armor"),
    /** Legacy id {@code 312} ({@code minecraft:diamond_leggings}) maps to {@link Material#DIAMOND_LEGGINGS}. */
    DIAMOND_LEGGINGS("312", 312, 0, "DIAMOND_LEGGINGS", "diamond_leggings"),
    /** Legacy id {@code 56} ({@code minecraft:diamond_ore}) maps to {@link Material#DIAMOND_ORE}. */
    DIAMOND_ORE("56", 56, 0, "DIAMOND_ORE", "diamond_ore"),
    /** Legacy id {@code 278} ({@code minecraft:diamond_pickaxe}) maps to {@link Material#DIAMOND_PICKAXE}. */
    DIAMOND_PICKAXE("278", 278, 0, "DIAMOND_PICKAXE", "diamond_pickaxe"),
    /** Legacy id {@code 277} ({@code minecraft:diamond_shovel}) maps to {@link Material#DIAMOND_SHOVEL}. */
    DIAMOND_SHOVEL("277", 277, 0, "DIAMOND_SHOVEL", "diamond_shovel"),
    /** Legacy id {@code 276} ({@code minecraft:diamond_sword}) maps to {@link Material#DIAMOND_SWORD}. */
    DIAMOND_SWORD("276", 276, 0, "DIAMOND_SWORD", "diamond_sword"),
    /** Legacy id {@code 1:3} ({@code minecraft:stone}) maps to {@link Material#DIORITE}. */
    DIORITE("1:3", 1, 3, "DIORITE", "stone"),
    /** Legacy id {@code 3} ({@code minecraft:dirt}) maps to {@link Material#DIRT}. */
    DIRT("3", 3, 0, "DIRT", "dirt"),
    /** Legacy id {@code 23} ({@code minecraft:dispenser}) maps to {@link Material#DISPENSER}. */
    DISPENSER("23", 23, 0, "DISPENSER", "dispenser"),
    /** Legacy id {@code 125:4} ({@code minecraft:double_wooden_slab}) maps to {@link Material#ACACIA_PLANKS}. */
    DOUBLE_ACACIA_WOOD_SLAB("125:4", 125, 4, "ACACIA_PLANKS", "double_wooden_slab"),
    /** Legacy id {@code 125:2} ({@code minecraft:double_wooden_slab}) maps to {@link Material#BIRCH_PLANKS}. */
    DOUBLE_BIRCH_WOOD_SLAB("125:2", 125, 2, "BIRCH_PLANKS", "double_wooden_slab"),
    /** Legacy id {@code 43:4} ({@code minecraft:double_stone_slab}) maps to {@link Material#BRICK}. */
    DOUBLE_BRICK_SLAB("43:4", 43, 4, "BRICK", "double_stone_slab"),
    /** Legacy id {@code 43:3} ({@code minecraft:double_stone_slab}) maps to {@link Material#COBBLESTONE}. */
    DOUBLE_COBBLESTONE_SLAB("43:3", 43, 3, "COBBLESTONE", "double_stone_slab"),
    /** Legacy id {@code 125:5} ({@code minecraft:double_wooden_slab}) maps to {@link Material#DARK_OAK_PLANKS}. */
    DOUBLE_DARK_OAK_WOOD_SLAB("125:5", 125, 5, "DARK_OAK_PLANKS", "double_wooden_slab"),
    /** Legacy id {@code 125:3} ({@code minecraft:double_wooden_slab}) maps to {@link Material#JUNGLE_PLANKS}. */
    DOUBLE_JUNGLE_WOOD_SLAB("125:3", 125, 3, "JUNGLE_PLANKS", "double_wooden_slab"),
    /** Legacy id {@code 43:6} ({@code minecraft:double_stone_slab}) maps to {@link Material#NETHER_BRICK}. */
    DOUBLE_NETHER_BRICK_SLAB("43:6", 43, 6, "NETHER_BRICK", "double_stone_slab"),
    /** Legacy id {@code 125} ({@code minecraft:double_wooden_slab}) maps to {@link Material#OAK_PLANKS}. */
    DOUBLE_OAK_WOOD_SLAB("125", 125, 0, "OAK_PLANKS", "double_wooden_slab"),
    /** Legacy id {@code 43:7} ({@code minecraft:double_stone_slab}) maps to {@link Material#QUARTZ_BLOCK}. */
    DOUBLE_QUARTZ_SLAB("43:7", 43, 7, "QUARTZ_BLOCK", "double_stone_slab"),
    /** Legacy id {@code 181} ({@code minecraft:double_stone_slab2}) maps to {@link Material#RED_SANDSTONE}. */
    DOUBLE_RED_SANDSTONE_SLAB("181", 181, 0, "RED_SANDSTONE", "double_stone_slab2"),
    /** Legacy id {@code 43:1} ({@code minecraft:double_stone_slab}) maps to {@link Material#SANDSTONE}. */
    DOUBLE_SANDSTONE_SLAB("43:1", 43, 1, "SANDSTONE", "double_stone_slab"),
    /** Legacy id {@code 125:1} ({@code minecraft:double_wooden_slab}) maps to {@link Material#SPRUCE_PLANKS}. */
    DOUBLE_SPRUCE_WOOD_SLAB("125:1", 125, 1, "SPRUCE_PLANKS", "double_wooden_slab"),
    /** Legacy id {@code 43:5} ({@code minecraft:double_stone_slab}) maps to {@link Material#STONE_BRICKS}. */
    DOUBLE_STONE_BRICK_SLAB("43:5", 43, 5, "STONE_BRICKS", "double_stone_slab"),
    /** Legacy id {@code 43} ({@code minecraft:double_stone_slab}) maps to {@link Material#STONE}. */
    DOUBLE_STONE_SLAB("43", 43, 0, "STONE", "double_stone_slab"),
    /** Legacy id {@code 175:2} ({@code minecraft:double_plant}) maps to {@link Material#LEGACY_DOUBLE_PLANT}. */
    DOUBLE_TALLGRASS("175:2", 175, 2, "LEGACY_DOUBLE_PLANT", "double_plant"),
    /** Legacy id {@code 43:2} ({@code minecraft:double_stone_slab}) maps to {@link Material#OAK_PLANKS}. */
    DOUBLE_WOODEN_SLAB("43:2", 43, 2, "OAK_PLANKS", "double_stone_slab"),
    /** Legacy id {@code 122} ({@code minecraft:dragon_egg}) maps to {@link Material#DRAGON_EGG}. */
    DRAGON_EGG("122", 122, 0, "DRAGON_EGG", "dragon_egg"),
    /** Legacy id {@code 437} ({@code minecraft:dragon_breath}) maps to {@link Material#DRAGON_BREATH}. */
    DRAGON_S_BREATH("437", 437, 0, "DRAGON_BREATH", "dragon_breath"),
    /** Legacy id {@code 158} ({@code minecraft:dropper}) maps to {@link Material#DROPPER}. */
    DROPPER("158", 158, 0, "DROPPER", "dropper"),
    /** Legacy id {@code 344} ({@code minecraft:egg}) maps to {@link Material#EGG}. */
    EGG("344", 344, 0, "EGG", "egg"),
    /** Legacy id {@code 443} ({@code minecraft:elytra}) maps to {@link Material#ELYTRA}. */
    ELYTRA("443", 443, 0, "ELYTRA", "elytra"),
    /** Legacy id {@code 388} ({@code minecraft:emerald}) maps to {@link Material#EMERALD}. */
    EMERALD("388", 388, 0, "EMERALD", "emerald"),
    /** Legacy id {@code 133} ({@code minecraft:emerald_block}) maps to {@link Material#EMERALD_BLOCK}. */
    EMERALD_BLOCK("133", 133, 0, "EMERALD_BLOCK", "emerald_block"),
    /** Legacy id {@code 129} ({@code minecraft:emerald_ore}) maps to {@link Material#EMERALD_ORE}. */
    EMERALD_ORE("129", 129, 0, "EMERALD_ORE", "emerald_ore"),
    /** Legacy id {@code 395} ({@code minecraft:map}) maps to {@link Material#MAP}. */
    EMPTY_MAP("395", 395, 0, "MAP", "map"),
    /** Legacy id {@code 403} ({@code minecraft:enchanted_book}) maps to {@link Material#ENCHANTED_BOOK}. */
    ENCHANTED_BOOK("403", 403, 0, "ENCHANTED_BOOK", "enchanted_book"),
    /** Legacy id {@code 322:1} ({@code minecraft:golden_apple}) maps to {@link Material#ENCHANTED_GOLDEN_APPLE}. */
    ENCHANTED_GOLDEN_APPLE("322:1", 322, 1, "ENCHANTED_GOLDEN_APPLE", "golden_apple"),
    /** Legacy id {@code 116} ({@code minecraft:enchanting_table}) maps to {@link Material#ENCHANTING_TABLE}. */
    ENCHANTMENT_TABLE("116", 116, 0, "ENCHANTING_TABLE", "enchanting_table"),
    /** Legacy id {@code 426} ({@code minecraft:end_crystal}) maps to {@link Material#END_CRYSTAL}. */
    END_CRYSTAL("426", 426, 0, "END_CRYSTAL", "end_crystal"),
    /** Legacy id {@code 209} ({@code minecraft:end_gateway}) maps to {@link Material#END_GATEWAY}. */
    END_GATEWAY("209", 209, 0, "END_GATEWAY", "end_gateway"),
    /** Legacy id {@code 119} ({@code minecraft:end_portal}) maps to {@link Material#END_PORTAL}. */
    END_PORTAL("119", 119, 0, "END_PORTAL", "end_portal"),
    /** Legacy id {@code 120} ({@code minecraft:end_portal_frame}) maps to {@link Material#END_PORTAL_FRAME}. */
    END_PORTAL_FRAME("120", 120, 0, "END_PORTAL_FRAME", "end_portal_frame"),
    /** Legacy id {@code 198} ({@code minecraft:end_rod}) maps to {@link Material#END_ROD}. */
    END_ROD("198", 198, 0, "END_ROD", "end_rod"),
    /** Legacy id {@code 121} ({@code minecraft:end_stone}) maps to {@link Material#END_STONE}. */
    END_STONE("121", 121, 0, "END_STONE", "end_stone"),
    /** Legacy id {@code 206} ({@code minecraft:end_bricks}) maps to {@link Material#END_STONE_BRICKS}. */
    END_STONE_BRICKS("206", 206, 0, "END_STONE_BRICKS", "end_bricks"),
    /** Legacy id {@code 130} ({@code minecraft:ender_chest}) maps to {@link Material#ENDER_CHEST}. */
    ENDER_CHEST("130", 130, 0, "ENDER_CHEST", "ender_chest"),
    /** Legacy id {@code 368} ({@code minecraft:ender_pearl}) maps to {@link Material#ENDER_PEARL}. */
    ENDER_PEARL("368", 368, 0, "ENDER_PEARL", "ender_pearl"),
    /** Legacy id {@code 381} ({@code minecraft:ender_eye}) maps to {@link Material#ENDER_EYE}. */
    EYE_OF_ENDER("381", 381, 0, "ENDER_EYE", "ender_eye"),
    /** Legacy id {@code 2260} ({@code minecraft:record_far}) maps to {@link Material#MUSIC_DISC_FAR}. */
    FAR_DISC("2260", 2260, 0, "MUSIC_DISC_FAR", "record_far"),
    /** Legacy id {@code 60} ({@code minecraft:farmland}) maps to {@link Material#FARMLAND}. */
    FARMLAND("60", 60, 0, "FARMLAND", "farmland"),
    /** Legacy id {@code 288} ({@code minecraft:feather}) maps to {@link Material#FEATHER}. */
    FEATHER("288", 288, 0, "FEATHER", "feather"),
    /** Legacy id {@code 376} ({@code minecraft:fermented_spider_eye}) maps to {@link Material#FERMENTED_SPIDER_EYE}. */
    FERMENTED_SPIDER_EYE("376", 376, 0, "FERMENTED_SPIDER_EYE", "fermented_spider_eye"),
    /** Legacy id {@code 31:2} ({@code minecraft:tallgrass}) maps to {@link Material#FERN}. */
    FERN("31:2", 31, 2, "FERN", "tallgrass"),
    /** Legacy id {@code 51} ({@code minecraft:fire}) maps to {@link Material#FIRE}. */
    FIRE("51", 51, 0, "FIRE", "fire"),
    /** Legacy id {@code 385} ({@code minecraft:fire_charge}) maps to {@link Material#FIRE_CHARGE}. */
    FIRE_CHARGE("385", 385, 0, "FIRE_CHARGE", "fire_charge"),
    /** Legacy id {@code 401} ({@code minecraft:fireworks}) maps to {@link Material#FIREWORK_ROCKET}. */
    FIREWORK_ROCKET("401", 401, 0, "FIREWORK_ROCKET", "fireworks"),
    /** Legacy id {@code 402} ({@code minecraft:firework_charge}) maps to {@link Material#FIREWORK_STAR}. */
    FIREWORK_STAR("402", 402, 0, "FIREWORK_STAR", "firework_charge"),
    /** Legacy id {@code 346} ({@code minecraft:fishing_rod}) maps to {@link Material#FISHING_ROD}. */
    FISHING_ROD("346", 346, 0, "FISHING_ROD", "fishing_rod"),
    /** Legacy id {@code 318} ({@code minecraft:flint}) maps to {@link Material#FLINT}. */
    FLINT("318", 318, 0, "FLINT", "flint"),
    /** Legacy id {@code 259} ({@code minecraft:flint_and_steel}) maps to {@link Material#FLINT_AND_STEEL}. */
    FLINT_AND_STEEL("259", 259, 0, "FLINT_AND_STEEL", "flint_and_steel"),
    /** Legacy id {@code 140} ({@code minecraft:flower_pot}) maps to {@link Material#FLOWER_POT}. */
    FLOWER_POT_BLOCK("140", 140, 0, "FLOWER_POT", "flower_pot"),
    /** Legacy id {@code 390} ({@code minecraft:flower_pot}) maps to {@link Material#FLOWER_POT}. */
    FLOWER_POT("390", 390, 0, "FLOWER_POT", "flower_pot"),
    /** Legacy id {@code 10} ({@code minecraft:flowing_lava}) maps to {@link Material#LAVA}. */
    FLOWING_LAVA("10", 10, 0, "LAVA", "flowing_lava"),
    /** Legacy id {@code 8} ({@code minecraft:flowing_water}) maps to {@link Material#WATER}. */
    FLOWING_WATER("8", 8, 0, "WATER", "flowing_water"),
    /** Legacy id {@code 176} ({@code minecraft:standing_banner}) maps to {@link Material#BLACK_WALL_BANNER}. */
    FREE_STANDING_BANNER("176", 176, 0, "BLACK_WALL_BANNER", "standing_banner"),
    /** Legacy id {@code 212} ({@code minecraft:frosted_ice}) maps to {@link Material#FROSTED_ICE}. */
    FROSTED_ICE("212", 212, 0, "FROSTED_ICE", "frosted_ice"),
    /** Legacy id {@code 61} ({@code minecraft:furnace}) maps to {@link Material#FURNACE}. */
    FURNACE("61", 61, 0, "FURNACE", "furnace"),
    /** Legacy id {@code 370} ({@code minecraft:ghast_tear}) maps to {@link Material#GHAST_TEAR}. */
    GHAST_TEAR("370", 370, 0, "GHAST_TEAR", "ghast_tear"),
    /** Legacy id {@code 20} ({@code minecraft:glass}) maps to {@link Material#GLASS}. */
    GLASS("20", 20, 0, "GLASS", "glass"),
    /** Legacy id {@code 374} ({@code minecraft:glass_bottle}) maps to {@link Material#GLASS_BOTTLE}. */
    GLASS_BOTTLE("374", 374, 0, "GLASS_BOTTLE", "glass_bottle"),
    /** Legacy id {@code 102} ({@code minecraft:glass_pane}) maps to {@link Material#GLASS_PANE}. */
    GLASS_PANE("102", 102, 0, "GLASS_PANE", "glass_pane"),
    /** Legacy id {@code 382} ({@code minecraft:speckled_melon}) maps to {@link Material#GLISTERING_MELON_SLICE}. */
    GLISTERING_MELON("382", 382, 0, "GLISTERING_MELON_SLICE", "speckled_melon"),
    /** Legacy id {@code 74} ({@code minecraft:lit_redstone_ore}) maps to {@link Material#LEGACY_GLOWING_REDSTONE_ORE}. */
    GLOWING_REDSTONE_ORE("74", 74, 0, "LEGACY_GLOWING_REDSTONE_ORE", "lit_redstone_ore"),
    /** Legacy id {@code 89} ({@code minecraft:glowstone}) maps to {@link Material#GLOWSTONE}. */
    GLOWSTONE("89", 89, 0, "GLOWSTONE", "glowstone"),
    /** Legacy id {@code 348} ({@code minecraft:glowstone_dust}) maps to {@link Material#GLOWSTONE_DUST}. */
    GLOWSTONE_DUST("348", 348, 0, "GLOWSTONE_DUST", "glowstone_dust"),
    /** Legacy id {@code 41} ({@code minecraft:gold_block}) maps to {@link Material#GOLD_BLOCK}. */
    GOLD_BLOCK("41", 41, 0, "GOLD_BLOCK", "gold_block"),
    /** Legacy id {@code 266} ({@code minecraft:gold_ingot}) maps to {@link Material#GOLD_INGOT}. */
    GOLD_INGOT("266", 266, 0, "GOLD_INGOT", "gold_ingot"),
    /** Legacy id {@code 371} ({@code minecraft:gold_nugget}) maps to {@link Material#GOLD_NUGGET}. */
    GOLD_NUGGET("371", 371, 0, "GOLD_NUGGET", "gold_nugget"),
    /** Legacy id {@code 14} ({@code minecraft:gold_ore}) maps to {@link Material#GOLD_ORE}. */
    GOLD_ORE("14", 14, 0, "GOLD_ORE", "gold_ore"),
    /** Legacy id {@code 322} ({@code minecraft:golden_apple}) maps to {@link Material#GOLDEN_APPLE}. */
    GOLDEN_APPLE("322", 322, 0, "GOLDEN_APPLE", "golden_apple"),
    /** Legacy id {@code 286} ({@code minecraft:golden_axe}) maps to {@link Material#GOLDEN_AXE}. */
    GOLDEN_AXE("286", 286, 0, "GOLDEN_AXE", "golden_axe"),
    /** Legacy id {@code 317} ({@code minecraft:golden_boots}) maps to {@link Material#GOLDEN_BOOTS}. */
    GOLDEN_BOOTS("317", 317, 0, "GOLDEN_BOOTS", "golden_boots"),
    /** Legacy id {@code 396} ({@code minecraft:golden_carrot}) maps to {@link Material#GOLDEN_CARROT}. */
    GOLDEN_CARROT("396", 396, 0, "GOLDEN_CARROT", "golden_carrot"),
    /** Legacy id {@code 315} ({@code minecraft:golden_chestplate}) maps to {@link Material#GOLDEN_CHESTPLATE}. */
    GOLDEN_CHESTPLATE("315", 315, 0, "GOLDEN_CHESTPLATE", "golden_chestplate"),
    /** Legacy id {@code 314} ({@code minecraft:golden_helmet}) maps to {@link Material#GOLDEN_HELMET}. */
    GOLDEN_HELMET("314", 314, 0, "GOLDEN_HELMET", "golden_helmet"),
    /** Legacy id {@code 294} ({@code minecraft:golden_hoe}) maps to {@link Material#GOLDEN_HOE}. */
    GOLDEN_HOE("294", 294, 0, "GOLDEN_HOE", "golden_hoe"),
    /** Legacy id {@code 418} ({@code minecraft:golden_horse_armor}) maps to {@link Material#GOLDEN_HORSE_ARMOR}. */
    GOLDEN_HORSE_ARMOR("418", 418, 0, "GOLDEN_HORSE_ARMOR", "golden_horse_armor"),
    /** Legacy id {@code 316} ({@code minecraft:golden_leggings}) maps to {@link Material#GOLDEN_LEGGINGS}. */
    GOLDEN_LEGGINGS("316", 316, 0, "GOLDEN_LEGGINGS", "golden_leggings"),
    /** Legacy id {@code 285} ({@code minecraft:golden_pickaxe}) maps to {@link Material#GOLDEN_PICKAXE}. */
    GOLDEN_PICKAXE("285", 285, 0, "GOLDEN_PICKAXE", "golden_pickaxe"),
    /** Legacy id {@code 284} ({@code minecraft:golden_shovel}) maps to {@link Material#GOLDEN_SHOVEL}. */
    GOLDEN_SHOVEL("284", 284, 0, "GOLDEN_SHOVEL", "golden_shovel"),
    /** Legacy id {@code 283} ({@code minecraft:golden_sword}) maps to {@link Material#GOLDEN_SWORD}. */
    GOLDEN_SWORD("283", 283, 0, "GOLDEN_SWORD", "golden_sword"),
    /** Legacy id {@code 1:1} ({@code minecraft:stone}) maps to {@link Material#GRANITE}. */
    GRANITE("1:1", 1, 1, "GRANITE", "stone"),
    /** Legacy id {@code 2} ({@code minecraft:grass}) maps to {@link Material#GRASS_BLOCK}. */
    GRASS_BLOCK("2", 2, 0, "GRASS_BLOCK", "grass"),
    /** Legacy id {@code 31:1} ({@code minecraft:tallgrass}) maps to {@code Material.GRASS}. */
    GRASS("31:1", 31, 1, "GRASS", "tallgrass"),
    /** Legacy id {@code 208} ({@code minecraft:grass_path}) maps to {@code Material.GRASS_PATH}. */
    GRASS_PATH("208", 208, 0, "GRASS_PATH", "grass_path"),
    /** Legacy id {@code 13} ({@code minecraft:gravel}) maps to {@link Material#GRAVEL}. */
    GRAVEL("13", 13, 0, "GRAVEL", "gravel"),
    /** Legacy id {@code 171:7} ({@code minecraft:carpet}) maps to {@link Material#GRAY_CARPET}. */
    GRAY_CARPET("171:7", 171, 7, "GRAY_CARPET", "carpet"),
    /** Legacy id {@code 251:7} ({@code minecraft:concrete}) maps to {@link Material#GRAY_CONCRETE}. */
    GRAY_CONCRETE("251:7", 251, 7, "GRAY_CONCRETE", "concrete"),
    /** Legacy id {@code 252:7} ({@code minecraft:concrete_powder}) maps to {@link Material#GRAY_CONCRETE_POWDER}. */
    GRAY_CONCRETE_POWDER("252:7", 252, 7, "GRAY_CONCRETE_POWDER", "concrete_powder"),
    /** Legacy id {@code 351:8} ({@code minecraft:dye}) maps to {@link Material#GRAY_DYE}. */
    GRAY_DYE("351:8", 351, 8, "GRAY_DYE", "dye"),
    /** Legacy id {@code 242} ({@code minecraft:gray_glazed_terracotta}) maps to {@link Material#GRAY_GLAZED_TERRACOTTA}. */
    GRAY_GLAZED_TERRACOTTA("242", 242, 0, "GRAY_GLAZED_TERRACOTTA", "gray_glazed_terracotta"),
    /** Legacy id {@code 159:7} ({@code minecraft:stained_hardened_clay}) maps to {@link Material#GRAY_TERRACOTTA}. */
    GRAY_HARDENED_CLAY("159:7", 159, 7, "GRAY_TERRACOTTA", "stained_hardened_clay"),
    /** Legacy id {@code 226} ({@code minecraft:gray_shulker_box}) maps to {@link Material#GRAY_SHULKER_BOX}. */
    GRAY_SHULKER_BOX("226", 226, 0, "GRAY_SHULKER_BOX", "gray_shulker_box"),
    /** Legacy id {@code 95:7} ({@code minecraft:stained_glass}) maps to {@link Material#GRAY_STAINED_GLASS}. */
    GRAY_STAINED_GLASS("95:7", 95, 7, "GRAY_STAINED_GLASS", "stained_glass"),
    /** Legacy id {@code 160:7} ({@code minecraft:stained_glass_pane}) maps to {@link Material#GRAY_STAINED_GLASS_PANE}. */
    GRAY_STAINED_GLASS_PANE("160:7", 160, 7, "GRAY_STAINED_GLASS_PANE", "stained_glass_pane"),
    /** Legacy id {@code 35:7} ({@code minecraft:wool}) maps to {@link Material#GRAY_WOOL}. */
    GRAY_WOOL("35:7", 35, 7, "GRAY_WOOL", "wool"),
    /** Legacy id {@code 171:13} ({@code minecraft:carpet}) maps to {@link Material#GREEN_CARPET}. */
    GREEN_CARPET("171:13", 171, 13, "GREEN_CARPET", "carpet"),
    /** Legacy id {@code 251:13} ({@code minecraft:concrete}) maps to {@link Material#GREEN_CONCRETE}. */
    GREEN_CONCRETE("251:13", 251, 13, "GREEN_CONCRETE", "concrete"),
    /** Legacy id {@code 252:13} ({@code minecraft:concrete_powder}) maps to {@link Material#GREEN_CONCRETE_POWDER}. */
    GREEN_CONCRETE_POWDER("252:13", 252, 13, "GREEN_CONCRETE_POWDER", "concrete_powder"),
    /** Legacy id {@code 248} ({@code minecraft:green_glazed_terracotta}) maps to {@link Material#GREEN_GLAZED_TERRACOTTA}. */
    GREEN_GLAZED_TERRACOTTA("248", 248, 0, "GREEN_GLAZED_TERRACOTTA", "green_glazed_terracotta"),
    /** Legacy id {@code 159:13} ({@code minecraft:stained_hardened_clay}) maps to {@link Material#GREEN_TERRACOTTA}. */
    GREEN_HARDENED_CLAY("159:13", 159, 13, "GREEN_TERRACOTTA", "stained_hardened_clay"),
    /** Legacy id {@code 232} ({@code minecraft:green_shulker_box}) maps to {@link Material#GREEN_SHULKER_BOX}. */
    GREEN_SHULKER_BOX("232", 232, 0, "GREEN_SHULKER_BOX", "green_shulker_box"),
    /** Legacy id {@code 95:13} ({@code minecraft:stained_glass}) maps to {@link Material#GREEN_STAINED_GLASS}. */
    GREEN_STAINED_GLASS("95:13", 95, 13, "GREEN_STAINED_GLASS", "stained_glass"),
    /** Legacy id {@code 160:13} ({@code minecraft:stained_glass_pane}) maps to {@link Material#GREEN_STAINED_GLASS_PANE}. */
    GREEN_STAINED_GLASS_PANE("160:13", 160, 13, "GREEN_STAINED_GLASS_PANE", "stained_glass_pane"),
    /** Legacy id {@code 35:13} ({@code minecraft:wool}) maps to {@link Material#GREEN_WOOL}. */
    GREEN_WOOL("35:13", 35, 13, "GREEN_WOOL", "wool"),
    /** Legacy id {@code 289} ({@code minecraft:gunpowder}) maps to {@link Material#GUNPOWDER}. */
    GUNPOWDER("289", 289, 0, "GUNPOWDER", "gunpowder"),
    /** Legacy id {@code 172} ({@code minecraft:hardened_clay}) maps to {@link Material#TERRACOTTA}. */
    HARDENED_CLAY("172", 172, 0, "TERRACOTTA", "hardened_clay"),
    /** Legacy id {@code 170} ({@code minecraft:hay_block}) maps to {@link Material#HAY_BLOCK}. */
    HAY_BALE("170", 170, 0, "HAY_BLOCK", "hay_block"),
    /** Legacy id {@code 154} ({@code minecraft:hopper}) maps to {@link Material#HOPPER}. */
    HOPPER("154", 154, 0, "HOPPER", "hopper"),
    /** Legacy id {@code 79} ({@code minecraft:ice}) maps to {@link Material#ICE}. */
    ICE("79", 79, 0, "ICE", "ice"),
    /** Legacy id {@code 351} ({@code minecraft:dye}) maps to {@link Material#INK_SAC}. */
    INK_SACK("351", 351, 0, "INK_SAC", "dye"),
    /** Legacy id {@code 178} ({@code minecraft:daylight_detector_inverted}) maps to {@link Material#DAYLIGHT_DETECTOR}. */
    INVERTED_DAYLIGHT_SENSOR("178", 178, 0, "DAYLIGHT_DETECTOR", "daylight_detector_inverted"),
    /** Legacy id {@code 258} ({@code minecraft:iron_axe}) maps to {@link Material#IRON_AXE}. */
    IRON_AXE("258", 258, 0, "IRON_AXE", "iron_axe"),
    /** Legacy id {@code 101} ({@code minecraft:iron_bars}) maps to {@link Material#IRON_BARS}. */
    IRON_BARS("101", 101, 0, "IRON_BARS", "iron_bars"),
    /** Legacy id {@code 42} ({@code minecraft:iron_block}) maps to {@link Material#IRON_BLOCK}. */
    IRON_BLOCK("42", 42, 0, "IRON_BLOCK", "iron_block"),
    /** Legacy id {@code 309} ({@code minecraft:iron_boots}) maps to {@link Material#IRON_BOOTS}. */
    IRON_BOOTS("309", 309, 0, "IRON_BOOTS", "iron_boots"),
    /** Legacy id {@code 307} ({@code minecraft:iron_chestplate}) maps to {@link Material#IRON_CHESTPLATE}. */
    IRON_CHESTPLATE("307", 307, 0, "IRON_CHESTPLATE", "iron_chestplate"),
    /** Legacy id {@code 330} ({@code minecraft:iron_door}) maps to {@link Material#IRON_DOOR}. */
    IRON_DOOR("330", 330, 0, "IRON_DOOR", "iron_door"),
    /** Legacy id {@code 71} ({@code minecraft:iron_door}) maps to {@link Material#IRON_DOOR}. */
    IRON_DOOR_BLOCK("71", 71, 0, "IRON_DOOR", "iron_door"),
    /** Legacy id {@code 306} ({@code minecraft:iron_helmet}) maps to {@link Material#IRON_HELMET}. */
    IRON_HELMET("306", 306, 0, "IRON_HELMET", "iron_helmet"),
    /** Legacy id {@code 292} ({@code minecraft:iron_hoe}) maps to {@link Material#IRON_HOE}. */
    IRON_HOE("292", 292, 0, "IRON_HOE", "iron_hoe"),
    /** Legacy id {@code 417} ({@code minecraft:iron_horse_armor}) maps to {@link Material#IRON_HORSE_ARMOR}. */
    IRON_HORSE_ARMOR("417", 417, 0, "IRON_HORSE_ARMOR", "iron_horse_armor"),
    /** Legacy id {@code 265} ({@code minecraft:iron_ingot}) maps to {@link Material#IRON_INGOT}. */
    IRON_INGOT("265", 265, 0, "IRON_INGOT", "iron_ingot"),
    /** Legacy id {@code 308} ({@code minecraft:iron_leggings}) maps to {@link Material#IRON_LEGGINGS}. */
    IRON_LEGGINGS("308", 308, 0, "IRON_LEGGINGS", "iron_leggings"),
    /** Legacy id {@code 452} ({@code minecraft:iron_nugget}) maps to {@link Material#IRON_NUGGET}. */
    IRON_NUGGET("452", 452, 0, "IRON_NUGGET", "iron_nugget"),
    /** Legacy id {@code 15} ({@code minecraft:iron_ore}) maps to {@link Material#IRON_ORE}. */
    IRON_ORE("15", 15, 0, "IRON_ORE", "iron_ore"),
    /** Legacy id {@code 257} ({@code minecraft:iron_pickaxe}) maps to {@link Material#IRON_PICKAXE}. */
    IRON_PICKAXE("257", 257, 0, "IRON_PICKAXE", "iron_pickaxe"),
    /** Legacy id {@code 256} ({@code minecraft:iron_shovel}) maps to {@link Material#IRON_SHOVEL}. */
    IRON_SHOVEL("256", 256, 0, "IRON_SHOVEL", "iron_shovel"),
    /** Legacy id {@code 267} ({@code minecraft:iron_sword}) maps to {@link Material#IRON_SWORD}. */
    IRON_SWORD("267", 267, 0, "IRON_SWORD", "iron_sword"),
    /** Legacy id {@code 167} ({@code minecraft:iron_trapdoor}) maps to {@link Material#IRON_TRAPDOOR}. */
    IRON_TRAPDOOR("167", 167, 0, "IRON_TRAPDOOR", "iron_trapdoor"),
    /** Legacy id {@code 389} ({@code minecraft:item_frame}) maps to {@link Material#ITEM_FRAME}. */
    ITEM_FRAME("389", 389, 0, "ITEM_FRAME", "item_frame"),
    /** Legacy id {@code 91} ({@code minecraft:lit_pumpkin}) maps to {@link Material#JACK_O_LANTERN}. */
    JACK_O_LANTERN("91", 91, 0, "JACK_O_LANTERN", "lit_pumpkin"),
    /** Legacy id {@code 84} ({@code minecraft:jukebox}) maps to {@link Material#JUKEBOX}. */
    JUKEBOX("84", 84, 0, "JUKEBOX", "jukebox"),
    /** Legacy id {@code 446} ({@code minecraft:jungle_boat}) maps to {@link Material#JUNGLE_BOAT}. */
    JUNGLE_BOAT("446", 446, 0, "JUNGLE_BOAT", "jungle_boat"),
    /** Legacy id {@code 429} ({@code minecraft:jungle_door}) maps to {@link Material#JUNGLE_DOOR}. */
    JUNGLE_DOOR("429", 429, 0, "JUNGLE_DOOR", "jungle_door"),
    /** Legacy id {@code 195} ({@code minecraft:jungle_door}) maps to {@link Material#JUNGLE_DOOR}. */
    JUNGLE_DOOR_BLOCK("195", 195, 0, "JUNGLE_DOOR", "jungle_door"),
    /** Legacy id {@code 190} ({@code minecraft:jungle_fence}) maps to {@link Material#JUNGLE_FENCE}. */
    JUNGLE_FENCE("190", 190, 0, "JUNGLE_FENCE", "jungle_fence"),
    /** Legacy id {@code 185} ({@code minecraft:jungle_fence_gate}) maps to {@link Material#JUNGLE_FENCE_GATE}. */
    JUNGLE_FENCE_GATE("185", 185, 0, "JUNGLE_FENCE_GATE", "jungle_fence_gate"),
    /** Legacy id {@code 18:3} ({@code minecraft:leaves}) maps to {@link Material#JUNGLE_LEAVES}. */
    JUNGLE_LEAVES("18:3", 18, 3, "JUNGLE_LEAVES", "leaves"),
    /** Legacy id {@code 6:3} ({@code minecraft:sapling}) maps to {@link Material#JUNGLE_SAPLING}. */
    JUNGLE_SAPLING("6:3", 6, 3, "JUNGLE_SAPLING", "sapling"),
    /** Legacy id {@code 17:3} ({@code minecraft:log}) maps to {@link Material#JUNGLE_WOOD}. */
    JUNGLE_WOOD("17:3", 17, 3, "JUNGLE_WOOD", "log"),
    /** Legacy id {@code 5:3} ({@code minecraft:planks}) maps to {@link Material#JUNGLE_PLANKS}. */
    JUNGLE_WOOD_PLANK("5:3", 5, 3, "JUNGLE_PLANKS", "planks"),
    /** Legacy id {@code 126:3} ({@code minecraft:wooden_slab}) maps to {@link Material#JUNGLE_SLAB}. */
    JUNGLE_WOOD_SLAB("126:3", 126, 3, "JUNGLE_SLAB", "wooden_slab"),
    /** Legacy id {@code 136} ({@code minecraft:jungle_stairs}) maps to {@link Material#JUNGLE_STAIRS}. */
    JUNGLE_WOOD_STAIRS("136", 136, 0, "JUNGLE_STAIRS", "jungle_stairs"),
    /** Legacy id {@code 453} ({@code minecraft:knowledge_book}) maps to {@link Material#KNOWLEDGE_BOOK}. */
    KNOWLEDGE_BOOK("453", 453, 0, "KNOWLEDGE_BOOK", "knowledge_book"),
    /** Legacy id {@code 65} ({@code minecraft:ladder}) maps to {@link Material#LADDER}. */
    LADDER("65", 65, 0, "LADDER", "ladder"),
    /** Legacy id {@code 351:4} ({@code minecraft:dye}) maps to {@link Material#LAPIS_LAZULI}. */
    LAPIS_LAZULI("351:4", 351, 4, "LAPIS_LAZULI", "dye"),
    /** Legacy id {@code 22} ({@code minecraft:lapis_block}) maps to {@link Material#LAPIS_BLOCK}. */
    LAPIS_LAZULI_BLOCK("22", 22, 0, "LAPIS_BLOCK", "lapis_block"),
    /** Legacy id {@code 21} ({@code minecraft:lapis_ore}) maps to {@link Material#LAPIS_ORE}. */
    LAPIS_LAZULI_ORE("21", 21, 0, "LAPIS_ORE", "lapis_ore"),
    /** Legacy id {@code 175:3} ({@code minecraft:double_plant}) maps to {@link Material#LARGE_FERN}. */
    LARGE_FERN("175:3", 175, 3, "LARGE_FERN", "double_plant"),
    /** Legacy id {@code 327} ({@code minecraft:lava_bucket}) maps to {@link Material#LAVA_BUCKET}. */
    LAVA_BUCKET("327", 327, 0, "LAVA_BUCKET", "lava_bucket"),
    /** Legacy id {@code 420} ({@code minecraft:lead}) maps to {@link Material#LEAD}. */
    LEAD("420", 420, 0, "LEAD", "lead"),
    /** Legacy id {@code 334} ({@code minecraft:leather}) maps to {@link Material#LEATHER}. */
    LEATHER("334", 334, 0, "LEATHER", "leather"),
    /** Legacy id {@code 301} ({@code minecraft:leather_boots}) maps to {@link Material#LEATHER_BOOTS}. */
    LEATHER_BOOTS("301", 301, 0, "LEATHER_BOOTS", "leather_boots"),
    /** Legacy id {@code 298} ({@code minecraft:leather_helmet}) maps to {@link Material#LEATHER_HELMET}. */
    LEATHER_HELMET("298", 298, 0, "LEATHER_HELMET", "leather_helmet"),
    /** Legacy id {@code 300} ({@code minecraft:leather_leggings}) maps to {@link Material#LEATHER_LEGGINGS}. */
    LEATHER_PANTS("300", 300, 0, "LEATHER_LEGGINGS", "leather_leggings"),
    /** Legacy id {@code 299} ({@code minecraft:leather_chestplate}) maps to {@link Material#LEATHER_CHESTPLATE}. */
    LEATHER_TUNIC("299", 299, 0, "LEATHER_CHESTPLATE", "leather_chestplate"),
    /** Legacy id {@code 69} ({@code minecraft:lever}) maps to {@link Material#LEVER}. */
    LEVER("69", 69, 0, "LEVER", "lever"),
    /** Legacy id {@code 171:3} ({@code minecraft:carpet}) maps to {@link Material#LIGHT_BLUE_CARPET}. */
    LIGHT_BLUE_CARPET("171:3", 171, 3, "LIGHT_BLUE_CARPET", "carpet"),
    /** Legacy id {@code 251:3} ({@code minecraft:concrete}) maps to {@link Material#LIGHT_BLUE_CONCRETE}. */
    LIGHT_BLUE_CONCRETE("251:3", 251, 3, "LIGHT_BLUE_CONCRETE", "concrete"),
    /** Legacy id {@code 252:3} ({@code minecraft:concrete_powder}) maps to {@link Material#LIGHT_BLUE_CONCRETE_POWDER}. */
    LIGHT_BLUE_CONCRETE_POWDER("252:3", 252, 3, "LIGHT_BLUE_CONCRETE_POWDER", "concrete_powder"),
    /** Legacy id {@code 351:12} ({@code minecraft:dye}) maps to {@link Material#LIGHT_BLUE_DYE}. */
    LIGHT_BLUE_DYE("351:12", 351, 12, "LIGHT_BLUE_DYE", "dye"),
    /** Legacy id {@code 238} ({@code minecraft:light_blue_glazed_terracotta}) maps to {@link Material#LIGHT_BLUE_GLAZED_TERRACOTTA}. */
    LIGHT_BLUE_GLAZED_TERRACOTTA("238", 238, 0, "LIGHT_BLUE_GLAZED_TERRACOTTA", "light_blue_glazed_terracotta"),
    /** Legacy id {@code 159:3} ({@code minecraft:stained_hardened_clay}) maps to {@link Material#LIGHT_BLUE_TERRACOTTA}. */
    LIGHT_BLUE_HARDENED_CLAY("159:3", 159, 3, "LIGHT_BLUE_TERRACOTTA", "stained_hardened_clay"),
    /** Legacy id {@code 222} ({@code minecraft:light_blue_shulker_box}) maps to {@link Material#LIGHT_BLUE_SHULKER_BOX}. */
    LIGHT_BLUE_SHULKER_BOX("222", 222, 0, "LIGHT_BLUE_SHULKER_BOX", "light_blue_shulker_box"),
    /** Legacy id {@code 95:3} ({@code minecraft:stained_glass}) maps to {@link Material#LIGHT_BLUE_STAINED_GLASS}. */
    LIGHT_BLUE_STAINED_GLASS("95:3", 95, 3, "LIGHT_BLUE_STAINED_GLASS", "stained_glass"),
    /** Legacy id {@code 160:3} ({@code minecraft:stained_glass_pane}) maps to {@link Material#LIGHT_BLUE_STAINED_GLASS_PANE}. */
    LIGHT_BLUE_STAINED_GLASS_PANE("160:3", 160, 3, "LIGHT_BLUE_STAINED_GLASS_PANE", "stained_glass_pane"),
    /** Legacy id {@code 35:3} ({@code minecraft:wool}) maps to {@link Material#LIGHT_BLUE_WOOL}. */
    LIGHT_BLUE_WOOL("35:3", 35, 3, "LIGHT_BLUE_WOOL", "wool"),
    /** Legacy id {@code 171:8} ({@code minecraft:carpet}) maps to {@link Material#LIGHT_GRAY_CARPET}. */
    LIGHT_GRAY_CARPET("171:8", 171, 8, "LIGHT_GRAY_CARPET", "carpet"),
    /** Legacy id {@code 251:8} ({@code minecraft:concrete}) maps to {@link Material#LIGHT_GRAY_CONCRETE}. */
    LIGHT_GRAY_CONCRETE("251:8", 251, 8, "LIGHT_GRAY_CONCRETE", "concrete"),
    /** Legacy id {@code 252:8} ({@code minecraft:concrete_powder}) maps to {@link Material#LIGHT_GRAY_CONCRETE_POWDER}. */
    LIGHT_GRAY_CONCRETE_POWDER("252:8", 252, 8, "LIGHT_GRAY_CONCRETE_POWDER", "concrete_powder"),
    /** Legacy id {@code 351:7} ({@code minecraft:dye}) maps to {@link Material#LIGHT_GRAY_DYE}. */
    LIGHT_GRAY_DYE("351:7", 351, 7, "LIGHT_GRAY_DYE", "dye"),
    /** Legacy id {@code 243} ({@code minecraft:light_gray_glazed_terracotta}) maps to {@link Material#LIGHT_GRAY_GLAZED_TERRACOTTA}. */
    LIGHT_GRAY_GLAZED_TERRACOTTA("243", 243, 0, "LIGHT_GRAY_GLAZED_TERRACOTTA", "light_gray_glazed_terracotta"),
    /** Legacy id {@code 159:8} ({@code minecraft:stained_hardened_clay}) maps to {@link Material#LIGHT_GRAY_TERRACOTTA}. */
    LIGHT_GRAY_HARDENED_CLAY("159:8", 159, 8, "LIGHT_GRAY_TERRACOTTA", "stained_hardened_clay"),
    /** Legacy id {@code 227} ({@code minecraft:silver_shulker_box}) maps to {@link Material#LIGHT_GRAY_SHULKER_BOX}. */
    LIGHT_GRAY_SHULKER_BOX("227", 227, 0, "LIGHT_GRAY_SHULKER_BOX", "silver_shulker_box"),
    /** Legacy id {@code 95:8} ({@code minecraft:stained_glass}) maps to {@link Material#LIGHT_GRAY_STAINED_GLASS}. */
    LIGHT_GRAY_STAINED_GLASS("95:8", 95, 8, "LIGHT_GRAY_STAINED_GLASS", "stained_glass"),
    /** Legacy id {@code 160:8} ({@code minecraft:stained_glass_pane}) maps to {@link Material#LIGHT_GRAY_STAINED_GLASS_PANE}. */
    LIGHT_GRAY_STAINED_GLASS_PANE("160:8", 160, 8, "LIGHT_GRAY_STAINED_GLASS_PANE", "stained_glass_pane"),
    /** Legacy id {@code 35:8} ({@code minecraft:wool}) maps to {@link Material#LIGHT_GRAY_WOOL}. */
    LIGHT_GRAY_WOOL("35:8", 35, 8, "LIGHT_GRAY_WOOL", "wool"),
    /** Legacy id {@code 175:1} ({@code minecraft:double_plant}) maps to {@link Material#LILAC}. */
    LILAC("175:1", 175, 1, "LILAC", "double_plant"),
    /** Legacy id {@code 111} ({@code minecraft:waterlily}) maps to {@link Material#LILY_PAD}. */
    LILY_PAD("111", 111, 0, "LILY_PAD", "waterlily"),
    /** Legacy id {@code 171:5} ({@code minecraft:carpet}) maps to {@link Material#LIME_CARPET}. */
    LIME_CARPET("171:5", 171, 5, "LIME_CARPET", "carpet"),
    /** Legacy id {@code 251:5} ({@code minecraft:concrete}) maps to {@link Material#LIME_CONCRETE}. */
    LIME_CONCRETE("251:5", 251, 5, "LIME_CONCRETE", "concrete"),
    /** Legacy id {@code 252:5} ({@code minecraft:concrete_powder}) maps to {@link Material#LIME_CONCRETE_POWDER}. */
    LIME_CONCRETE_POWDER("252:5", 252, 5, "LIME_CONCRETE_POWDER", "concrete_powder"),
    /** Legacy id {@code 351:10} ({@code minecraft:dye}) maps to {@link Material#LIME_DYE}. */
    LIME_DYE("351:10", 351, 10, "LIME_DYE", "dye"),
    /** Legacy id {@code 240} ({@code minecraft:lime_glazed_terracotta}) maps to {@link Material#LIME_GLAZED_TERRACOTTA}. */
    LIME_GLAZED_TERRACOTTA("240", 240, 0, "LIME_GLAZED_TERRACOTTA", "lime_glazed_terracotta"),
    /** Legacy id {@code 159:5} ({@code minecraft:stained_hardened_clay}) maps to {@link Material#LIME_TERRACOTTA}. */
    LIME_HARDENED_CLAY("159:5", 159, 5, "LIME_TERRACOTTA", "stained_hardened_clay"),
    /** Legacy id {@code 224} ({@code minecraft:lime_shulker_box}) maps to {@link Material#LIME_SHULKER_BOX}. */
    LIME_SHULKER_BOX("224", 224, 0, "LIME_SHULKER_BOX", "lime_shulker_box"),
    /** Legacy id {@code 95:5} ({@code minecraft:stained_glass}) maps to {@link Material#LIME_STAINED_GLASS}. */
    LIME_STAINED_GLASS("95:5", 95, 5, "LIME_STAINED_GLASS", "stained_glass"),
    /** Legacy id {@code 160:5} ({@code minecraft:stained_glass_pane}) maps to {@link Material#LIME_STAINED_GLASS_PANE}. */
    LIME_STAINED_GLASS_PANE("160:5", 160, 5, "LIME_STAINED_GLASS_PANE", "stained_glass_pane"),
    /** Legacy id {@code 35:5} ({@code minecraft:wool}) maps to {@link Material#LIME_WOOL}. */
    LIME_WOOL("35:5", 35, 5, "LIME_WOOL", "wool"),
    /** Legacy id {@code 441} ({@code minecraft:lingering_potion}) maps to {@link Material#LINGERING_POTION}. */
    LINGERING_POTION("441", 441, 0, "LINGERING_POTION", "lingering_potion"),
    /** Legacy id {@code 171:2} ({@code minecraft:carpet}) maps to {@link Material#MAGENTA_CARPET}. */
    MAGENTA_CARPET("171:2", 171, 2, "MAGENTA_CARPET", "carpet"),
    /** Legacy id {@code 251:2} ({@code minecraft:concrete}) maps to {@link Material#MAGENTA_CONCRETE}. */
    MAGENTA_CONCRETE("251:2", 251, 2, "MAGENTA_CONCRETE", "concrete"),
    /** Legacy id {@code 252:2} ({@code minecraft:concrete_powder}) maps to {@link Material#MAGENTA_CONCRETE_POWDER}. */
    MAGENTA_CONCRETE_POWDER("252:2", 252, 2, "MAGENTA_CONCRETE_POWDER", "concrete_powder"),
    /** Legacy id {@code 351:13} ({@code minecraft:dye}) maps to {@link Material#MAGENTA_DYE}. */
    MAGENTA_DYE("351:13", 351, 13, "MAGENTA_DYE", "dye"),
    /** Legacy id {@code 237} ({@code minecraft:magenta_glazed_terracotta}) maps to {@link Material#MAGENTA_GLAZED_TERRACOTTA}. */
    MAGENTA_GLAZED_TERRACOTTA("237", 237, 0, "MAGENTA_GLAZED_TERRACOTTA", "magenta_glazed_terracotta"),
    /** Legacy id {@code 159:2} ({@code minecraft:stained_hardened_clay}) maps to {@link Material#MAGENTA_TERRACOTTA}. */
    MAGENTA_HARDENED_CLAY("159:2", 159, 2, "MAGENTA_TERRACOTTA", "stained_hardened_clay"),
    /** Legacy id {@code 221} ({@code minecraft:magenta_shulker_box}) maps to {@link Material#MAGENTA_SHULKER_BOX}. */
    MAGENTA_SHULKER_BOX("221", 221, 0, "MAGENTA_SHULKER_BOX", "magenta_shulker_box"),
    /** Legacy id {@code 95:2} ({@code minecraft:stained_glass}) maps to {@link Material#MAGENTA_STAINED_GLASS}. */
    MAGENTA_STAINED_GLASS("95:2", 95, 2, "MAGENTA_STAINED_GLASS", "stained_glass"),
    /** Legacy id {@code 160:2} ({@code minecraft:stained_glass_pane}) maps to {@link Material#MAGENTA_STAINED_GLASS_PANE}. */
    MAGENTA_STAINED_GLASS_PANE("160:2", 160, 2, "MAGENTA_STAINED_GLASS_PANE", "stained_glass_pane"),
    /** Legacy id {@code 35:2} ({@code minecraft:wool}) maps to {@link Material#MAGENTA_WOOL}. */
    MAGENTA_WOOL("35:2", 35, 2, "MAGENTA_WOOL", "wool"),
    /** Legacy id {@code 213} ({@code minecraft:magma}) maps to {@link Material#MAGMA_BLOCK}. */
    MAGMA_BLOCK("213", 213, 0, "MAGMA_BLOCK", "magma"),
    /** Legacy id {@code 378} ({@code minecraft:magma_cream}) maps to {@link Material#MAGMA_CREAM}. */
    MAGMA_CREAM("378", 378, 0, "MAGMA_CREAM", "magma_cream"),
    /** Legacy id {@code 2261} ({@code minecraft:record_mall}) maps to {@link Material#MUSIC_DISC_MALL}. */
    MALL_DISC("2261", 2261, 0, "MUSIC_DISC_MALL", "record_mall"),
    /** Legacy id {@code 358} ({@code minecraft:filled_map}) maps to {@link Material#FILLED_MAP}. */
    MAP("358", 358, 0, "FILLED_MAP", "filled_map"),
    /** Legacy id {@code 2262} ({@code minecraft:record_mellohi}) maps to {@link Material#MUSIC_DISC_MELLOHI}. */
    MELLOHI_DISC("2262", 2262, 0, "MUSIC_DISC_MELLOHI", "record_mellohi"),
    /** Legacy id {@code 360} ({@code minecraft:melon}) maps to {@link Material#MELON_SLICE}. */
    MELON("360", 360, 0, "MELON_SLICE", "melon"),
    /** Legacy id {@code 103} ({@code minecraft:melon_block}) maps to {@link Material#MELON}. */
    MELON_BLOCK("103", 103, 0, "MELON", "melon_block"),
    /** Legacy id {@code 362} ({@code minecraft:melon_seeds}) maps to {@link Material#MELON_SEEDS}. */
    MELON_SEEDS("362", 362, 0, "MELON_SEEDS", "melon_seeds"),
    /** Legacy id {@code 105} ({@code minecraft:melon_stem}) maps to {@link Material#MELON_STEM}. */
    MELON_STEM("105", 105, 0, "MELON_STEM", "melon_stem"),
    /** Legacy id {@code 335} ({@code minecraft:milk_bucket}) maps to {@link Material#MILK_BUCKET}. */
    MILK_BUCKET("335", 335, 0, "MILK_BUCKET", "milk_bucket"),
    /** Legacy id {@code 328} ({@code minecraft:minecart}) maps to {@link Material#MINECART}. */
    MINECART("328", 328, 0, "MINECART", "minecart"),
    /** Legacy id {@code 342} ({@code minecraft:chest_minecart}) maps to {@link Material#CHEST_MINECART}. */
    MINECART_WITH_CHEST("342", 342, 0, "CHEST_MINECART", "chest_minecart"),
    /** Legacy id {@code 422} ({@code minecraft:command_block_minecart}) maps to {@link Material#COMMAND_BLOCK_MINECART}. */
    MINECART_WITH_COMMAND_BLOCK("422", 422, 0, "COMMAND_BLOCK_MINECART", "command_block_minecart"),
    /** Legacy id {@code 343} ({@code minecraft:furnace_minecart}) maps to {@link Material#FURNACE_MINECART}. */
    MINECART_WITH_FURNACE("343", 343, 0, "FURNACE_MINECART", "furnace_minecart"),
    /** Legacy id {@code 408} ({@code minecraft:hopper_minecart}) maps to {@link Material#HOPPER_MINECART}. */
    MINECART_WITH_HOPPER("408", 408, 0, "HOPPER_MINECART", "hopper_minecart"),
    /** Legacy id {@code 407} ({@code minecraft:tnt_minecart}) maps to {@link Material#TNT_MINECART}. */
    MINECART_WITH_TNT("407", 407, 0, "TNT_MINECART", "tnt_minecart"),
    /** Legacy id {@code 144} ({@code minecraft:skull}) maps to {@link Material#SKELETON_SKULL}. */
    MOB_HEAD("144", 144, 0, "SKELETON_SKULL", "skull"),
    /** Legacy id {@code 397:4} ({@code minecraft:skull}) maps to {@link Material#CREEPER_HEAD}. */
    MOB_HEAD_CREEPER("397:4", 397, 4, "CREEPER_HEAD", "skull"),
    /** Legacy id {@code 397:5} ({@code minecraft:skull}) maps to {@link Material#DRAGON_HEAD}. */
    MOB_HEAD_DRAGON("397:5", 397, 5, "DRAGON_HEAD", "skull"),
    /** Legacy id {@code 397:3} ({@code minecraft:skull}) maps to {@link Material#PLAYER_HEAD}. */
    MOB_HEAD_HUMAN("397:3", 397, 3, "PLAYER_HEAD", "skull"),
    /** Legacy id {@code 397} ({@code minecraft:skull}) maps to {@link Material#SKELETON_SKULL}. */
    MOB_HEAD_SKELETON("397", 397, 0, "SKELETON_SKULL", "skull"),
    /** Legacy id {@code 397:1} ({@code minecraft:skull}) maps to {@link Material#WITHER_SKELETON_SKULL}. */
    MOB_HEAD_WITHER_SKELETON("397:1", 397, 1, "WITHER_SKELETON_SKULL", "skull"),
    /** Legacy id {@code 397:2} ({@code minecraft:skull}) maps to {@link Material#ZOMBIE_HEAD}. */
    MOB_HEAD_ZOMBIE("397:2", 397, 2, "ZOMBIE_HEAD", "skull"),
    /** Legacy id {@code 52} ({@code minecraft:mob_spawner}) maps to {@link Material#SPAWNER}. */
    MONSTER_SPAWNER("52", 52, 0, "SPAWNER", "mob_spawner"),
    /** Legacy id {@code 48} ({@code minecraft:mossy_cobblestone}) maps to {@link Material#MOSSY_COBBLESTONE}. */
    MOSS_STONE("48", 48, 0, "MOSSY_COBBLESTONE", "mossy_cobblestone"),
    /** Legacy id {@code 139:1} ({@code minecraft:cobblestone_wall}) maps to {@link Material#MOSSY_COBBLESTONE_WALL}. */
    MOSSY_COBBLESTONE_WALL("139:1", 139, 1, "MOSSY_COBBLESTONE_WALL", "cobblestone_wall"),
    /** Legacy id {@code 97:3} ({@code minecraft:monster_egg}) maps to {@link Material#INFESTED_MOSSY_STONE_BRICKS}. */
    MOSSY_STONE_BRICK_MONSTER_EGG("97:3", 97, 3, "INFESTED_MOSSY_STONE_BRICKS", "monster_egg"),
    /** Legacy id {@code 98:1} ({@code minecraft:stonebrick}) maps to {@link Material#MOSSY_STONE_BRICKS}. */
    MOSSY_STONE_BRICKS("98:1", 98, 1, "MOSSY_STONE_BRICKS", "stonebrick"),
    /** Legacy id {@code 282} ({@code minecraft:mushroom_stew}) maps to {@link Material#MUSHROOM_STEW}. */
    MUSHROOM_STEW("282", 282, 0, "MUSHROOM_STEW", "mushroom_stew"),
    /** Legacy id {@code 110} ({@code minecraft:mycelium}) maps to {@link Material#MYCELIUM}. */
    MYCELIUM("110", 110, 0, "MYCELIUM", "mycelium"),
    /** Legacy id {@code 421} ({@code minecraft:name_tag}) maps to {@link Material#NAME_TAG}. */
    NAME_TAG("421", 421, 0, "NAME_TAG", "name_tag"),
    /** Legacy id {@code 112} ({@code minecraft:nether_brick}) maps to {@link Material#NETHER_BRICK}. */
    NETHER_BRICK_BLOCK("112", 112, 0, "NETHER_BRICK", "nether_brick"),
    /** Legacy id {@code 405} ({@code minecraft:netherbrick}) maps to {@link Material#NETHER_BRICK}. */
    NETHER_BRICK("405", 405, 0, "NETHER_BRICK", "netherbrick"),
    /** Legacy id {@code 113} ({@code minecraft:nether_brick_fence}) maps to {@link Material#NETHER_BRICK_FENCE}. */
    NETHER_BRICK_FENCE("113", 113, 0, "NETHER_BRICK_FENCE", "nether_brick_fence"),
    /** Legacy id {@code 44:6} ({@code minecraft:stone_slab}) maps to {@link Material#NETHER_BRICK_SLAB}. */
    NETHER_BRICK_SLAB("44:6", 44, 6, "NETHER_BRICK_SLAB", "stone_slab"),
    /** Legacy id {@code 114} ({@code minecraft:nether_brick_stairs}) maps to {@link Material#NETHER_BRICK_STAIRS}. */
    NETHER_BRICK_STAIRS("114", 114, 0, "NETHER_BRICK_STAIRS", "nether_brick_stairs"),
    /** Legacy id {@code 90} ({@code minecraft:portal}) maps to {@link Material#NETHER_PORTAL}. */
    NETHER_PORTAL("90", 90, 0, "NETHER_PORTAL", "portal"),
    /** Legacy id {@code 406} ({@code minecraft:quartz}) maps to {@link Material#QUARTZ}. */
    NETHER_QUARTZ("406", 406, 0, "QUARTZ", "quartz"),
    /** Legacy id {@code 153} ({@code minecraft:quartz_ore}) maps to {@link Material#NETHER_QUARTZ_ORE}. */
    NETHER_QUARTZ_ORE("153", 153, 0, "NETHER_QUARTZ_ORE", "quartz_ore"),
    /** Legacy id {@code 399} ({@code minecraft:nether_star}) maps to {@link Material#NETHER_STAR}. */
    NETHER_STAR("399", 399, 0, "NETHER_STAR", "nether_star"),
    /** Legacy id {@code 115} ({@code minecraft:nether_wart}) maps to {@link Material#NETHER_WART}. */
    NETHER_WART_CROP_BLOCK("115", 115, 0, "NETHER_WART", "nether_wart"),
    /** Legacy id {@code 372} ({@code minecraft:nether_wart}) maps to {@link Material#NETHER_WART}. */
    NETHER_WART("372", 372, 0, "NETHER_WART", "nether_wart"),
    /** Legacy id {@code 214} ({@code minecraft:nether_wart_block}) maps to {@link Material#NETHER_WART_BLOCK}. */
    NETHER_WART_BLOCK("214", 214, 0, "NETHER_WART_BLOCK", "nether_wart_block"),
    /** Legacy id {@code 87} ({@code minecraft:netherrack}) maps to {@link Material#NETHERRACK}. */
    NETHERRACK("87", 87, 0, "NETHERRACK", "netherrack"),
    /** Legacy id {@code 25} ({@code minecraft:noteblock}) maps to {@link Material#NOTE_BLOCK}. */
    NOTE_BLOCK("25", 25, 0, "NOTE_BLOCK", "noteblock"),
    /** Legacy id {@code 333} ({@code minecraft:boat}) maps to {@link Material#OAK_BOAT}. */
    OAK_BOAT("333", 333, 0, "OAK_BOAT", "boat"),
    /** Legacy id {@code 324} ({@code minecraft:wooden_door}) maps to {@link Material#OAK_DOOR}. */
    OAK_DOOR("324", 324, 0, "OAK_DOOR", "wooden_door"),
    /** Legacy id {@code 64} ({@code minecraft:wooden_door}) maps to {@link Material#OAK_DOOR}. */
    OAK_DOOR_BLOCK("64", 64, 0, "OAK_DOOR", "wooden_door"),
    /** Legacy id {@code 85} ({@code minecraft:fence}) maps to {@link Material#OAK_FENCE}. */
    OAK_FENCE("85", 85, 0, "OAK_FENCE", "fence"),
    /** Legacy id {@code 107} ({@code minecraft:fence_gate}) maps to {@link Material#OAK_FENCE_GATE}. */
    OAK_FENCE_GATE("107", 107, 0, "OAK_FENCE_GATE", "fence_gate"),
    /** Legacy id {@code 18} ({@code minecraft:leaves}) maps to {@link Material#OAK_LEAVES}. */
    OAK_LEAVES("18", 18, 0, "OAK_LEAVES", "leaves"),
    /** Legacy id {@code 6} ({@code minecraft:sapling}) maps to {@link Material#OAK_SAPLING}. */
    OAK_SAPLING("6", 6, 0, "OAK_SAPLING", "sapling"),
    /** Legacy id {@code 17} ({@code minecraft:log}) maps to {@link Material#OAK_WOOD}. */
    OAK_WOOD("17", 17, 0, "OAK_WOOD", "log"),
    /** Legacy id {@code 5} ({@code minecraft:planks}) maps to {@link Material#OAK_PLANKS}. */
    OAK_WOOD_PLANK("5", 5, 0, "OAK_PLANKS", "planks"),
    /** Legacy id {@code 126} ({@code minecraft:wooden_slab}) maps to {@link Material#OAK_SLAB}. */
    OAK_WOOD_SLAB("126", 126, 0, "OAK_SLAB", "wooden_slab"),
    /** Legacy id {@code 53} ({@code minecraft:oak_stairs}) maps to {@link Material#OAK_STAIRS}. */
    OAK_WOOD_STAIRS("53", 53, 0, "OAK_STAIRS", "oak_stairs"),
    /** Legacy id {@code 218} ({@code minecraft:observer}) maps to {@link Material#OBSERVER}. */
    OBSERVER("218", 218, 0, "OBSERVER", "observer"),
    /** Legacy id {@code 49} ({@code minecraft:obsidian}) maps to {@link Material#OBSIDIAN}. */
    OBSIDIAN("49", 49, 0, "OBSIDIAN", "obsidian"),
    /** Legacy id {@code 171:1} ({@code minecraft:carpet}) maps to {@link Material#ORANGE_CARPET}. */
    ORANGE_CARPET("171:1", 171, 1, "ORANGE_CARPET", "carpet"),
    /** Legacy id {@code 251:1} ({@code minecraft:concrete}) maps to {@link Material#ORANGE_CONCRETE}. */
    ORANGE_CONCRETE("251:1", 251, 1, "ORANGE_CONCRETE", "concrete"),
    /** Legacy id {@code 252:1} ({@code minecraft:concrete_powder}) maps to {@link Material#ORANGE_CONCRETE_POWDER}. */
    ORANGE_CONCRETE_POWDER("252:1", 252, 1, "ORANGE_CONCRETE_POWDER", "concrete_powder"),
    /** Legacy id {@code 351:14} ({@code minecraft:dye}) maps to {@link Material#ORANGE_DYE}. */
    ORANGE_DYE("351:14", 351, 14, "ORANGE_DYE", "dye"),
    /** Legacy id {@code 236} ({@code minecraft:orange_glazed_terracotta}) maps to {@link Material#ORANGE_GLAZED_TERRACOTTA}. */
    ORANGE_GLAZED_TERRACOTTA("236", 236, 0, "ORANGE_GLAZED_TERRACOTTA", "orange_glazed_terracotta"),
    /** Legacy id {@code 159:1} ({@code minecraft:stained_hardened_clay}) maps to {@link Material#ORANGE_TERRACOTTA}. */
    ORANGE_HARDENED_CLAY("159:1", 159, 1, "ORANGE_TERRACOTTA", "stained_hardened_clay"),
    /** Legacy id {@code 220} ({@code minecraft:orange_shulker_box}) maps to {@link Material#ORANGE_SHULKER_BOX}. */
    ORANGE_SHULKER_BOX("220", 220, 0, "ORANGE_SHULKER_BOX", "orange_shulker_box"),
    /** Legacy id {@code 95:1} ({@code minecraft:stained_glass}) maps to {@link Material#ORANGE_STAINED_GLASS}. */
    ORANGE_STAINED_GLASS("95:1", 95, 1, "ORANGE_STAINED_GLASS", "stained_glass"),
    /** Legacy id {@code 160:1} ({@code minecraft:stained_glass_pane}) maps to {@link Material#ORANGE_STAINED_GLASS_PANE}. */
    ORANGE_STAINED_GLASS_PANE("160:1", 160, 1, "ORANGE_STAINED_GLASS_PANE", "stained_glass_pane"),
    /** Legacy id {@code 38:5} ({@code minecraft:red_flower}) maps to {@link Material#ORANGE_TULIP}. */
    ORANGE_TULIP("38:5", 38, 5, "ORANGE_TULIP", "red_flower"),
    /** Legacy id {@code 35:1} ({@code minecraft:wool}) maps to {@link Material#ORANGE_WOOL}. */
    ORANGE_WOOL("35:1", 35, 1, "ORANGE_WOOL", "wool"),
    /** Legacy id {@code 38:8} ({@code minecraft:red_flower}) maps to {@link Material#OXEYE_DAISY}. */
    OXEYE_DAISY("38:8", 38, 8, "OXEYE_DAISY", "red_flower"),
    /** Legacy id {@code 174} ({@code minecraft:packed_ice}) maps to {@link Material#PACKED_ICE}. */
    PACKED_ICE("174", 174, 0, "PACKED_ICE", "packed_ice"),
    /** Legacy id {@code 321} ({@code minecraft:painting}) maps to {@link Material#PAINTING}. */
    PAINTING("321", 321, 0, "PAINTING", "painting"),
    /** Legacy id {@code 339} ({@code minecraft:paper}) maps to {@link Material#PAPER}. */
    PAPER("339", 339, 0, "PAPER", "paper"),
    /** Legacy id {@code 175:5} ({@code minecraft:double_plant}) maps to {@link Material#PEONY}. */
    PEONY("175:5", 175, 5, "PEONY", "double_plant"),
    /** Legacy id {@code 155:2} ({@code minecraft:quartz_block}) maps to {@link Material#QUARTZ_PILLAR}. */
    PILLAR_QUARTZ_BLOCK("155:2", 155, 2, "QUARTZ_PILLAR", "quartz_block"),
    /** Legacy id {@code 171:6} ({@code minecraft:carpet}) maps to {@link Material#PINK_CARPET}. */
    PINK_CARPET("171:6", 171, 6, "PINK_CARPET", "carpet"),
    /** Legacy id {@code 251:6} ({@code minecraft:concrete}) maps to {@link Material#PINK_CONCRETE}. */
    PINK_CONCRETE("251:6", 251, 6, "PINK_CONCRETE", "concrete"),
    /** Legacy id {@code 252:6} ({@code minecraft:concrete_powder}) maps to {@link Material#PINK_CONCRETE_POWDER}. */
    PINK_CONCRETE_POWDER("252:6", 252, 6, "PINK_CONCRETE_POWDER", "concrete_powder"),
    /** Legacy id {@code 351:9} ({@code minecraft:dye}) maps to {@link Material#PINK_DYE}. */
    PINK_DYE("351:9", 351, 9, "PINK_DYE", "dye"),
    /** Legacy id {@code 241} ({@code minecraft:pink_glazed_terracotta}) maps to {@link Material#PINK_GLAZED_TERRACOTTA}. */
    PINK_GLAZED_TERRACOTTA("241", 241, 0, "PINK_GLAZED_TERRACOTTA", "pink_glazed_terracotta"),
    /** Legacy id {@code 159:6} ({@code minecraft:stained_hardened_clay}) maps to {@link Material#PINK_TERRACOTTA}. */
    PINK_HARDENED_CLAY("159:6", 159, 6, "PINK_TERRACOTTA", "stained_hardened_clay"),
    /** Legacy id {@code 225} ({@code minecraft:pink_shulker_box}) maps to {@link Material#PINK_SHULKER_BOX}. */
    PINK_SHULKER_BOX("225", 225, 0, "PINK_SHULKER_BOX", "pink_shulker_box"),
    /** Legacy id {@code 95:6} ({@code minecraft:stained_glass}) maps to {@link Material#PINK_STAINED_GLASS}. */
    PINK_STAINED_GLASS("95:6", 95, 6, "PINK_STAINED_GLASS", "stained_glass"),
    /** Legacy id {@code 160:6} ({@code minecraft:stained_glass_pane}) maps to {@link Material#PINK_STAINED_GLASS_PANE}. */
    PINK_STAINED_GLASS_PANE("160:6", 160, 6, "PINK_STAINED_GLASS_PANE", "stained_glass_pane"),
    /** Legacy id {@code 38:7} ({@code minecraft:red_flower}) maps to {@link Material#PINK_TULIP}. */
    PINK_TULIP("38:7", 38, 7, "PINK_TULIP", "red_flower"),
    /** Legacy id {@code 35:6} ({@code minecraft:wool}) maps to {@link Material#PINK_WOOL}. */
    PINK_WOOL("35:6", 35, 6, "PINK_WOOL", "wool"),
    /** Legacy id {@code 33} ({@code minecraft:piston}) maps to {@link Material#PISTON}. */
    PISTON("33", 33, 0, "PISTON", "piston"),
    /** Legacy id {@code 34} ({@code minecraft:piston_head}) maps to {@link Material#PISTON_HEAD}. */
    PISTON_HEAD("34", 34, 0, "PISTON_HEAD", "piston_head"),
    /** Legacy id {@code 3:2} ({@code minecraft:dirt}) maps to {@link Material#PODZOL}. */
    PODZOL("3:2", 3, 2, "PODZOL", "dirt"),
    /** Legacy id {@code 394} ({@code minecraft:poisonous_potato}) maps to {@link Material#POISONOUS_POTATO}. */
    POISONOUS_POTATO("394", 394, 0, "POISONOUS_POTATO", "poisonous_potato"),
    /** Legacy id {@code 1:6} ({@code minecraft:stone}) maps to {@link Material#POLISHED_ANDESITE}. */
    POLISHED_ANDESITE("1:6", 1, 6, "POLISHED_ANDESITE", "stone"),
    /** Legacy id {@code 1:4} ({@code minecraft:stone}) maps to {@link Material#POLISHED_DIORITE}. */
    POLISHED_DIORITE("1:4", 1, 4, "POLISHED_DIORITE", "stone"),
    /** Legacy id {@code 1:2} ({@code minecraft:stone}) maps to {@link Material#POLISHED_GRANITE}. */
    POLISHED_GRANITE("1:2", 1, 2, "POLISHED_GRANITE", "stone"),
    /** Legacy id {@code 433} ({@code minecraft:popped_chorus_fruit}) maps to {@link Material#POPPED_CHORUS_FRUIT}. */
    POPPED_CHORUS_FRUIT("433", 433, 0, "POPPED_CHORUS_FRUIT", "popped_chorus_fruit"),
    /** Legacy id {@code 38} ({@code minecraft:red_flower}) maps to {@link Material#POPPY}. */
    POPPY("38", 38, 0, "POPPY", "red_flower"),
    /** Legacy id {@code 392} ({@code minecraft:potato}) maps to {@link Material#POTATO}. */
    POTATO("392", 392, 0, "POTATO", "potato"),
    /** Legacy id {@code 142} ({@code minecraft:potatoes}) maps to {@link Material#POTATOES}. */
    POTATOES("142", 142, 0, "POTATOES", "potatoes"),
    /** Legacy id {@code 373} ({@code minecraft:potion}) maps to {@link Material#POTION}. */
    POTION("373", 373, 0, "POTION", "potion"),
    /** Legacy id {@code 27} ({@code minecraft:golden_rail}) maps to {@link Material#POWERED_RAIL}. */
    POWERED_RAIL("27", 27, 0, "POWERED_RAIL", "golden_rail"),
    /** Legacy id {@code 168} ({@code minecraft:prismarine}) maps to {@link Material#PRISMARINE}. */
    PRISMARINE("168", 168, 0, "PRISMARINE", "prismarine"),
    /** Legacy id {@code 168:1} ({@code minecraft:prismarine}) maps to {@link Material#PRISMARINE_BRICKS}. */
    PRISMARINE_BRICKS("168:1", 168, 1, "PRISMARINE_BRICKS", "prismarine"),
    /** Legacy id {@code 410} ({@code minecraft:prismarine_crystals}) maps to {@link Material#PRISMARINE_CRYSTALS}. */
    PRISMARINE_CRYSTALS("410", 410, 0, "PRISMARINE_CRYSTALS", "prismarine_crystals"),
    /** Legacy id {@code 409} ({@code minecraft:prismarine_shard}) maps to {@link Material#PRISMARINE_SHARD}. */
    PRISMARINE_SHARD("409", 409, 0, "PRISMARINE_SHARD", "prismarine_shard"),
    /** Legacy id {@code 349:3} ({@code minecraft:fish}) maps to {@link Material#PUFFERFISH}. */
    PUFFERFISH("349:3", 349, 3, "PUFFERFISH", "fish"),
    /** Legacy id {@code 86} ({@code minecraft:pumpkin}) maps to {@link Material#PUMPKIN}. */
    PUMPKIN("86", 86, 0, "PUMPKIN", "pumpkin"),
    /** Legacy id {@code 400} ({@code minecraft:pumpkin_pie}) maps to {@link Material#PUMPKIN_PIE}. */
    PUMPKIN_PIE("400", 400, 0, "PUMPKIN_PIE", "pumpkin_pie"),
    /** Legacy id {@code 361} ({@code minecraft:pumpkin_seeds}) maps to {@link Material#PUMPKIN_SEEDS}. */
    PUMPKIN_SEEDS("361", 361, 0, "PUMPKIN_SEEDS", "pumpkin_seeds"),
    /** Legacy id {@code 104} ({@code minecraft:pumpkin_stem}) maps to {@link Material#PUMPKIN_STEM}. */
    PUMPKIN_STEM("104", 104, 0, "PUMPKIN_STEM", "pumpkin_stem"),
    /** Legacy id {@code 171:10} ({@code minecraft:carpet}) maps to {@link Material#PURPLE_CARPET}. */
    PURPLE_CARPET("171:10", 171, 10, "PURPLE_CARPET", "carpet"),
    /** Legacy id {@code 251:10} ({@code minecraft:concrete}) maps to {@link Material#PURPLE_CONCRETE}. */
    PURPLE_CONCRETE("251:10", 251, 10, "PURPLE_CONCRETE", "concrete"),
    /** Legacy id {@code 252:10} ({@code minecraft:concrete_powder}) maps to {@link Material#PURPLE_CONCRETE_POWDER}. */
    PURPLE_CONCRETE_POWDER("252:10", 252, 10, "PURPLE_CONCRETE_POWDER", "concrete_powder"),
    /** Legacy id {@code 351:5} ({@code minecraft:dye}) maps to {@link Material#PURPLE_DYE}. */
    PURPLE_DYE("351:5", 351, 5, "PURPLE_DYE", "dye"),
    /** Legacy id {@code 245} ({@code minecraft:purple_glazed_terracotta}) maps to {@link Material#PURPLE_GLAZED_TERRACOTTA}. */
    PURPLE_GLAZED_TERRACOTTA("245", 245, 0, "PURPLE_GLAZED_TERRACOTTA", "purple_glazed_terracotta"),
    /** Legacy id {@code 159:10} ({@code minecraft:stained_hardened_clay}) maps to {@link Material#PURPLE_TERRACOTTA}. */
    PURPLE_HARDENED_CLAY("159:10", 159, 10, "PURPLE_TERRACOTTA", "stained_hardened_clay"),
    /** Legacy id {@code 229} ({@code minecraft:purple_shulker_box}) maps to {@link Material#PURPLE_SHULKER_BOX}. */
    PURPLE_SHULKER_BOX("229", 229, 0, "PURPLE_SHULKER_BOX", "purple_shulker_box"),
    /** Legacy id {@code 95:10} ({@code minecraft:stained_glass}) maps to {@link Material#PURPLE_STAINED_GLASS}. */
    PURPLE_STAINED_GLASS("95:10", 95, 10, "PURPLE_STAINED_GLASS", "stained_glass"),
    /** Legacy id {@code 160:10} ({@code minecraft:stained_glass_pane}) maps to {@link Material#PURPLE_STAINED_GLASS_PANE}. */
    PURPLE_STAINED_GLASS_PANE("160:10", 160, 10, "PURPLE_STAINED_GLASS_PANE", "stained_glass_pane"),
    /** Legacy id {@code 35:10} ({@code minecraft:wool}) maps to {@link Material#PURPLE_WOOL}. */
    PURPLE_WOOL("35:10", 35, 10, "PURPLE_WOOL", "wool"),
    /** Legacy id {@code 201} ({@code minecraft:purpur_block}) maps to {@link Material#PURPUR_BLOCK}. */
    PURPUR_BLOCK("201", 201, 0, "PURPUR_BLOCK", "purpur_block"),
    /** Legacy id {@code 204} ({@code minecraft:purpur_double_slab}) maps to {@link Material#PURPUR_BLOCK}. */
    PURPUR_DOUBLE_SLAB("204", 204, 0, "PURPUR_BLOCK", "purpur_double_slab"),
    /** Legacy id {@code 202} ({@code minecraft:purpur_pillar}) maps to {@link Material#PURPUR_PILLAR}. */
    PURPUR_PILLAR("202", 202, 0, "PURPUR_PILLAR", "purpur_pillar"),
    /** Legacy id {@code 205} ({@code minecraft:purpur_slab}) maps to {@link Material#PURPUR_SLAB}. */
    PURPUR_SLAB("205", 205, 0, "PURPUR_SLAB", "purpur_slab"),
    /** Legacy id {@code 203} ({@code minecraft:purpur_stairs}) maps to {@link Material#PURPUR_STAIRS}. */
    PURPUR_STAIRS("203", 203, 0, "PURPUR_STAIRS", "purpur_stairs"),
    /** Legacy id {@code 155} ({@code minecraft:quartz_block}) maps to {@link Material#QUARTZ_BLOCK}. */
    QUARTZ_BLOCK("155", 155, 0, "QUARTZ_BLOCK", "quartz_block"),
    /** Legacy id {@code 44:7} ({@code minecraft:stone_slab}) maps to {@link Material#QUARTZ_SLAB}. */
    QUARTZ_SLAB("44:7", 44, 7, "QUARTZ_SLAB", "stone_slab"),
    /** Legacy id {@code 156} ({@code minecraft:quartz_stairs}) maps to {@link Material#QUARTZ_STAIRS}. */
    QUARTZ_STAIRS("156", 156, 0, "QUARTZ_STAIRS", "quartz_stairs"),
    /** Legacy id {@code 415} ({@code minecraft:rabbit_hide}) maps to {@link Material#RABBIT_HIDE}. */
    RABBIT_HIDE("415", 415, 0, "RABBIT_HIDE", "rabbit_hide"),
    /** Legacy id {@code 414} ({@code minecraft:rabbit_foot}) maps to {@link Material#RABBIT_FOOT}. */
    RABBIT_S_FOOT("414", 414, 0, "RABBIT_FOOT", "rabbit_foot"),
    /** Legacy id {@code 413} ({@code minecraft:rabbit_stew}) maps to {@link Material#RABBIT_STEW}. */
    RABBIT_STEW("413", 413, 0, "RABBIT_STEW", "rabbit_stew"),
    /** Legacy id {@code 66} ({@code minecraft:rail}) maps to {@link Material#RAIL}. */
    RAIL("66", 66, 0, "RAIL", "rail"),
    /** Legacy id {@code 363} ({@code minecraft:beef}) maps to {@link Material#BEEF}. */
    RAW_BEEF("363", 363, 0, "BEEF", "beef"),
    /** Legacy id {@code 365} ({@code minecraft:chicken}) maps to {@link Material#CHICKEN}. */
    RAW_CHICKEN("365", 365, 0, "CHICKEN", "chicken"),
    /** Legacy id {@code 349} ({@code minecraft:fish}) maps to {@link Material#COD}. */
    RAW_FISH("349", 349, 0, "COD", "fish"),
    /** Legacy id {@code 423} ({@code minecraft:mutton}) maps to {@link Material#MUTTON}. */
    RAW_MUTTON("423", 423, 0, "MUTTON", "mutton"),
    /** Legacy id {@code 319} ({@code minecraft:porkchop}) maps to {@link Material#PORKCHOP}. */
    RAW_PORKCHOP("319", 319, 0, "PORKCHOP", "porkchop"),
    /** Legacy id {@code 411} ({@code minecraft:rabbit}) maps to {@link Material#RABBIT}. */
    RAW_RABBIT("411", 411, 0, "RABBIT", "rabbit"),
    /** Legacy id {@code 349:1} ({@code minecraft:fish}) maps to {@link Material#SALMON}. */
    RAW_SALMON("349:1", 349, 1, "SALMON", "fish"),
    /** Legacy id {@code 171:14} ({@code minecraft:carpet}) maps to {@link Material#RED_CARPET}. */
    RED_CARPET("171:14", 171, 14, "RED_CARPET", "carpet"),
    /** Legacy id {@code 251:14} ({@code minecraft:concrete}) maps to {@link Material#RED_CONCRETE}. */
    RED_CONCRETE("251:14", 251, 14, "RED_CONCRETE", "concrete"),
    /** Legacy id {@code 252:14} ({@code minecraft:concrete_powder}) maps to {@link Material#RED_CONCRETE_POWDER}. */
    RED_CONCRETE_POWDER("252:14", 252, 14, "RED_CONCRETE_POWDER", "concrete_powder"),
    /** Legacy id {@code 249} ({@code minecraft:red_glazed_terracotta}) maps to {@link Material#RED_GLAZED_TERRACOTTA}. */
    RED_GLAZED_TERRACOTTA("249", 249, 0, "RED_GLAZED_TERRACOTTA", "red_glazed_terracotta"),
    /** Legacy id {@code 159:14} ({@code minecraft:stained_hardened_clay}) maps to {@link Material#RED_TERRACOTTA}. */
    RED_HARDENED_CLAY("159:14", 159, 14, "RED_TERRACOTTA", "stained_hardened_clay"),
    /** Legacy id {@code 40} ({@code minecraft:red_mushroom}) maps to {@link Material#RED_MUSHROOM}. */
    RED_MUSHROOM("40", 40, 0, "RED_MUSHROOM", "red_mushroom"),
    /** Legacy id {@code 100} ({@code minecraft:red_mushroom_block}) maps to {@link Material#RED_MUSHROOM_BLOCK}. */
    RED_MUSHROOM_BLOCK("100", 100, 0, "RED_MUSHROOM_BLOCK", "red_mushroom_block"),
    /** Legacy id {@code 215} ({@code minecraft:red_nether_brick}) maps to {@link Material#RED_NETHER_BRICKS}. */
    RED_NETHER_BRICK("215", 215, 0, "RED_NETHER_BRICKS", "red_nether_brick"),
    /** Legacy id {@code 12:1} ({@code minecraft:sand}) maps to {@link Material#RED_SAND}. */
    RED_SAND("12:1", 12, 1, "RED_SAND", "sand"),
    /** Legacy id {@code 179} ({@code minecraft:red_sandstone}) maps to {@link Material#RED_SANDSTONE}. */
    RED_SANDSTONE("179", 179, 0, "RED_SANDSTONE", "red_sandstone"),
    /** Legacy id {@code 182} ({@code minecraft:stone_slab2}) maps to {@link Material#RED_SANDSTONE_SLAB}. */
    RED_SANDSTONE_SLAB("182", 182, 0, "RED_SANDSTONE_SLAB", "stone_slab2"),
    /** Legacy id {@code 180} ({@code minecraft:red_sandstone_stairs}) maps to {@link Material#RED_SANDSTONE_STAIRS}. */
    RED_SANDSTONE_STAIRS("180", 180, 0, "RED_SANDSTONE_STAIRS", "red_sandstone_stairs"),
    /** Legacy id {@code 233} ({@code minecraft:red_shulker_box}) maps to {@link Material#RED_SHULKER_BOX}. */
    RED_SHULKER_BOX("233", 233, 0, "RED_SHULKER_BOX", "red_shulker_box"),
    /** Legacy id {@code 95:14} ({@code minecraft:stained_glass}) maps to {@link Material#RED_STAINED_GLASS}. */
    RED_STAINED_GLASS("95:14", 95, 14, "RED_STAINED_GLASS", "stained_glass"),
    /** Legacy id {@code 160:14} ({@code minecraft:stained_glass_pane}) maps to {@link Material#RED_STAINED_GLASS_PANE}. */
    RED_STAINED_GLASS_PANE("160:14", 160, 14, "RED_STAINED_GLASS_PANE", "stained_glass_pane"),
    /** Legacy id {@code 38:4} ({@code minecraft:red_flower}) maps to {@link Material#RED_TULIP}. */
    RED_TULIP("38:4", 38, 4, "RED_TULIP", "red_flower"),
    /** Legacy id {@code 35:14} ({@code minecraft:wool}) maps to {@link Material#RED_WOOL}. */
    RED_WOOL("35:14", 35, 14, "RED_WOOL", "wool"),
    /** Legacy id {@code 331} ({@code minecraft:redstone}) maps to {@link Material#REDSTONE}. */
    REDSTONE("331", 331, 0, "REDSTONE", "redstone"),
    /** Legacy id {@code 152} ({@code minecraft:redstone_block}) maps to {@link Material#REDSTONE_BLOCK}. */
    REDSTONE_BLOCK("152", 152, 0, "REDSTONE_BLOCK", "redstone_block"),
    /** Legacy id {@code 404} ({@code minecraft:comparator}) maps to {@link Material#COMPARATOR}. */
    REDSTONE_COMPARATOR("404", 404, 0, "COMPARATOR", "comparator"),
    /** Legacy id {@code 150} ({@code minecraft:powered_comparator}) maps to {@link Material#COMPARATOR}. */
    REDSTONE_COMPARATOR_ACTIVE("150", 150, 0, "COMPARATOR", "powered_comparator"),
    /** Legacy id {@code 149} ({@code minecraft:unpowered_comparator}) maps to {@link Material#COMPARATOR}. */
    REDSTONE_COMPARATOR_INACTIVE("149", 149, 0, "COMPARATOR", "unpowered_comparator"),
    /** Legacy id {@code 124} ({@code minecraft:lit_redstone_lamp}) maps to {@link Material#REDSTONE_LAMP}. */
    REDSTONE_LAMP_ACTIVE("124", 124, 0, "REDSTONE_LAMP", "lit_redstone_lamp"),
    /** Legacy id {@code 123} ({@code minecraft:redstone_lamp}) maps to {@link Material#REDSTONE_LAMP}. */
    REDSTONE_LAMP_INACTIVE("123", 123, 0, "REDSTONE_LAMP", "redstone_lamp"),
    /** Legacy id {@code 73} ({@code minecraft:redstone_ore}) maps to {@link Material#REDSTONE_ORE}. */
    REDSTONE_ORE("73", 73, 0, "REDSTONE_ORE", "redstone_ore"),
    /** Legacy id {@code 356} ({@code minecraft:repeater}) maps to {@link Material#REPEATER}. */
    REDSTONE_REPEATER("356", 356, 0, "REPEATER", "repeater"),
    /** Legacy id {@code 93} ({@code minecraft:unpowered_repeater}) maps to {@link Material#REPEATER}. */
    REDSTONE_REPEATER_BLOCK_OFF("93", 93, 0, "REPEATER", "unpowered_repeater"),
    /** Legacy id {@code 94} ({@code minecraft:powered_repeater}) maps to {@link Material#REPEATER}. */
    REDSTONE_REPEATER_BLOCK_ON("94", 94, 0, "REPEATER", "powered_repeater"),
    /** Legacy id {@code 75} ({@code minecraft:unlit_redstone_torch}) maps to {@link Material#REDSTONE_TORCH}. */
    REDSTONE_TORCH_OFF("75", 75, 0, "REDSTONE_TORCH", "unlit_redstone_torch"),
    /** Legacy id {@code 76} ({@code minecraft:redstone_torch}) maps to {@link Material#REDSTONE_TORCH}. */
    REDSTONE_TORCH_ON("76", 76, 0, "REDSTONE_TORCH", "redstone_torch"),
    /** Legacy id {@code 55} ({@code minecraft:redstone_wire}) maps to {@link Material#REDSTONE_WIRE}. */
    REDSTONE_WIRE("55", 55, 0, "REDSTONE_WIRE", "redstone_wire"),
    /** Legacy id {@code 210} ({@code minecraft:repeating_command_block}) maps to {@link Material#REPEATING_COMMAND_BLOCK}. */
    REPEATING_COMMAND_BLOCK("210", 210, 0, "REPEATING_COMMAND_BLOCK", "repeating_command_block"),
    /** Legacy id {@code 175:4} ({@code minecraft:double_plant}) maps to {@link Material#ROSE_BUSH}. */
    ROSE_BUSH("175:4", 175, 4, "ROSE_BUSH", "double_plant"),
    /** Legacy id {@code 351:1} ({@code minecraft:dye}) maps to {@link Material#RED_DYE}. */
    ROSE_RED("351:1", 351, 1, "RED_DYE", "dye"),
    /** Legacy id {@code 367} ({@code minecraft:rotten_flesh}) maps to {@link Material#ROTTEN_FLESH}. */
    ROTTEN_FLESH("367", 367, 0, "ROTTEN_FLESH", "rotten_flesh"),
    /** Legacy id {@code 329} ({@code minecraft:saddle}) maps to {@link Material#SADDLE}. */
    SADDLE("329", 329, 0, "SADDLE", "saddle"),
    /** Legacy id {@code 12} ({@code minecraft:sand}) maps to {@link Material#SAND}. */
    SAND("12", 12, 0, "SAND", "sand"),
    /** Legacy id {@code 24} ({@code minecraft:sandstone}) maps to {@link Material#SANDSTONE}. */
    SANDSTONE("24", 24, 0, "SANDSTONE", "sandstone"),
    /** Legacy id {@code 44:1} ({@code minecraft:stone_slab}) maps to {@link Material#SANDSTONE_SLAB}. */
    SANDSTONE_SLAB("44:1", 44, 1, "SANDSTONE_SLAB", "stone_slab"),
    /** Legacy id {@code 128} ({@code minecraft:sandstone_stairs}) maps to {@link Material#SANDSTONE_STAIRS}. */
    SANDSTONE_STAIRS("128", 128, 0, "SANDSTONE_STAIRS", "sandstone_stairs"),
    /** Legacy id {@code 169} ({@code minecraft:sea_lantern}) maps to {@link Material#SEA_LANTERN}. */
    SEA_LANTERN("169", 169, 0, "SEA_LANTERN", "sea_lantern"),
    /** Legacy id {@code 359} ({@code minecraft:shears}) maps to {@link Material#SHEARS}. */
    SHEARS("359", 359, 0, "SHEARS", "shears"),
    /** Legacy id {@code 442} ({@code minecraft:shield}) maps to {@link Material#SHIELD}. */
    SHIELD("442", 442, 0, "SHIELD", "shield"),
    /** Legacy id {@code 450} ({@code minecraft:shulker_shell}) maps to {@link Material#SHULKER_SHELL}. */
    SHULKER_SHELL("450", 450, 0, "SHULKER_SHELL", "shulker_shell"),
    /** Legacy id {@code 323} ({@code minecraft:sign}) maps to {@link Material#OAK_SIGN}. */
    SIGN("323", 323, 0, "OAK_SIGN", "sign"),
    /** Legacy id {@code 165} ({@code minecraft:slime}) maps to {@link Material#SLIME_BLOCK}. */
    SLIME_BLOCK("165", 165, 0, "SLIME_BLOCK", "slime"),
    /** Legacy id {@code 341} ({@code minecraft:slime_ball}) maps to {@link Material#SLIME_BALL}. */
    SLIMEBALL("341", 341, 0, "SLIME_BALL", "slime_ball"),
    /** Legacy id {@code 179:2} ({@code minecraft:red_sandstone}) maps to {@link Material#SMOOTH_RED_SANDSTONE}. */
    SMOOTH_RED_SANDSTONE("179:2", 179, 2, "SMOOTH_RED_SANDSTONE", "red_sandstone"),
    /** Legacy id {@code 24:2} ({@code minecraft:sandstone}) maps to {@link Material#SMOOTH_SANDSTONE}. */
    SMOOTH_SANDSTONE("24:2", 24, 2, "SMOOTH_SANDSTONE", "sandstone"),
    /** Legacy id {@code 78} ({@code minecraft:snow_layer}) maps to {@link Material#SNOW}. */
    SNOW("78", 78, 0, "SNOW", "snow_layer"),
    /** Legacy id {@code 80} ({@code minecraft:snow}) maps to {@link Material#SNOW_BLOCK}. */
    SNOW_BLOCK("80", 80, 0, "SNOW_BLOCK", "snow"),
    /** Legacy id {@code 332} ({@code minecraft:snowball}) maps to {@link Material#SNOWBALL}. */
    SNOWBALL("332", 332, 0, "SNOWBALL", "snowball"),
    /** Legacy id {@code 88} ({@code minecraft:soul_sand}) maps to {@link Material#SOUL_SAND}. */
    SOUL_SAND("88", 88, 0, "SOUL_SAND", "soul_sand"),
    /** Legacy id {@code 383:65} ({@code minecraft:spawn_egg}) maps to {@link Material#BAT_SPAWN_EGG}. */
    SPAWN_BAT("383:65", 383, 65, "BAT_SPAWN_EGG", "spawn_egg"),
    /** Legacy id {@code 383:61} ({@code minecraft:spawn_egg}) maps to {@link Material#BLAZE_SPAWN_EGG}. */
    SPAWN_BLAZE("383:61", 383, 61, "BLAZE_SPAWN_EGG", "spawn_egg"),
    /** Legacy id {@code 383:59} ({@code minecraft:spawn_egg}) maps to {@link Material#CAVE_SPIDER_SPAWN_EGG}. */
    SPAWN_CAVE_SPIDER("383:59", 383, 59, "CAVE_SPIDER_SPAWN_EGG", "spawn_egg"),
    /** Legacy id {@code 383:93} ({@code minecraft:spawn_egg}) maps to {@link Material#CHICKEN_SPAWN_EGG}. */
    SPAWN_CHICKEN("383:93", 383, 93, "CHICKEN_SPAWN_EGG", "spawn_egg"),
    /** Legacy id {@code 383:92} ({@code minecraft:spawn_egg}) maps to {@link Material#COW_SPAWN_EGG}. */
    SPAWN_COW("383:92", 383, 92, "COW_SPAWN_EGG", "spawn_egg"),
    /** Legacy id {@code 383:50} ({@code minecraft:spawn_egg}) maps to {@link Material#CREEPER_SPAWN_EGG}. */
    SPAWN_CREEPER("383:50", 383, 50, "CREEPER_SPAWN_EGG", "spawn_egg"),
    /** Legacy id {@code 383:31} ({@code minecraft:spawn_egg}) maps to {@link Material#DONKEY_SPAWN_EGG}. */
    SPAWN_DONKEY("383:31", 383, 31, "DONKEY_SPAWN_EGG", "spawn_egg"),
    /** Legacy id {@code 383:4} ({@code minecraft:spawn_egg}) maps to {@link Material#ELDER_GUARDIAN_SPAWN_EGG}. */
    SPAWN_ELDER_GUARDIAN("383:4", 383, 4, "ELDER_GUARDIAN_SPAWN_EGG", "spawn_egg"),
    /** Legacy id {@code 383:58} ({@code minecraft:spawn_egg}) maps to {@link Material#ENDERMAN_SPAWN_EGG}. */
    SPAWN_ENDERMAN("383:58", 383, 58, "ENDERMAN_SPAWN_EGG", "spawn_egg"),
    /** Legacy id {@code 383:67} ({@code minecraft:spawn_egg}) maps to {@link Material#ENDERMITE_SPAWN_EGG}. */
    SPAWN_ENDERMITE("383:67", 383, 67, "ENDERMITE_SPAWN_EGG", "spawn_egg"),
    /** Legacy id {@code 383:34} ({@code minecraft:spawn_egg}) maps to {@link Material#EVOKER_SPAWN_EGG}. */
    SPAWN_EVOKER("383:34", 383, 34, "EVOKER_SPAWN_EGG", "spawn_egg"),
    /** Legacy id {@code 383:56} ({@code minecraft:spawn_egg}) maps to {@link Material#GHAST_SPAWN_EGG}. */
    SPAWN_GHAST("383:56", 383, 56, "GHAST_SPAWN_EGG", "spawn_egg"),
    /** Legacy id {@code 383:68} ({@code minecraft:spawn_egg}) maps to {@link Material#GUARDIAN_SPAWN_EGG}. */
    SPAWN_GUARDIAN("383:68", 383, 68, "GUARDIAN_SPAWN_EGG", "spawn_egg"),
    /** Legacy id {@code 383:100} ({@code minecraft:spawn_egg}) maps to {@link Material#HORSE_SPAWN_EGG}. */
    SPAWN_HORSE("383:100", 383, 100, "HORSE_SPAWN_EGG", "spawn_egg"),
    /** Legacy id {@code 383:23} ({@code minecraft:spawn_egg}) maps to {@link Material#HUSK_SPAWN_EGG}. */
    SPAWN_HUSK("383:23", 383, 23, "HUSK_SPAWN_EGG", "spawn_egg"),
    /** Legacy id {@code 383:103} ({@code minecraft:spawn_egg}) maps to {@link Material#LLAMA_SPAWN_EGG}. */
    SPAWN_LLAMA("383:103", 383, 103, "LLAMA_SPAWN_EGG", "spawn_egg"),
    /** Legacy id {@code 383:62} ({@code minecraft:spawn_egg}) maps to {@link Material#MAGMA_CUBE_SPAWN_EGG}. */
    SPAWN_MAGMA_CUBE("383:62", 383, 62, "MAGMA_CUBE_SPAWN_EGG", "spawn_egg"),
    /** Legacy id {@code 383:96} ({@code minecraft:spawn_egg}) maps to {@link Material#MOOSHROOM_SPAWN_EGG}. */
    SPAWN_MOOSHROOM("383:96", 383, 96, "MOOSHROOM_SPAWN_EGG", "spawn_egg"),
    /** Legacy id {@code 383:32} ({@code minecraft:spawn_egg}) maps to {@link Material#MULE_SPAWN_EGG}. */
    SPAWN_MULE("383:32", 383, 32, "MULE_SPAWN_EGG", "spawn_egg"),
    /** Legacy id {@code 383:98} ({@code minecraft:spawn_egg}) maps to {@link Material#OCELOT_SPAWN_EGG}. */
    SPAWN_OCELOT("383:98", 383, 98, "OCELOT_SPAWN_EGG", "spawn_egg"),
    /** Legacy id {@code 383:105} ({@code minecraft:spawn_egg}) maps to {@link Material#PARROT_SPAWN_EGG}. */
    SPAWN_PARROT("383:105", 383, 105, "PARROT_SPAWN_EGG", "spawn_egg"),
    /** Legacy id {@code 383:90} ({@code minecraft:spawn_egg}) maps to {@link Material#PIG_SPAWN_EGG}. */
    SPAWN_PIG("383:90", 383, 90, "PIG_SPAWN_EGG", "spawn_egg"),
    /** Legacy id {@code 383:102} ({@code minecraft:spawn_egg}) maps to {@link Material#POLAR_BEAR_SPAWN_EGG}. */
    SPAWN_POLAR_BEAR("383:102", 383, 102, "POLAR_BEAR_SPAWN_EGG", "spawn_egg"),
    /** Legacy id {@code 383:101} ({@code minecraft:spawn_egg}) maps to {@link Material#RABBIT_SPAWN_EGG}. */
    SPAWN_RABBIT("383:101", 383, 101, "RABBIT_SPAWN_EGG", "spawn_egg"),
    /** Legacy id {@code 383:91} ({@code minecraft:spawn_egg}) maps to {@link Material#SHEEP_SPAWN_EGG}. */
    SPAWN_SHEEP("383:91", 383, 91, "SHEEP_SPAWN_EGG", "spawn_egg"),
    /** Legacy id {@code 383:69} ({@code minecraft:spawn_egg}) maps to {@link Material#SHULKER_SPAWN_EGG}. */
    SPAWN_SHULKER("383:69", 383, 69, "SHULKER_SPAWN_EGG", "spawn_egg"),
    /** Legacy id {@code 383:60} ({@code minecraft:spawn_egg}) maps to {@link Material#SILVERFISH_SPAWN_EGG}. */
    SPAWN_SILVERFISH("383:60", 383, 60, "SILVERFISH_SPAWN_EGG", "spawn_egg"),
    /** Legacy id {@code 383:51} ({@code minecraft:spawn_egg}) maps to {@link Material#SKELETON_SPAWN_EGG}. */
    SPAWN_SKELETON("383:51", 383, 51, "SKELETON_SPAWN_EGG", "spawn_egg"),
    /** Legacy id {@code 383:28} ({@code minecraft:spawn_egg}) maps to {@link Material#SKELETON_HORSE_SPAWN_EGG}. */
    SPAWN_SKELETON_HORSE("383:28", 383, 28, "SKELETON_HORSE_SPAWN_EGG", "spawn_egg"),
    /** Legacy id {@code 383:55} ({@code minecraft:spawn_egg}) maps to {@link Material#SLIME_SPAWN_EGG}. */
    SPAWN_SLIME("383:55", 383, 55, "SLIME_SPAWN_EGG", "spawn_egg"),
    /** Legacy id {@code 383:52} ({@code minecraft:spawn_egg}) maps to {@link Material#SPIDER_SPAWN_EGG}. */
    SPAWN_SPIDER("383:52", 383, 52, "SPIDER_SPAWN_EGG", "spawn_egg"),
    /** Legacy id {@code 383:94} ({@code minecraft:spawn_egg}) maps to {@link Material#SQUID_SPAWN_EGG}. */
    SPAWN_SQUID("383:94", 383, 94, "SQUID_SPAWN_EGG", "spawn_egg"),
    /** Legacy id {@code 383:6} ({@code minecraft:spawn_egg}) maps to {@link Material#STRAY_SPAWN_EGG}. */
    SPAWN_STRAY("383:6", 383, 6, "STRAY_SPAWN_EGG", "spawn_egg"),
    /** Legacy id {@code 383:35} ({@code minecraft:spawn_egg}) maps to {@link Material#VEX_SPAWN_EGG}. */
    SPAWN_VEX("383:35", 383, 35, "VEX_SPAWN_EGG", "spawn_egg"),
    /** Legacy id {@code 383:120} ({@code minecraft:spawn_egg}) maps to {@link Material#VILLAGER_SPAWN_EGG}. */
    SPAWN_VILLAGER("383:120", 383, 120, "VILLAGER_SPAWN_EGG", "spawn_egg"),
    /** Legacy id {@code 383:36} ({@code minecraft:spawn_egg}) maps to {@link Material#VINDICATOR_SPAWN_EGG}. */
    SPAWN_VINDICATOR("383:36", 383, 36, "VINDICATOR_SPAWN_EGG", "spawn_egg"),
    /** Legacy id {@code 383:66} ({@code minecraft:spawn_egg}) maps to {@link Material#WITCH_SPAWN_EGG}. */
    SPAWN_WITCH("383:66", 383, 66, "WITCH_SPAWN_EGG", "spawn_egg"),
    /** Legacy id {@code 383:5} ({@code minecraft:spawn_egg}) maps to {@link Material#WITHER_SKELETON_SPAWN_EGG}. */
    SPAWN_WITHER_SKELETON("383:5", 383, 5, "WITHER_SKELETON_SPAWN_EGG", "spawn_egg"),
    /** Legacy id {@code 383:95} ({@code minecraft:spawn_egg}) maps to {@link Material#WOLF_SPAWN_EGG}. */
    SPAWN_WOLF("383:95", 383, 95, "WOLF_SPAWN_EGG", "spawn_egg"),
    /** Legacy id {@code 383:54} ({@code minecraft:spawn_egg}) maps to {@link Material#ZOMBIE_SPAWN_EGG}. */
    SPAWN_ZOMBIE("383:54", 383, 54, "ZOMBIE_SPAWN_EGG", "spawn_egg"),
    /** Legacy id {@code 383:29} ({@code minecraft:spawn_egg}) maps to {@link Material#ZOMBIE_HORSE_SPAWN_EGG}. */
    SPAWN_ZOMBIE_HORSE("383:29", 383, 29, "ZOMBIE_HORSE_SPAWN_EGG", "spawn_egg"),
    //SPAWN_ZOMBIE_PIGMAN("383:57", 383, 57", "SPAWN_ZOMBIE_PIGMAN", "spawn_egg"),
    /** Legacy id {@code 383:27} ({@code minecraft:spawn_egg}) maps to {@link Material#ZOMBIE_VILLAGER_SPAWN_EGG}. */
    SPAWN_ZOMBIE_VILLAGER("383:27", 383, 27, "ZOMBIE_VILLAGER_SPAWN_EGG", "spawn_egg"),
    /** Legacy id {@code 439} ({@code minecraft:spectral_arrow}) maps to {@link Material#SPECTRAL_ARROW}. */
    SPECTRAL_ARROW("439", 439, 0, "SPECTRAL_ARROW", "spectral_arrow"),
    /** Legacy id {@code 375} ({@code minecraft:spider_eye}) maps to {@link Material#SPIDER_EYE}. */
    SPIDER_EYE("375", 375, 0, "SPIDER_EYE", "spider_eye"),
    /** Legacy id {@code 438} ({@code minecraft:splash_potion}) maps to {@link Material#SPLASH_POTION}. */
    SPLASH_POTION("438", 438, 0, "SPLASH_POTION", "splash_potion"),
    /** Legacy id {@code 19} ({@code minecraft:sponge}) maps to {@link Material#SPONGE}. */
    SPONGE("19", 19, 0, "SPONGE", "sponge"),
    /** Legacy id {@code 444} ({@code minecraft:spruce_boat}) maps to {@link Material#SPRUCE_BOAT}. */
    SPRUCE_BOAT("444", 444, 0, "SPRUCE_BOAT", "spruce_boat"),
    /** Legacy id {@code 427} ({@code minecraft:spruce_door}) maps to {@link Material#SPRUCE_DOOR}. */
    SPRUCE_DOOR("427", 427, 0, "SPRUCE_DOOR", "spruce_door"),
    /** Legacy id {@code 193} ({@code minecraft:spruce_door}) maps to {@link Material#SPRUCE_DOOR}. */
    SPRUCE_DOOR_BLOCK("193", 193, 0, "SPRUCE_DOOR", "spruce_door"),
    /** Legacy id {@code 188} ({@code minecraft:spruce_fence}) maps to {@link Material#SPRUCE_FENCE}. */
    SPRUCE_FENCE("188", 188, 0, "SPRUCE_FENCE", "spruce_fence"),
    /** Legacy id {@code 183} ({@code minecraft:spruce_fence_gate}) maps to {@link Material#SPRUCE_FENCE_GATE}. */
    SPRUCE_FENCE_GATE("183", 183, 0, "SPRUCE_FENCE_GATE", "spruce_fence_gate"),
    /** Legacy id {@code 18:1} ({@code minecraft:leaves}) maps to {@link Material#SPRUCE_LEAVES}. */
    SPRUCE_LEAVES("18:1", 18, 1, "SPRUCE_LEAVES", "leaves"),
    /** Legacy id {@code 6:1} ({@code minecraft:sapling}) maps to {@link Material#SPRUCE_SAPLING}. */
    SPRUCE_SAPLING("6:1", 6, 1, "SPRUCE_SAPLING", "sapling"),
    /** Legacy id {@code 17:1} ({@code minecraft:log}) maps to {@link Material#SPRUCE_WOOD}. */
    SPRUCE_WOOD("17:1", 17, 1, "SPRUCE_WOOD", "log"),
    /** Legacy id {@code 5:1} ({@code minecraft:planks}) maps to {@link Material#SPRUCE_PLANKS}. */
    SPRUCE_WOOD_PLANK("5:1", 5, 1, "SPRUCE_PLANKS", "planks"),
    /** Legacy id {@code 126:1} ({@code minecraft:wooden_slab}) maps to {@link Material#SPRUCE_SLAB}. */
    SPRUCE_WOOD_SLAB("126:1", 126, 1, "SPRUCE_SLAB", "wooden_slab"),
    /** Legacy id {@code 134} ({@code minecraft:spruce_stairs}) maps to {@link Material#SPRUCE_STAIRS}. */
    SPRUCE_WOOD_STAIRS("134", 134, 0, "SPRUCE_STAIRS", "spruce_stairs"),
    /** Legacy id {@code 2263} ({@code minecraft:record_stal}) maps to {@link Material#MUSIC_DISC_STAL}. */
    STAL_DISC("2263", 2263, 0, "MUSIC_DISC_STAL", "record_stal"),
    /** Legacy id {@code 63} ({@code minecraft:standing_sign}) maps to {@link Material#OAK_WALL_SIGN}. */
    STANDING_SIGN_BLOCK("63", 63, 0, "OAK_WALL_SIGN", "standing_sign"),
    /** Legacy id {@code 364} ({@code minecraft:cooked_beef}) maps to {@link Material#COOKED_BEEF}. */
    STEAK("364", 364, 0, "COOKED_BEEF", "cooked_beef"),
    /** Legacy id {@code 280} ({@code minecraft:stick}) maps to {@link Material#STICK}. */
    STICK("280", 280, 0, "STICK", "stick"),
    /** Legacy id {@code 29} ({@code minecraft:sticky_piston}) maps to {@link Material#STICKY_PISTON}. */
    STICKY_PISTON("29", 29, 0, "STICKY_PISTON", "sticky_piston"),
    /** Legacy id {@code 11} ({@code minecraft:lava}) maps to {@link Material#LAVA}. */
    STILL_LAVA("11", 11, 0, "LAVA", "lava"),
    /** Legacy id {@code 9} ({@code minecraft:water}) maps to {@link Material#WATER}. */
    STILL_WATER("9", 9, 0, "WATER", "water"),
    /** Legacy id {@code 1} ({@code minecraft:stone}) maps to {@link Material#STONE}. */
    STONE("1", 1, 0, "STONE", "stone"),
    /** Legacy id {@code 275} ({@code minecraft:stone_axe}) maps to {@link Material#STONE_AXE}. */
    STONE_AXE("275", 275, 0, "STONE_AXE", "stone_axe"),
    /** Legacy id {@code 97:2} ({@code minecraft:monster_egg}) maps to {@link Material#INFESTED_STONE_BRICKS}. */
    STONE_BRICK_MONSTER_EGG("97:2", 97, 2, "INFESTED_STONE_BRICKS", "monster_egg"),
    /** Legacy id {@code 44:5} ({@code minecraft:stone_slab}) maps to {@link Material#STONE_BRICK_SLAB}. */
    STONE_BRICK_SLAB("44:5", 44, 5, "STONE_BRICK_SLAB", "stone_slab"),
    /** Legacy id {@code 109} ({@code minecraft:stone_brick_stairs}) maps to {@link Material#STONE_BRICK_STAIRS}. */
    STONE_BRICK_STAIRS("109", 109, 0, "STONE_BRICK_STAIRS", "stone_brick_stairs"),
    /** Legacy id {@code 98} ({@code minecraft:stonebrick}) maps to {@link Material#STONE_BRICKS}. */
    STONE_BRICKS("98", 98, 0, "STONE_BRICKS", "stonebrick"),
    /** Legacy id {@code 77} ({@code minecraft:stone_button}) maps to {@link Material#STONE_BUTTON}. */
    STONE_BUTTON("77", 77, 0, "STONE_BUTTON", "stone_button"),
    /** Legacy id {@code 291} ({@code minecraft:stone_hoe}) maps to {@link Material#STONE_HOE}. */
    STONE_HOE("291", 291, 0, "STONE_HOE", "stone_hoe"),
    /** Legacy id {@code 97} ({@code minecraft:monster_egg}) maps to {@link Material#INFESTED_STONE}. */
    STONE_MONSTER_EGG("97", 97, 0, "INFESTED_STONE", "monster_egg"),
    /** Legacy id {@code 274} ({@code minecraft:stone_pickaxe}) maps to {@link Material#STONE_PICKAXE}. */
    STONE_PICKAXE("274", 274, 0, "STONE_PICKAXE", "stone_pickaxe"),
    /** Legacy id {@code 70} ({@code minecraft:stone_pressure_plate}) maps to {@link Material#STONE_PRESSURE_PLATE}. */
    STONE_PRESSURE_PLATE("70", 70, 0, "STONE_PRESSURE_PLATE", "stone_pressure_plate"),
    /** Legacy id {@code 273} ({@code minecraft:stone_shovel}) maps to {@link Material#STONE_SHOVEL}. */
    STONE_SHOVEL("273", 273, 0, "STONE_SHOVEL", "stone_shovel"),
    /** Legacy id {@code 44} ({@code minecraft:stone_slab}) maps to {@link Material#STONE_SLAB}. */
    STONE_SLAB("44", 44, 0, "STONE_SLAB", "stone_slab"),
    /** Legacy id {@code 272} ({@code minecraft:stone_sword}) maps to {@link Material#STONE_SWORD}. */
    STONE_SWORD("272", 272, 0, "STONE_SWORD", "stone_sword"),
    /** Legacy id {@code 2264} ({@code minecraft:record_strad}) maps to {@link Material#MUSIC_DISC_STRAD}. */
    STRAD_DISC("2264", 2264, 0, "MUSIC_DISC_STRAD", "record_strad"),
    /** Legacy id {@code 287} ({@code minecraft:string}) maps to {@link Material#STRING}. */
    STRING("287", 287, 0, "STRING", "string"),
    /** Legacy id {@code 255} ({@code minecraft:structure_block}) maps to {@link Material#STRUCTURE_BLOCK}. */
    STRUCTURE_BLOCK("255", 255, 0, "STRUCTURE_BLOCK", "structure_block"),
    /** Legacy id {@code 217} ({@code minecraft:structure_void}) maps to {@link Material#STRUCTURE_VOID}. */
    STRUCTURE_VOID("217", 217, 0, "STRUCTURE_VOID", "structure_void"),
    /** Legacy id {@code 353} ({@code minecraft:sugar}) maps to {@link Material#SUGAR}. */
    SUGAR("353", 353, 0, "SUGAR", "sugar"),
    /** Legacy id {@code 338} ({@code minecraft:reeds}) maps to {@link Material#SUGAR_CANE}. */
    SUGAR_CANES("338", 338, 0, "SUGAR_CANE", "reeds"),
    /** Legacy id {@code 83} ({@code minecraft:reeds}) maps to {@link Material#SUGAR_CANE}. */
    SUGAR_CANES_BLOCK("83", 83, 0, "SUGAR_CANE", "reeds"),
    /** Legacy id {@code 175} ({@code minecraft:double_plant}) maps to {@link Material#SUNFLOWER}. */
    SUNFLOWER("175", 175, 0, "SUNFLOWER", "double_plant"),
    /** Legacy id {@code 440} ({@code minecraft:tipped_arrow}) maps to {@link Material#TIPPED_ARROW}. */
    TIPPED_ARROW("440", 440, 0, "TIPPED_ARROW", "tipped_arrow"),
    /** Legacy id {@code 46} ({@code minecraft:tnt}) maps to {@link Material#TNT}. */
    TNT("46", 46, 0, "TNT", "tnt"),
    /** Legacy id {@code 50} ({@code minecraft:torch}) maps to {@link Material#TORCH}. */
    TORCH("50", 50, 0, "TORCH", "torch"),
    /** Legacy id {@code 449} ({@code minecraft:totem_of_undying}) maps to {@link Material#TOTEM_OF_UNDYING}. */
    TOTEM_OF_UNDYING("449", 449, 0, "TOTEM_OF_UNDYING", "totem_of_undying"),
    /** Legacy id {@code 146} ({@code minecraft:trapped_chest}) maps to {@link Material#TRAPPED_CHEST}. */
    TRAPPED_CHEST("146", 146, 0, "TRAPPED_CHEST", "trapped_chest"),
    /** Legacy id {@code 132} ({@code minecraft:tripwire_hook}) maps to {@link Material#TRIPWIRE}. */
    TRIPWIRE("132", 132, 0, "TRIPWIRE", "tripwire_hook"),
    /** Legacy id {@code 131} ({@code minecraft:tripwire_hook}) maps to {@link Material#TRIPWIRE_HOOK}. */
    TRIPWIRE_HOOK("131", 131, 0, "TRIPWIRE_HOOK", "tripwire_hook"),
    /** Legacy id {@code 106} ({@code minecraft:vine}) maps to {@link Material#VINE}. */
    VINES("106", 106, 0, "VINE", "vine"),
    /** Legacy id {@code 2267} ({@code minecraft:record_wait}) maps to {@link Material#MUSIC_DISC_WAIT}. */
    WAIT_DISC("2267", 2267, 0, "MUSIC_DISC_WAIT", "record_wait"),
    /** Legacy id {@code 177} ({@code minecraft:wall_banner}) maps to {@link Material#BLACK_WALL_BANNER}. */
    WALL_MOUNTED_BANNER("177", 177, 0, "BLACK_WALL_BANNER", "wall_banner"),
    /** Legacy id {@code 68} ({@code minecraft:wall_sign}) maps to {@link Material#OAK_WALL_SIGN}. */
    WALL_MOUNTED_SIGN_BLOCK("68", 68, 0, "OAK_WALL_SIGN", "wall_sign"),
    /** Legacy id {@code 2265} ({@code minecraft:record_ward}) maps to {@link Material#MUSIC_DISC_WARD}. */
    WARD_DISC("2265", 2265, 0, "MUSIC_DISC_WARD", "record_ward"),
    /** Legacy id {@code 326} ({@code minecraft:water_bucket}) maps to {@link Material#WATER_BUCKET}. */
    WATER_BUCKET("326", 326, 0, "WATER_BUCKET", "water_bucket"),
    /** Legacy id {@code 148} ({@code minecraft:heavy_weighted_pressure_plate}) maps to {@link Material#HEAVY_WEIGHTED_PRESSURE_PLATE}. */
    WEIGHTED_PRESSURE_PLATE_HEAVY("148", 148, 0, "HEAVY_WEIGHTED_PRESSURE_PLATE", "heavy_weighted_pressure_plate"),
    /** Legacy id {@code 147} ({@code minecraft:light_weighted_pressure_plate}) maps to {@link Material#LIGHT_WEIGHTED_PRESSURE_PLATE}. */
    WEIGHTED_PRESSURE_PLATE_LIGHT("147", 147, 0, "LIGHT_WEIGHTED_PRESSURE_PLATE", "light_weighted_pressure_plate"),
    /** Legacy id {@code 19:1} ({@code minecraft:sponge}) maps to {@link Material#WET_SPONGE}. */
    WET_SPONGE("19:1", 19, 1, "WET_SPONGE", "sponge"),
    /** Legacy id {@code 296} ({@code minecraft:wheat}) maps to {@link Material#WHEAT}. */
    WHEAT("296", 296, 0, "WHEAT", "wheat"),
    /** Legacy id {@code 59} ({@code minecraft:wheat}) maps to {@link Material#WHEAT}. */
    WHEAT_CROPS("59", 59, 0, "WHEAT", "wheat"),
    /** Legacy id {@code 295} ({@code minecraft:wheat_seeds}) maps to {@link Material#WHEAT_SEEDS}. */
    WHEAT_SEEDS("295", 295, 0, "WHEAT_SEEDS", "wheat_seeds"),
    /** Legacy id {@code 171} ({@code minecraft:carpet}) maps to {@link Material#WHITE_CARPET}. */
    WHITE_CARPET("171", 171, 0, "WHITE_CARPET", "carpet"),
    /** Legacy id {@code 251} ({@code minecraft:concrete}) maps to {@link Material#WHITE_CONCRETE}. */
    WHITE_CONCRETE("251", 251, 0, "WHITE_CONCRETE", "concrete"),
    /** Legacy id {@code 252} ({@code minecraft:concrete_powder}) maps to {@link Material#WHITE_CONCRETE_POWDER}. */
    WHITE_CONCRETE_POWDER("252", 252, 0, "WHITE_CONCRETE_POWDER", "concrete_powder"),
    /** Legacy id {@code 235} ({@code minecraft:white_glazed_terracotta}) maps to {@link Material#WHITE_GLAZED_TERRACOTTA}. */
    WHITE_GLAZED_TERRACOTTA("235", 235, 0, "WHITE_GLAZED_TERRACOTTA", "white_glazed_terracotta"),
    /** Legacy id {@code 159} ({@code minecraft:stained_hardened_clay}) maps to {@link Material#WHITE_TERRACOTTA}. */
    WHITE_HARDENED_CLAY("159", 159, 0, "WHITE_TERRACOTTA", "stained_hardened_clay"),
    /** Legacy id {@code 219} ({@code minecraft:white_shulker_box}) maps to {@link Material#WHITE_SHULKER_BOX}. */
    WHITE_SHULKER_BOX("219", 219, 0, "WHITE_SHULKER_BOX", "white_shulker_box"),
    /** Legacy id {@code 95} ({@code minecraft:stained_glass}) maps to {@link Material#WHITE_STAINED_GLASS}. */
    WHITE_STAINED_GLASS("95", 95, 0, "WHITE_STAINED_GLASS", "stained_glass"),
    /** Legacy id {@code 160} ({@code minecraft:stained_glass_pane}) maps to {@link Material#WHITE_STAINED_GLASS_PANE}. */
    WHITE_STAINED_GLASS_PANE("160", 160, 0, "WHITE_STAINED_GLASS_PANE", "stained_glass_pane"),
    /** Legacy id {@code 38:6} ({@code minecraft:red_flower}) maps to {@link Material#WHITE_TULIP}. */
    WHITE_TULIP("38:6", 38, 6, "WHITE_TULIP", "red_flower"),
    /** Legacy id {@code 35} ({@code minecraft:wool}) maps to {@link Material#WHITE_WOOL}. */
    WHITE_WOOL("35", 35, 0, "WHITE_WOOL", "wool"),
    /** Legacy id {@code 271} ({@code minecraft:wooden_axe}) maps to {@link Material#WOODEN_AXE}. */
    WOODEN_AXE("271", 271, 0, "WOODEN_AXE", "wooden_axe"),
    /** Legacy id {@code 143} ({@code minecraft:wooden_button}) maps to {@link Material#OAK_BUTTON}. */
    WOODEN_BUTTON("143", 143, 0, "OAK_BUTTON", "wooden_button"),
    /** Legacy id {@code 290} ({@code minecraft:wooden_hoe}) maps to {@link Material#WOODEN_HOE}. */
    WOODEN_HOE("290", 290, 0, "WOODEN_HOE", "wooden_hoe"),
    /** Legacy id {@code 270} ({@code minecraft:wooden_pickaxe}) maps to {@link Material#WOODEN_PICKAXE}. */
    WOODEN_PICKAXE("270", 270, 0, "WOODEN_PICKAXE", "wooden_pickaxe"),
    /** Legacy id {@code 72} ({@code minecraft:wooden_pressure_plate}) maps to {@link Material#OAK_PRESSURE_PLATE}. */
    WOODEN_PRESSURE_PLATE("72", 72, 0, "OAK_PRESSURE_PLATE", "wooden_pressure_plate"),
    /** Legacy id {@code 269} ({@code minecraft:wooden_shovel}) maps to {@link Material#WOODEN_SHOVEL}. */
    WOODEN_SHOVEL("269", 269, 0, "WOODEN_SHOVEL", "wooden_shovel"),
    /** Legacy id {@code 44:2} ({@code minecraft:stone_slab}) maps to {@link Material#OAK_SLAB}. */
    WOODEN_SLAB("44:2", 44, 2, "OAK_SLAB", "stone_slab"),
    /** Legacy id {@code 268} ({@code minecraft:wooden_sword}) maps to {@link Material#WOODEN_SWORD}. */
    WOODEN_SWORD("268", 268, 0, "WOODEN_SWORD", "wooden_sword"),
    /** Legacy id {@code 96} ({@code minecraft:trapdoor}) maps to {@link Material#OAK_TRAPDOOR}. */
    WOODEN_TRAPDOOR("96", 96, 0, "OAK_TRAPDOOR", "trapdoor"),
    /** Legacy id {@code 387} ({@code minecraft:written_book}) maps to {@link Material#WRITTEN_BOOK}. */
    WRITTEN_BOOK("387", 387, 0, "WRITTEN_BOOK", "written_book"),
    /** Legacy id {@code 171:4} ({@code minecraft:carpet}) maps to {@link Material#YELLOW_CARPET}. */
    YELLOW_CARPET("171:4", 171, 4, "YELLOW_CARPET", "carpet"),
    /** Legacy id {@code 251:4} ({@code minecraft:concrete}) maps to {@link Material#YELLOW_CONCRETE}. */
    YELLOW_CONCRETE("251:4", 251, 4, "YELLOW_CONCRETE", "concrete"),
    /** Legacy id {@code 252:4} ({@code minecraft:concrete_powder}) maps to {@link Material#YELLOW_CONCRETE_POWDER}. */
    YELLOW_CONCRETE_POWDER("252:4", 252, 4, "YELLOW_CONCRETE_POWDER", "concrete_powder"),
    /** Legacy id {@code 239} ({@code minecraft:yellow_glazed_terracotta}) maps to {@link Material#YELLOW_GLAZED_TERRACOTTA}. */
    YELLOW_GLAZED_TERRACOTTA("239", 239, 0, "YELLOW_GLAZED_TERRACOTTA", "yellow_glazed_terracotta"),
    /** Legacy id {@code 159:4} ({@code minecraft:stained_hardened_clay}) maps to {@link Material#YELLOW_TERRACOTTA}. */
    YELLOW_HARDENED_CLAY("159:4", 159, 4, "YELLOW_TERRACOTTA", "stained_hardened_clay"),
    /** Legacy id {@code 223} ({@code minecraft:yellow_shulker_box}) maps to {@link Material#YELLOW_SHULKER_BOX}. */
    YELLOW_SHULKER_BOX("223", 223, 0, "YELLOW_SHULKER_BOX", "yellow_shulker_box"),
    /** Legacy id {@code 95:4} ({@code minecraft:stained_glass}) maps to {@link Material#YELLOW_STAINED_GLASS}. */
    YELLOW_STAINED_GLASS("95:4", 95, 4, "YELLOW_STAINED_GLASS", "stained_glass"),
    /** Legacy id {@code 160:4} ({@code minecraft:stained_glass_pane}) maps to {@link Material#YELLOW_STAINED_GLASS_PANE}. */
    YELLOW_STAINED_GLASS_PANE("160:4", 160, 4, "YELLOW_STAINED_GLASS_PANE", "stained_glass_pane"),
    /** Legacy id {@code 35:4} ({@code minecraft:wool}) maps to {@link Material#YELLOW_WOOL}. */
    YELLOW_WOOL("35:4", 35, 4, "YELLOW_WOOL", "wool"),
    ;

    @Getter
    String totalID, minecraftName, stringMaterial;

    @Getter
    int ID, metadata;

    @Getter
    Material material;


    LegacyItemAPI(String totalID, int ID, int metadata, String newBukkitName, String minecraftName) {
        this.totalID = totalID;
        this.ID = ID;
        this.metadata = metadata;
        this.minecraftName = minecraftName;
        this.material = Material.valueOf(newBukkitName);
        this.stringMaterial = newBukkitName;
    }

    /**
     * Returns the modern {@link Material} this legacy entry maps to.
     *
     * @return the mapped material
     */
    public Material toMaterial() {
        return material;
    }

    /**
     * Resolves a legacy ID to a {@link Material}, accepting either a plain numeric ID or {@code id:data}.
     *
     * @param id the legacy ID, optionally in {@code id:data} form
     * @return the matching material, or null if none matches
     */
    public static Material getMaterial(String id) {
        return getRawMaterial(id);
    }

    /**
     * Resolves a legacy numeric ID to a {@link Material}.
     *
     * @param id the legacy numeric ID
     * @return the matching material, or null if none matches
     */
    public static Material getMaterial(int id) {
        return getRawMaterial(String.valueOf(id));
    }

    /**
     * Resolves a legacy ID and data value to a {@link Material}.
     *
     * @param id       the legacy numeric ID
     * @param metadata the data value
     * @return the matching material, or null if none matches
     */
    public static Material getMaterial(String id, String metadata) {
        return getMaterial(id + ":" + metadata);
    }

    /**
     * Resolves a legacy ID and data value to a {@link Material}.
     *
     * @param id       the legacy numeric ID
     * @param metadata the data value
     * @return the matching material, or null if none matches
     */
    public static Material getMaterial(int id, int metadata) {
        return getMaterial(id + ":" + metadata);
    }

    private static Material getRawMaterial(String id) {
        if (id.contains(":")) {
            for (LegacyItemAPI oldID : LegacyItemAPI.values()) {
                if (oldID.getTotalID().equals(id))
                    return oldID.getMaterial();
            }
        } else {
            for (LegacyItemAPI oldID : LegacyItemAPI.values()) {
                if (oldID.getID() == Integer.parseInt(id))
                    return oldID.getMaterial();
            }
        }
        return null;
    }
}