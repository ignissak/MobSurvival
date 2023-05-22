package sk.ignissak.mobsurvival.shop

import cz.craftmania.craftcore.builders.items.ItemBuilder
import cz.craftmania.craftcore.inventory.builder.ClickableItem
import cz.craftmania.craftcore.inventory.builder.SmartInventory
import cz.craftmania.craftcore.inventory.builder.content.InventoryContents
import cz.craftmania.craftcore.inventory.builder.content.InventoryProvider
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import sk.ignissak.mobsurvival.Main
import sk.ignissak.mobsurvival.game.items.ItemType
import sk.ignissak.mobsurvival.game.items.Items

class ShopMenu : InventoryProvider {

    override fun init(player: Player, inv: InventoryContents) {
        val gamePlayer = Main.gamePlayerManager.playerMap[player] ?: return
        val playerItems = gamePlayer.playerItems!!

        val swordTier = playerItems.getTier(ItemType.SWORD)
        val chestplateTier = playerItems.getTier(ItemType.CHESTPLATE)
        val leggingsTier = playerItems.getTier(ItemType.LEGGINGS)

        val nextSword = Items.getNextTierByItemTypeAndCurrentTier(ItemType.SWORD, swordTier)
        val nextChestplate = Items.getNextTierByItemTypeAndCurrentTier(ItemType.CHESTPLATE, chestplateTier)
        val nextLeggings = Items.getNextTierByItemTypeAndCurrentTier(ItemType.LEGGINGS, leggingsTier)

        val swordItem: ItemStack = if (nextSword == null) {
            ItemBuilder(Material.BARRIER)
                .setName("§6Upgrade: &6Meč")
                .addLore("§7Max tier")
                .build()
        } else {
            ItemBuilder(nextSword.itemStack)
                .addLore("§7Tier: §c${nextSword.tier}", "§7Cena: §6${nextSword.price}g")
                .build()
        }

        val chestplateItem: ItemStack = if (nextChestplate == null) {
            ItemBuilder(Material.BARRIER)
                .setName("§6Upgrade: &6Chestplate")
                .addLore("§7Max tier")
                .build()
        } else {
            ItemBuilder(nextChestplate.itemStack)
                .addLore("§7Tier: §c${nextChestplate.tier}", "§7Cena: §6${nextChestplate.price}g")
                .build()
        }

        val leggingsItem: ItemStack = if (nextLeggings == null) {
            ItemBuilder(Material.BARRIER)
                .setName("§6Upgrade: &6Leggings")
                .addLore("§7Max tier")
                .build()
        } else {
            ItemBuilder(nextLeggings.itemStack)
                .addLore("§7Tier: §c${nextLeggings.tier}", "§7Cena: §6${nextLeggings.price}g")
                .build()
        }

        val consumablesItem = ItemBuilder(Material.APPLE)
            .setName("§6Jídlo")
            .addLore("§7Klikni pre otvorenie")
            .build()

        val specialsItem = ItemBuilder(Material.TNT)
            .setName("§6Špeciálne")
            .addLore("§7Klikni pre otvorenie")
            .build()

        inv.set(0, 2, ClickableItem.of(swordItem) {
            playerItems.upgrade(ItemType.SWORD)
            player.closeInventory()
        })

        inv.set(0, 3, ClickableItem.of(chestplateItem) {
            playerItems.upgrade(ItemType.CHESTPLATE)
            player.closeInventory()
        })

        inv.set(0, 4, ClickableItem.of(leggingsItem) {
            playerItems.upgrade(ItemType.LEGGINGS)
            player.closeInventory()
        })

        inv.set(0, 5, ClickableItem.of(consumablesItem) {
            player.closeInventory()
            SmartInventory.builder().provider(ConsumablesMenu()).size(1, 9).title("§6Jídlo").build().open(player)
        })

        inv.set(0, 6, ClickableItem.of(specialsItem) {
            player.closeInventory()
            SmartInventory.builder().provider(SpecialsMenu()).size(1, 9).title("§6Špeciálne").build().open(player)
        })
    }


}