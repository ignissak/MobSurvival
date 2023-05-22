package sk.ignissak.mobsurvival.item

import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot

class CustomItemListener : Listener {

    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        val player = event.player
        if (!(event.action == Action.RIGHT_CLICK_AIR || event.action == Action.RIGHT_CLICK_BLOCK)) return

        val item = player.equipment?.itemInMainHand ?: return
        if (!item.hasItemMeta()) return
        if (event.hand == EquipmentSlot.OFF_HAND) return

        val customItem = CustomItem.getItemByName(item.itemMeta!!.displayName) ?: return
        customItem.action(player)

        event.isCancelled = true
        event.setUseInteractedBlock(Event.Result.DENY)
        event.setUseItemInHand(Event.Result.DENY)
    }
}