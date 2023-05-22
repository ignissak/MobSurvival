package sk.ignissak.mobsurvival.game.items

import cz.craftmania.craftcore.builders.items.ItemBuilder
import cz.craftmania.craftcore.xseries.XPotion
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

enum class Items(
    val price: Int,
    val upgradable: Boolean,
    val itemType: ItemType,
    val tier: Int,
    val itemStack: ItemStack
) {

    SWORD_TIER_1(
        0, true, ItemType.SWORD, 1,
        ItemBuilder(Material.WOODEN_SWORD)
            .setName("§7Meč §6lvl. 1")
            .build()
    ),

    SWORD_TIER_2(
        150, true, ItemType.SWORD, 2,
        ItemBuilder(Material.STONE_SWORD)
            .setName("§7Meč §6lvl. 2")
            .build()
    ),

    SWORD_TIER_3(
        300, true, ItemType.SWORD, 3,
        ItemBuilder(Material.IRON_SWORD)
            .setName("§7Meč §6lvl. 3")
            .build()
    ),

    SWORD_TIER_4(
        600, true, ItemType.SWORD, 4,
        ItemBuilder(Material.IRON_SWORD)
            .setName("§7Meč §6lvl. 4")
            .addEnchant(Enchantment.DAMAGE_ALL, 1)
            .build()
    ),

    SWORD_TIER_5(
        1200, false, ItemType.SWORD, 5,
        ItemBuilder(Material.IRON_SWORD)
            .setName("§7Meč §6lvl. 5")
            .addEnchant(Enchantment.DAMAGE_ALL, 3)
            .build()
    ),

    CHESTPLATE_TIER_1(
        0, true, ItemType.CHESTPLATE, 1,
        ItemBuilder(Material.LEATHER_CHESTPLATE)
            .setName("§7Chestplate §6lvl. 1")
            .build()
    ),

    CHESTPLATE_TIER_2(
        200, true, ItemType.CHESTPLATE, 2,
        ItemBuilder(Material.GOLDEN_CHESTPLATE)
            .setName("§7Chestplate §6lvl. 2")
            .build()
    ),

    CHESTPLATE_TIER_3(
        400, true, ItemType.CHESTPLATE, 3,
        ItemBuilder(Material.IRON_CHESTPLATE)
            .setName("§7Chestplate §6lvl. 3")
            .addEnchant(Enchantment.PROTECTION_PROJECTILE, 1)
            .build()
    ),

    CHESTPLATE_TIER_4(
        800, true, ItemType.CHESTPLATE, 4,
        ItemBuilder(Material.IRON_CHESTPLATE)
            .setName("§7Chestplate §6lvl. 4")
            .addEnchant(Enchantment.PROTECTION_PROJECTILE, 2)
            .addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
            .build()
    ),

    CHESTPLATE_TIER_5(
        1600, false, ItemType.CHESTPLATE, 5,
        ItemBuilder(Material.DIAMOND_CHESTPLATE)
            .setName("§7Chestplate §6lvl. 5")
            .addEnchant(Enchantment.PROTECTION_PROJECTILE, 2)
            .addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
            .build()
    ),

    LEGGINGS_TIER_1(
        0, true, ItemType.LEGGINGS, 1,
        ItemBuilder(Material.LEATHER_LEGGINGS)
            .setName("§7Leggings §6lvl. 1")
            .build()
    ),

    LEGGINGS_TIER_2(
        150, true, ItemType.LEGGINGS, 2,
        ItemBuilder(Material.GOLDEN_LEGGINGS)
            .setName("§7Leggings §6lvl. 2")
            .build()
    ),

    LEGGINGS_TIER_3(
        300, true, ItemType.LEGGINGS, 3,
        ItemBuilder(Material.IRON_LEGGINGS)
            .setName("§7Leggings §6lvl. 3")
            .addEnchant(Enchantment.PROTECTION_PROJECTILE, 1)
            .build()
    ),

    LEGGINGS_TIER_4(
        600, true, ItemType.LEGGINGS, 4,
        ItemBuilder(Material.IRON_LEGGINGS)
            .setName("§7Leggings §6lvl. 4")
            .addEnchant(Enchantment.PROTECTION_PROJECTILE, 2)
            .addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1)
            .build()
    ),

    LEGGINGS_TIER_5(
        1200, false, ItemType.LEGGINGS, 5,
        ItemBuilder(Material.DIAMOND_LEGGINGS)
            .setName("§7Leggings §6lvl. 5")
            .addEnchant(Enchantment.PROTECTION_PROJECTILE, 2)
            .addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
            .build()
    ),

    APPLE(
        20, false, ItemType.CONSUMABLE, 1,
        ItemBuilder(Material.APPLE)
            .setName("§7Jablko")
            .build()
    ),

    GOLDEN_CARROT(
        30, false, ItemType.CONSUMABLE, 1,
        ItemBuilder(Material.GOLDEN_CARROT)
            .setName("§7Zlatá mrkva")
            .build()
    ),

    GOLDEN_APPLE(
        60, false, ItemType.CONSUMABLE, 1,
        ItemBuilder(Material.GOLDEN_APPLE)
            .setName("§7Zlaté jablko")
            .build()
    ),

    STEAK(
        90, false, ItemType.CONSUMABLE, 1,
        ItemBuilder(Material.COOKED_BEEF, 3)
            .setName("§7Steak")
            .build()
    ),

    REGENERATION_POTION(
        300, false, ItemType.SPECIAL, 1,
        ItemBuilder(
            XPotion.buildItemWithEffects(
                Material.SPLASH_POTION,
                null,
                PotionEffect(PotionEffectType.REGENERATION, 20 * 5, 1)
            )
        )
            .setName("§7Regeneračný elixír")
            .build()
    ),

    STRENGTH_POTION(
        300, false, ItemType.SPECIAL, 1,
        ItemBuilder(
            XPotion.buildItemWithEffects(
                Material.SPLASH_POTION,
                null,
                PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 30, 1)
            )
        )
            .setName("§7Elixír síly")
            .build()
    ),

    TNT(
        300, false, ItemType.SPECIAL, 1,
        ItemBuilder(Material.TNT)
            .setName("§7TNT")
            .build()
    ),

    ;

    companion object {
        fun getByTierAndItemType(tier: Int, itemType: ItemType): Items? {
            return values().firstOrNull { it.tier == tier && it.itemType == itemType }
        }

        fun getNextTierByItemTypeAndCurrentTier(itemType: ItemType, currentTier: Int): Items? {
            return values().firstOrNull { it.itemType == itemType && it.tier == currentTier + 1 }
        }

        fun getMaxTierByItemType(itemType: ItemType): Int {
            return values().filter { it.itemType == itemType }.maxByOrNull { it.tier }?.tier ?: 1
        }

    }

}