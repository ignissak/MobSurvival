package sk.ignissak.mobsurvival.game.events

import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Blaze
import org.bukkit.entity.TNTPrimed
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockBurnEvent
import org.bukkit.event.block.BlockIgniteEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.block.BlockSpreadEvent
import org.bukkit.event.block.LeavesDecayEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemDamageEvent
import org.bukkit.event.weather.WeatherChangeEvent
import org.bukkit.inventory.EquipmentSlot

class WorldListener : Listener {

    @EventHandler
    fun onItemDamage(event: PlayerItemDamageEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun onWeatherChange(event: WeatherChangeEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun onBreak(e: BlockBreakEvent) {
        if (e.player.isOp && e.player.gameMode == GameMode.CREATIVE) return
        e.isCancelled = true
    }

    @EventHandler
    fun onPlace(e: BlockPlaceEvent) {
        if (e.player.isOp && e.player.gameMode == GameMode.CREATIVE) return
        e.isCancelled = true
    }

    @EventHandler
    fun onTNT(event: PlayerInteractEvent) {
        val p = event.player
        if (!(event.action == Action.RIGHT_CLICK_BLOCK || event.action == Action.RIGHT_CLICK_AIR)) return
        if (event.hand != EquipmentSlot.HAND) return
        val item = p.inventory.itemInHand
        if (item.type != Material.TNT) return
        if (item.amount == 1) p.inventory.remove(item) else item.amount = item.amount - 1
        val tnt = p.world.spawn(p.location, TNTPrimed::class.java)
        tnt.velocity = p.location.direction.multiply(1.1)
        tnt.fuseTicks = 20
        event.isCancelled = true
    }

    @EventHandler
    fun onLeaveDecay(event: LeavesDecayEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun onExplode(event: EntityExplodeEvent) {
        event.blockList().clear()
        event.isCancelled = true
    }

    @EventHandler
    fun onBurn(event: BlockBurnEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun onSpread(event: BlockSpreadEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun onIgnite(event: BlockIgniteEvent) {
        if (event.ignitingEntity !is Blaze || event.cause != BlockIgniteEvent.IgniteCause.FIREBALL) event.isCancelled = true
    }

}