package sk.ignissak.mobsurvival.game.events

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityTeleportEvent

class BossListener : Listener {

    @EventHandler
    fun onTeleport(event: EntityTeleportEvent) {
        val entity = event.entity
        
    }
}