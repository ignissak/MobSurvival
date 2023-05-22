package sk.ignissak.mobsurvival.shop

import cz.craftmania.craftcore.builders.items.ItemBuilder
import cz.craftmania.craftcore.inventory.builder.ClickableItem
import cz.craftmania.craftcore.inventory.builder.SmartInventory
import cz.craftmania.craftcore.inventory.builder.content.InventoryContents
import cz.craftmania.craftcore.inventory.builder.content.InventoryProvider
import org.bukkit.Material
import org.bukkit.entity.Player
import sk.ignissak.mobsurvival.Main
import sk.ignissak.mobsurvival.game.items.Items

class ConsumablesMenu : InventoryProvider {

    override fun init(player: Player, inv: InventoryContents) {
        val gamePlayer = Main.gamePlayerManager.playerMap[player] ?: return
        val playerItems = gamePlayer.playerItems!!

        inv.set(
            0,
            2,
            ClickableItem.of(ItemBuilder(Items.APPLE.itemStack).addLore("§7Cena: §6${Items.APPLE.price}g").build()) {
                playerItems.buy(Items.APPLE)
            })

        inv.set(
            0,
            3,
            ClickableItem.of(
                ItemBuilder(Items.GOLDEN_CARROT.itemStack).addLore("§7Cena: §6${Items.GOLDEN_CARROT.price}g").build()
            ) {
                playerItems.buy(Items.GOLDEN_CARROT)
            })

        inv.set(
            0,
            5,
            ClickableItem.of(
                ItemBuilder(Items.GOLDEN_APPLE.itemStack).addLore("§7Cena: §6${Items.GOLDEN_APPLE.price}g").build()
            ) {
                playerItems.buy(Items.GOLDEN_APPLE)
            })

        inv.set(
            0,
            6,
            ClickableItem.of(ItemBuilder(Items.STEAK.itemStack).addLore("§7Cena: §6${Items.STEAK.price}g").build()) {
                playerItems.buy(Items.STEAK)
            })

        inv.set(0, 0, ClickableItem.of(ItemBuilder(Material.ARROW).setName("§7Späť").build()) {
            SmartInventory.builder().provider(ShopMenu()).size(1, 9).title("§6Obchod").build().open(player)
        })
    }


}