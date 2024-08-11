package net.infernal_coding.villager_realism.util;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.entity.villager.VillagerType;
import net.minecraft.item.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.storage.MapDecoration;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class NewTrades {

    static {
        Map<VillagerProfession, Int2ObjectMap<VillagerTrades.ITrade[]>> map = new HashMap<>(VillagerTrades.VILLAGER_DEFAULT_TRADES);
        map.putAll(NewTrades.EXPERIMENTAL_TRADES);
        VillagerTrades.VILLAGER_DEFAULT_TRADES = map;
    }
    public static final Map<VillagerProfession, Int2ObjectMap<VillagerTrades.ITrade[]>> EXPERIMENTAL_TRADES = ImmutableMap.of(
            VillagerProfession.LIBRARIAN, toIntMap(ImmutableMap.<Integer, VillagerTrades.ITrade[]>builder()
                    .put(1, new VillagerTrades.ITrade[]{
                            new EmeraldForItems(Items.PAPER, 24, 16, 2), commonBooks(1),
                            new ItemsForEmeralds(Blocks.BOOKSHELF, 9, 1, 12, 1)})
                    .put(2, new VillagerTrades.ITrade[]{
                            new EmeraldForItems(Items.BOOK, 4, 12, 10), commonBooks(5),
                            new ItemsForEmeralds(Items.LANTERN, 1, 1, 5)})
                    .put(3, new VillagerTrades.ITrade[]{
                            new EmeraldForItems(Items.INK_SAC, 5, 12, 20), commonBooks(10),
                            new ItemsForEmeralds(Items.GLASS, 1, 4, 10)})
                    .put(4, new VillagerTrades.ITrade[]{
                            new EmeraldForItems(Items.WRITABLE_BOOK, 2, 12, 30),
                            new ItemsForEmeralds(Items.CLOCK, 5, 1, 15),
                            new ItemsForEmeralds(Items.COMPASS, 4, 1, 15)})
                    .put(5, new VillagerTrades.ITrade[]{specialBooks(),
                            new ItemsForEmeralds(Items.NAME_TAG, 20, 1, 30)}).build()),
            VillagerProfession.ARMORER, toIntMap(ImmutableMap.<Integer, VillagerTrades.ITrade[]>builder().put(1,
                            new VillagerTrades.ITrade[]{
                                    new EmeraldForItems(Items.COAL, 15, 12, 2),
                                    new EmeraldForItems(Items.IRON_INGOT, 5, 12, 2)})
                    .put(2, new VillagerTrades.ITrade[]{
                            TypeSpecificTrade.oneTradeInBiomes(
                                    new ItemsForEmeralds(Items.IRON_BOOTS, 4, 1, 12, 5, 0.05F), VillagerType.DESERT, VillagerType.PLAINS, VillagerType.SAVANNA, VillagerType.SNOW, VillagerType.TAIGA),
                            TypeSpecificTrade.oneTradeInBiomes(
                                    new ItemsForEmeralds(Items.CHAINMAIL_BOOTS, 4, 1, 12, 5, 0.05F), VillagerType.JUNGLE, VillagerType.SWAMP),
                            TypeSpecificTrade.oneTradeInBiomes(
                                    new ItemsForEmeralds(Items.IRON_HELMET, 5, 1, 12, 5, 0.05F), VillagerType.DESERT, VillagerType.PLAINS, VillagerType.SAVANNA, VillagerType.SNOW, VillagerType.TAIGA),
                            TypeSpecificTrade.oneTradeInBiomes(
                                    new ItemsForEmeralds(Items.CHAINMAIL_HELMET, 5, 1, 12, 5, 0.05F), VillagerType.JUNGLE, VillagerType.SWAMP),
                            TypeSpecificTrade.oneTradeInBiomes(
                                    new ItemsForEmeralds(Items.IRON_LEGGINGS, 7, 1, 12, 5, 0.05F), VillagerType.DESERT, VillagerType.PLAINS, VillagerType.SAVANNA, VillagerType.SNOW, VillagerType.TAIGA),
                            TypeSpecificTrade.oneTradeInBiomes(
                                    new ItemsForEmeralds(Items.CHAINMAIL_LEGGINGS, 7, 1, 12, 5, 0.05F), VillagerType.JUNGLE, VillagerType.SWAMP),
                            TypeSpecificTrade.oneTradeInBiomes(
                                    new ItemsForEmeralds(Items.IRON_CHESTPLATE, 9, 1, 12, 5, 0.05F), VillagerType.DESERT, VillagerType.PLAINS, VillagerType.SAVANNA, VillagerType.SNOW, VillagerType.TAIGA),
                            TypeSpecificTrade.oneTradeInBiomes(
                                    new ItemsForEmeralds(Items.CHAINMAIL_CHESTPLATE, 9, 1, 12, 5, 0.05F), VillagerType.JUNGLE, VillagerType.SWAMP)})
                    .put(3, new VillagerTrades.ITrade[]{
                            new EmeraldForItems(Items.LAVA_BUCKET, 1, 12, 20),
                            new ItemsForEmeralds(Items.SHIELD, 5, 1, 12, 10, 0.05F),
                            new ItemsForEmeralds(Items.BELL, 36, 1, 12, 10, 0.2F)})
                    .put(4, new VillagerTrades.ITrade[]{
                            TypeSpecificTrade.oneTradeInBiomes(
                                    new ItemsForEmeralds(enchant(Items.IRON_BOOTS, Enchantments.THORNS, 1), 8, 1, 3, 15, 0.05F), VillagerType.DESERT),
                            TypeSpecificTrade.oneTradeInBiomes(
                                    new ItemsForEmeralds(enchant(Items.IRON_HELMET, Enchantments.THORNS, 1), 9, 1, 3, 15, 0.05F), VillagerType.DESERT),
                            TypeSpecificTrade.oneTradeInBiomes(
                                    new ItemsForEmeralds(enchant(Items.IRON_LEGGINGS, Enchantments.THORNS, 1), 11, 1, 3, 15, 0.05F), VillagerType.DESERT),
                            TypeSpecificTrade.oneTradeInBiomes(
                                    new ItemsForEmeralds(enchant(Items.IRON_CHESTPLATE, Enchantments.THORNS, 1), 13, 1, 3, 15, 0.05F), VillagerType.DESERT),
                            TypeSpecificTrade.oneTradeInBiomes(
                                    new ItemsForEmeralds(enchant(Items.IRON_BOOTS, Enchantments.PROTECTION, 1), 8, 1, 3, 15, 0.05F), VillagerType.PLAINS),
                            TypeSpecificTrade.oneTradeInBiomes(
                                    new ItemsForEmeralds(enchant(Items.IRON_HELMET, Enchantments.PROTECTION, 1), 9, 1, 3, 15, 0.05F), VillagerType.PLAINS),
                            TypeSpecificTrade.oneTradeInBiomes(
                                    new ItemsForEmeralds(enchant(Items.IRON_LEGGINGS, Enchantments.PROTECTION, 1), 11, 1, 3, 15, 0.05F), VillagerType.PLAINS),
                            TypeSpecificTrade.oneTradeInBiomes(
                                    new ItemsForEmeralds(enchant(Items.IRON_CHESTPLATE, Enchantments.PROTECTION, 1), 13, 1, 3, 15, 0.05F), VillagerType.PLAINS),
                            TypeSpecificTrade.oneTradeInBiomes(
                                    new ItemsForEmeralds(enchant(Items.IRON_BOOTS, Enchantments.BINDING_CURSE, 1), 2, 1, 3, 15, 0.05F), VillagerType.SAVANNA),
                            TypeSpecificTrade.oneTradeInBiomes(
                                    new ItemsForEmeralds(enchant(Items.IRON_HELMET, Enchantments.BINDING_CURSE, 1), 3, 1, 3, 15, 0.05F), VillagerType.SAVANNA),
                            TypeSpecificTrade.oneTradeInBiomes(
                                    new ItemsForEmeralds(enchant(Items.IRON_LEGGINGS, Enchantments.BINDING_CURSE, 1), 5, 1, 3, 15, 0.05F), VillagerType.SAVANNA),
                            TypeSpecificTrade.oneTradeInBiomes(
                                    new ItemsForEmeralds(enchant(Items.IRON_CHESTPLATE, Enchantments.BINDING_CURSE, 1), 7, 1, 3, 15, 0.05F), VillagerType.SAVANNA),
                            TypeSpecificTrade.oneTradeInBiomes(
                                    new ItemsForEmeralds(enchant(Items.IRON_BOOTS, Enchantments.FROST_WALKER, 1), 8, 1, 3, 15, 0.05F), VillagerType.SNOW),
                            TypeSpecificTrade.oneTradeInBiomes(
                                    new ItemsForEmeralds(enchant(Items.IRON_HELMET, Enchantments.AQUA_AFFINITY, 1), 9, 1, 3, 15, 0.05F), VillagerType.SNOW),
                            TypeSpecificTrade.oneTradeInBiomes(
                                    new ItemsForEmeralds(enchant(Items.CHAINMAIL_BOOTS, Enchantments.UNBREAKING, 1), 8, 1, 3, 15, 0.05F), VillagerType.JUNGLE),
                            TypeSpecificTrade.oneTradeInBiomes(
                                    new ItemsForEmeralds(enchant(Items.CHAINMAIL_HELMET, Enchantments.UNBREAKING, 1), 9, 1, 3, 15, 0.05F), VillagerType.JUNGLE),
                            TypeSpecificTrade.oneTradeInBiomes(
                                    new ItemsForEmeralds(enchant(Items.CHAINMAIL_LEGGINGS, Enchantments.UNBREAKING, 1), 11, 1, 3, 15, 0.05F), VillagerType.JUNGLE),
                            TypeSpecificTrade.oneTradeInBiomes(
                                    new ItemsForEmeralds(enchant(Items.CHAINMAIL_CHESTPLATE, Enchantments.UNBREAKING, 1), 13, 1, 3, 15, 0.05F), VillagerType.JUNGLE),
                            TypeSpecificTrade.oneTradeInBiomes(
                                    new ItemsForEmeralds(enchant(Items.CHAINMAIL_BOOTS, Enchantments.MENDING, 1), 8, 1, 3, 15, 0.05F), VillagerType.SWAMP),
                            TypeSpecificTrade.oneTradeInBiomes(
                                    new ItemsForEmeralds(enchant(Items.CHAINMAIL_HELMET, Enchantments.MENDING, 1), 9, 1, 3, 15, 0.05F), VillagerType.SWAMP),
                            TypeSpecificTrade.oneTradeInBiomes(
                                    new ItemsForEmeralds(enchant(Items.CHAINMAIL_LEGGINGS, Enchantments.MENDING, 1), 11, 1, 3, 15, 0.05F), VillagerType.SWAMP),
                            TypeSpecificTrade.oneTradeInBiomes(
                                    new ItemsForEmeralds(enchant(Items.CHAINMAIL_CHESTPLATE, Enchantments.MENDING, 1), 13, 1, 3, 15, 0.05F), VillagerType.SWAMP),
                            TypeSpecificTrade.oneTradeInBiomes(
                                    new ItemsAndEmeraldsToItems(Items.DIAMOND_BOOTS, 1, 4, Items.DIAMOND_LEGGINGS, 1, 3, 15, 0.05F), VillagerType.TAIGA),
                            TypeSpecificTrade.oneTradeInBiomes(
                                    new ItemsAndEmeraldsToItems(Items.DIAMOND_LEGGINGS, 1, 4, Items.DIAMOND_CHESTPLATE, 1, 3, 15, 0.05F), VillagerType.TAIGA),
                            TypeSpecificTrade.oneTradeInBiomes(
                                    new ItemsAndEmeraldsToItems(Items.DIAMOND_HELMET, 1, 4, Items.DIAMOND_BOOTS, 1, 3, 15, 0.05F), VillagerType.TAIGA),
                            TypeSpecificTrade.oneTradeInBiomes(
                                    new ItemsAndEmeraldsToItems(Items.DIAMOND_CHESTPLATE, 1, 2, Items.DIAMOND_HELMET, 1, 3, 15, 0.05F), VillagerType.TAIGA)})
                    .put(5, new VillagerTrades.ITrade[]{
                            TypeSpecificTrade.oneTradeInBiomes(
                                    new ItemsAndEmeraldsToItems(Items.DIAMOND, 4, 16, enchant(Items.DIAMOND_CHESTPLATE, Enchantments.THORNS, 1), 1, 3, 30, 0.05F), VillagerType.DESERT),
                            TypeSpecificTrade.oneTradeInBiomes(
                                    new ItemsAndEmeraldsToItems(Items.DIAMOND, 3, 16, enchant(Items.DIAMOND_LEGGINGS, Enchantments.THORNS, 1), 1, 3, 30, 0.05F), VillagerType.DESERT),
                            TypeSpecificTrade.oneTradeInBiomes(
                                    new ItemsAndEmeraldsToItems(Items.DIAMOND, 3, 16, enchant(Items.DIAMOND_LEGGINGS, Enchantments.PROTECTION, 1), 1, 3, 30, 0.05F), VillagerType.PLAINS),
                            TypeSpecificTrade.oneTradeInBiomes(
                                    new ItemsAndEmeraldsToItems(Items.DIAMOND, 2, 12, enchant(Items.DIAMOND_BOOTS, Enchantments.PROTECTION, 1), 1, 3, 30, 0.05F), VillagerType.PLAINS),
                            TypeSpecificTrade.oneTradeInBiomes(
                                    new ItemsAndEmeraldsToItems(Items.DIAMOND, 2, 6, enchant(Items.DIAMOND_HELMET, Enchantments.BINDING_CURSE, 1), 1, 3, 30, 0.05F), VillagerType.SAVANNA),
                            TypeSpecificTrade.oneTradeInBiomes(
                                    new ItemsAndEmeraldsToItems(Items.DIAMOND, 3, 8, enchant(Items.DIAMOND_CHESTPLATE, Enchantments.BINDING_CURSE, 1), 1, 3, 30, 0.05F), VillagerType.SAVANNA),
                            TypeSpecificTrade.oneTradeInBiomes(
                                    new ItemsAndEmeraldsToItems(Items.DIAMOND, 2, 12, enchant(Items.DIAMOND_BOOTS, Enchantments.FROST_WALKER, 1), 1, 3, 30, 0.05F), VillagerType.SNOW),
                            TypeSpecificTrade.oneTradeInBiomes(
                                    new ItemsAndEmeraldsToItems(Items.DIAMOND, 3, 12, enchant(Items.DIAMOND_HELMET, Enchantments.AQUA_AFFINITY, 1), 1, 3, 30, 0.05F), VillagerType.SNOW),
                            TypeSpecificTrade.oneTradeInBiomes(
                                    new ItemsForEmeralds(enchant(Items.CHAINMAIL_HELMET, Enchantments.PROJECTILE_PROTECTION, 1), 9, 1, 3, 30, 0.05F), VillagerType.JUNGLE),
                            TypeSpecificTrade.oneTradeInBiomes(
                                    new ItemsForEmeralds(enchant(Items.CHAINMAIL_BOOTS, Enchantments.FEATHER_FALLING, 1), 8, 1, 3, 30, 0.05F), VillagerType.JUNGLE),
                            TypeSpecificTrade.oneTradeInBiomes(
                                    new ItemsForEmeralds(enchant(Items.CHAINMAIL_HELMET, Enchantments.RESPIRATION, 1), 9, 1, 3, 30, 0.05F), VillagerType.SWAMP),
                            TypeSpecificTrade.oneTradeInBiomes(
                                    new ItemsForEmeralds(enchant(Items.CHAINMAIL_BOOTS, Enchantments.DEPTH_STRIDER, 1), 8, 1, 3, 30, 0.05F), VillagerType.SWAMP),
                            TypeSpecificTrade.oneTradeInBiomes(
                                    new ItemsAndEmeraldsToItems(Items.DIAMOND, 4, 18, enchant(Items.DIAMOND_CHESTPLATE, Enchantments.BLAST_PROTECTION, 1), 1, 3, 30, 0.05F), VillagerType.TAIGA),
                            TypeSpecificTrade.oneTradeInBiomes(
                                    new ItemsAndEmeraldsToItems(Items.DIAMOND, 3, 18, enchant(Items.DIAMOND_LEGGINGS, Enchantments.BLAST_PROTECTION, 1), 1, 3, 30, 0.05F), VillagerType.TAIGA),
                            TypeSpecificTrade.oneTradeInBiomes(
                                    new EmeraldForItems(Items.DIAMOND_BLOCK, 1, 12, 30, 42), VillagerType.TAIGA),
                            TypeSpecificTrade.oneTradeInBiomes(
                                    new EmeraldForItems(Items.IRON_BLOCK, 1, 12, 30, 4), VillagerType.DESERT, VillagerType.JUNGLE, VillagerType.PLAINS, VillagerType.SAVANNA, VillagerType.SNOW, VillagerType.SWAMP)}).build()),
            VillagerProfession.CARTOGRAPHER, toIntMap(ImmutableMap.of(1, new VillagerTrades.ITrade[]{
                            new EmeraldForItems(Items.PAPER, 24, 16, 2),
                            new ItemsForEmeralds(Items.MAP, 7, 1, 1)}, 2,
                    new VillagerTrades.ITrade[]{
                            new EmeraldForItems(Items.COMPASS, 1, 12, 20),
                            new VillagerTrades.EmeraldForMapTrade(13, Structure.MONUMENT, MapDecoration.Type.MONUMENT, 12, 10)}, 4,
                    new VillagerTrades.ITrade[]{new ItemsForEmeralds(Items.ITEM_FRAME, 7, 1, 15),
                            new ItemsForEmeralds(Items.WHITE_BANNER, 3, 1, 15),
                            new ItemsForEmeralds(Items.BLUE_BANNER, 3, 1, 15),
                            new ItemsForEmeralds(Items.LIGHT_BLUE_BANNER, 3, 1, 15),
                            new ItemsForEmeralds(Items.RED_BANNER, 3, 1, 15),
                            new ItemsForEmeralds(Items.PINK_BANNER, 3, 1, 15),
                            new ItemsForEmeralds(Items.GREEN_BANNER, 3, 1, 15),
                            new ItemsForEmeralds(Items.LIME_BANNER, 3, 1, 15),
                            new ItemsForEmeralds(Items.GRAY_BANNER, 3, 1, 15),
                            new ItemsForEmeralds(Items.BLACK_BANNER, 3, 1, 15),
                            new ItemsForEmeralds(Items.PURPLE_BANNER, 3, 1, 15),
                            new ItemsForEmeralds(Items.MAGENTA_BANNER, 3, 1, 15),
                            new ItemsForEmeralds(Items.CYAN_BANNER, 3, 1, 15),
                            new ItemsForEmeralds(Items.BROWN_BANNER, 3, 1, 15),
                            new ItemsForEmeralds(Items.YELLOW_BANNER, 3, 1, 15),
                            new ItemsForEmeralds(Items.ORANGE_BANNER, 3, 1, 15),
                            new ItemsForEmeralds(Items.LIGHT_GRAY_BANNER, 3, 1, 15)}, 5,
                    new VillagerTrades.ITrade[]{
                            new ItemsForEmeralds(Items.GLOBE_BANNER_PATTERN, 8, 1, 30),
                            new VillagerTrades.EmeraldForMapTrade(14, Structure.WOODLAND_MANSION, MapDecoration.Type.MANSION, 1, 30)})));
    

    private static VillagerTrades.ITrade commonBooks(int p_301375_) {
        return new TypeSpecificTrade(ImmutableMap.<VillagerType, VillagerTrades.ITrade>builder().put(VillagerType.DESERT, new EnchantBookForEmeraldsTrade(p_301375_, Enchantments.FIRE_PROTECTION, Enchantments.THORNS, Enchantments.INFINITY)).put(VillagerType.JUNGLE, new EnchantBookForEmeraldsTrade(p_301375_, Enchantments.FEATHER_FALLING, Enchantments.PROJECTILE_PROTECTION, Enchantments.POWER)).put(VillagerType.PLAINS, new EnchantBookForEmeraldsTrade(p_301375_, Enchantments.PUNCH, Enchantments.SMITE, Enchantments.BANE_OF_ARTHROPODS)).put(VillagerType.SAVANNA, new EnchantBookForEmeraldsTrade(p_301375_, Enchantments.KNOCKBACK, Enchantments.BINDING_CURSE, Enchantments.SWEEPING)).put(VillagerType.SNOW, new EnchantBookForEmeraldsTrade(p_301375_, Enchantments.AQUA_AFFINITY, Enchantments.LOOTING, Enchantments.FROST_WALKER)).put(VillagerType.SWAMP, new EnchantBookForEmeraldsTrade(p_301375_, Enchantments.DEPTH_STRIDER, Enchantments.RESPIRATION, Enchantments.VANISHING_CURSE)).put(VillagerType.TAIGA, new EnchantBookForEmeraldsTrade(p_301375_, Enchantments.BLAST_PROTECTION, Enchantments.FIRE_ASPECT, Enchantments.FLAME)).build());
    }

    private static VillagerTrades.ITrade specialBooks() {
        return new TypeSpecificTrade(ImmutableMap.<VillagerType, VillagerTrades.ITrade>builder().put(VillagerType.DESERT, new EnchantBookForEmeraldsTrade(30, 3, 3, Enchantments.EFFICIENCY)).put(VillagerType.JUNGLE, new EnchantBookForEmeraldsTrade(30, 2, 2, Enchantments.UNBREAKING)).put(VillagerType.PLAINS, new EnchantBookForEmeraldsTrade(30, 3, 3, Enchantments.PROTECTION)).put(VillagerType.SAVANNA, new EnchantBookForEmeraldsTrade(30, 3, 3, Enchantments.SHARPNESS)).put(VillagerType.SNOW, new EnchantBookForEmeraldsTrade(30, Enchantments.SILK_TOUCH)).put(VillagerType.SWAMP, new EnchantBookForEmeraldsTrade(30, Enchantments.MENDING)).put(VillagerType.TAIGA, new EnchantBookForEmeraldsTrade(30, 2, 2, Enchantments.FORTUNE)).build());
    }

    private static Int2ObjectMap<VillagerTrades.ITrade[]> toIntMap(ImmutableMap<Integer, VillagerTrades.ITrade[]> p_35631_) {
        return new Int2ObjectOpenHashMap<>(p_35631_);
    }
    private static ItemStack enchant(Item item, Enchantment enchantment, int level) {
        ItemStack itemstack = new ItemStack(item);
        itemstack.addEnchantment(enchantment, level);
        return itemstack;
    }

    static class ItemsAndEmeraldsToItems implements VillagerTrades.ITrade {
        private final ItemStack fromItem;
        private final int emeraldCost;
        private final ItemStack toItem;
        private final int maxUses;
        private final int villagerXp;
        private final float priceMultiplier;

        public ItemsAndEmeraldsToItems(Item fromItem, int count, int emeraldCost, Item toItem, int newCount, int maxUses, int villagerXp, float priceMultiplier) {
            this(fromItem, count, emeraldCost, new ItemStack(toItem), newCount, maxUses, villagerXp, priceMultiplier);
        }

        public ItemsAndEmeraldsToItems(Item fromItem, int count, int emeraldCost, ItemStack toItem, int newCount, int maxUses, int villagerXp, float priceMultiplier) {
            this.fromItem = new ItemStack(fromItem, count);
            this.emeraldCost = emeraldCost;
            this.toItem = toItem.copy();
            toItem.setCount(newCount);
            this.maxUses = maxUses;
            this.villagerXp = villagerXp;
            this.priceMultiplier = priceMultiplier;
        }

        @Nullable
        public MerchantOffer getOffer(Entity entity, Random random) {
            return new MerchantOffer(new ItemStack(Items.EMERALD, this.emeraldCost), this.fromItem.copy(), this.toItem.copy(), this.maxUses, this.villagerXp, this.priceMultiplier);
        }
    }

    static class ItemsForEmeralds implements VillagerTrades.ITrade {
        private final ItemStack itemStack;
        private final int emeraldCost;
        private final int maxUses;
        private final int villagerXp;
        private final float priceMultiplier;

        public ItemsForEmeralds(Block block, int emeraldCost, int count, int maxUses, int villagerXp) {
            this(new ItemStack(block), emeraldCost, count, maxUses, villagerXp);
        }

        public ItemsForEmeralds(Item item, int emeraldCost, int count, int villagerXp) {
            this(new ItemStack(item), emeraldCost, count, 12, villagerXp);
        }

        public ItemsForEmeralds(Item item, int emeraldCost, int count, int maxUses, int villagerXp) {
            this(new ItemStack(item), emeraldCost, count, maxUses, villagerXp);
        }

        public ItemsForEmeralds(ItemStack stack, int emeraldCost, int count, int maxUses, int villagerXp) {
            this(stack, emeraldCost, count, maxUses, villagerXp, 0.05F);
        }

        public ItemsForEmeralds(Item item, int emeraldCost, int count, int maxUses, int villagerXp, float priceMultiplier) {
            this(new ItemStack(item), emeraldCost, count, maxUses, villagerXp, priceMultiplier);
        }

        public ItemsForEmeralds(ItemStack itemStack, int emeraldCost, int count, int maxUses, int villagerXp, float priceMultiplier) {
            this.itemStack = itemStack;
            this.emeraldCost = emeraldCost;
            this.itemStack.setCount(count);
            this.maxUses = maxUses;
            this.villagerXp = villagerXp;
            this.priceMultiplier = priceMultiplier;
        }

        public MerchantOffer getOffer(Entity entity, Random rand) {
            return new MerchantOffer(new ItemStack(Items.EMERALD, this.emeraldCost), this.itemStack.copy(), this.maxUses, this.villagerXp, this.priceMultiplier);
        }
    }
    static class EmeraldForItems implements VillagerTrades.ITrade {
        private final ItemStack itemStack;
        private final int maxUses;
        private final int villagerXp;
        private final int emeraldAmount;
        private final float priceMultiplier;

        public EmeraldForItems(Item item, int count, int maxUses, int villagerXp) {
            this(item, count, maxUses, villagerXp, 1);
        }

        public EmeraldForItems(Item item, int count, int maxUses, int villagerXp, int emeraldAmount) {
            this(new ItemStack(item.asItem(), count), maxUses, villagerXp, emeraldAmount);
        }

        public EmeraldForItems(ItemStack stack, int maxUses, int villagerXp, int emeraldAmount) {
            this.itemStack = stack;
            this.maxUses = maxUses;
            this.villagerXp = villagerXp;
            this.emeraldAmount = emeraldAmount;
            this.priceMultiplier = 0.05F;
        }

        public MerchantOffer getOffer(Entity entity, Random random) {
            return new MerchantOffer(this.itemStack.copy(), new ItemStack(Items.EMERALD, this.emeraldAmount), this.maxUses, this.villagerXp, this.priceMultiplier);
        }
    }


    public static class TypeSpecificTrade implements VillagerTrades.ITrade {
        private final Map<VillagerType, VillagerTrades.ITrade> trades;
        public TypeSpecificTrade(Map<VillagerType, VillagerTrades.ITrade> trades) {
            this.trades = trades;
        }



        public static TypeSpecificTrade oneTradeInBiomes(VillagerTrades.ITrade trade, VillagerType... types) {
            return new TypeSpecificTrade(Arrays.stream(types).collect(Collectors.toMap((type) -> type, (type) -> trade)));
        }

        @Nullable
        public MerchantOffer getOffer(Entity entity, Random random) {
            VillagerEntity villager = (VillagerEntity) entity;

            VillagerType villagertype = villager.getVillagerData().getType();
            VillagerTrades.ITrade villagertrades$itemlisting = this.trades.get(villagertype);
            return villagertrades$itemlisting == null ? null : villagertrades$itemlisting.getOffer(entity, random);
        }
    }

   public static class EnchantBookForEmeraldsTrade implements VillagerTrades.ITrade {
        private final int villagerXp;
        private final List<Enchantment> tradeableEnchantments;
        private final int minLevel;
        private final int maxLevel;

        public EnchantBookForEmeraldsTrade(int level) {
            this(level, Registry.ENCHANTMENT.stream().toArray(Enchantment[]::new));
        }

        public EnchantBookForEmeraldsTrade(int level, Enchantment... enchantments) {
            this(level, 0, Integer.MAX_VALUE, enchantments);
        }

        public EnchantBookForEmeraldsTrade(int villagerXp, int minLevel, int maxLevel, Enchantment... enchantments) {
            this.minLevel = minLevel;
            this.maxLevel = maxLevel;
            this.villagerXp = villagerXp;
            this.tradeableEnchantments = Arrays.asList(enchantments);
        }

        public MerchantOffer getOffer(Entity trader, Random rand) {
            Enchantment enchantment = this.tradeableEnchantments.get(rand.nextInt(this.tradeableEnchantments.size()));
            int i = Math.max(enchantment.getMinLevel(), this.minLevel);
            int j = Math.min(enchantment.getMaxLevel(), this.maxLevel);
            int k = MathHelper.nextInt(rand, i, j);
            ItemStack itemstack = EnchantedBookItem.getEnchantedItemStack(new EnchantmentData(enchantment, k));
            int l = 2 + rand.nextInt(5 + k * 10) + 3 * k;
            if (enchantment.isTreasureEnchantment()) {
                l *= 2;
            }

            if (l > 64) {
                l = 64;
            }

            return new MerchantOffer(new ItemStack(Items.EMERALD, l), new ItemStack(Items.BOOK), itemstack, 12, this.villagerXp, 0.2F);
        }
    }

}
