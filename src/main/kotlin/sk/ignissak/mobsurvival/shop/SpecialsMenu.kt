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

class SpecialsMenu : InventoryProvider {

    override fun init(player: Player, inv: InventoryContents) {
        val gamePlayer = Main.gamePlayerManager.playerMap[player] ?: return
        val playerItems = gamePlayer.playerItems!!

        inv.set(
            0,
            3,
            ClickableItem.of(
                ItemBuilder(Items.REGENERATION_POTION.itemStack).addLore("§7Cena: §6${Items.REGENERATION_POTION.price}g")
                    .build()
            ) {
                playerItems.buy(Items.REGENERATION_POTION)
            })

        inv.set(
            0,
            4,
            ClickableItem.of(
                ItemBuilder(Items.STRENGTH_POTION.itemStack).addLore("§7Cena: §6${Items.STRENGTH_POTION.price}g").build()
            ) {
                playerItems.buy(Items.STRENGTH_POTION)
            })

        inv.set(
            0,
            5,
            ClickableItem.of(ItemBuilder(Items.TNT.itemStack).addLore("§7Cena: §6${Items.TNT.price}g").build()) {
                playerItems.buy(Items.TNT)
            })

        inv.set(0, 0, ClickableItem.of(ItemBuilder(Material.ARROW).setName("§7Späť").build()) {
            SmartInventory.builder().provider(ShopMenu()).size(1, 9).title("§6Obchod").build().open(player)
        })
    }

}