package sk.ignissak.mobsurvival.item

import cz.craftmania.craftcore.builders.items.ItemBuilder
import cz.craftmania.craftcore.inventory.builder.SmartInventory
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import sk.ignissak.mobsurvival.shop.ShopMenu

enum class CustomItem(val item: ItemStack, val action: (player: Player) -> Unit) {

    SHOP_ITEM(item = ItemBuilder(Material.NETHER_STAR)
        .setName("§6Obchod")
        .build(), action = {
        SmartInventory.builder()
            .provider(ShopMenu()).size(1, 9).title("§6Obchod").build().open(it)
    })

    ;

    companion object {
        fun getItemByName(name: String): CustomItem? {
            return values().firstOrNull { it.item.itemMeta?.displayName.equals(name) }
        }
    }
}
