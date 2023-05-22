package sk.ignissak.mobsurvival.game.events

import cz.craftmania.craftcore.xseries.particles.ParticleDisplay
import cz.craftmania.craftcore.xseries.particles.XParticle
import org.bukkit.Bukkit
import org.bukkit.Particle
import org.bukkit.entity.Arrow
import org.bukkit.entity.Endermite
import org.bukkit.entity.Monster
import org.bukkit.entity.Player
import org.bukkit.entity.TNTPrimed
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.*
import sk.ignissak.mobsurvival.Main
import sk.ignissak.mobsurvival.board.BoardManager
import sk.ignissak.mobsurvival.game.GameState
import sk.ignissak.mobsurvival.utils.MessageUtils

class MobListener : Listener {

    companion object {
        val damagers: HashMap<Int, HashMap<Player, Double>> = hashMapOf()
    }

    @EventHandler
    fun onMobDeath(event: EntityDeathEvent) {
        val entity = event.entity

        event.droppedExp = 0
        event.drops.clear()
        var wasBoss = false

        if (Main.waveManager.boss != null) {
            if (entity.entityId == Main.waveManager.boss!!.entity.entityId) {
                wasBoss = true
                Main.waveManager.boss = null
                Main.waveManager.bossBar.setVisible(false)
                XParticle.helix(
                    Main.instance,
                    10,
                    2.0,
                    1.0,
                    1.0,
                    3,
                    1,
                    false,
                    true,
                    ParticleDisplay.simple(entity.location, Particle.DRAGON_BREATH).withCount(50)
                )
            }
        }

        Main.waveManager.entities.remove(entity.entityId)
        Main.waveManager.checkForNextWave()
        BoardManager.updateAll()

        Bukkit.getOnlinePlayers().forEach {
            it.level = Main.waveManager.entities.count()
        }

        if (Main.gameManager.state != GameState.INGAME) return

        val map = damagers[entity.entityId] ?: return
        map.toList().sortedBy { (_, value) -> value }.toMap()

        if (wasBoss) {
            MessageUtils.broadcast("§c§lBOSS §c${entity.customName} §c§lZOMREL!")
            MessageUtils.broadcast(" ")
        }

        var i = 1
        for ((player, damage) in map) {
            val gamePlayer = Main.gamePlayerManager.playerMap[player] ?: continue
            gamePlayer.kills += 1
            gamePlayer.addGold(damage.toInt())
            if (wasBoss)
                MessageUtils.broadcast("§c$i. §7${player.name} §8- §7${damage.toInt()} DMG")
            i += 1
        }

        damagers.remove(entity.entityId)
    }


    @EventHandler(priority = EventPriority.LOWEST)
    fun onHit(event: EntityDamageByEntityEvent) {
        if (event.damager !is Player) return
        val player = event.damager as Player
        val entity = event.entity

        if (!damagers.containsKey(entity.entityId))
            damagers[entity.entityId] = hashMapOf()

        val map = damagers[entity.entityId] ?: return

        if (map.containsKey(player)) {
            map[player] = map[player]!! + event.finalDamage
        } else {
            map[player] = event.finalDamage
        }

        MessageUtils.broadcast("&f${player.name} -> -${event.finalDamage} HP -> ${entity.customName}")

        if (entity.entityId == Main.waveManager.boss?.entity?.entityId) {
            Main.waveManager.boss?.onDamage(player, event.finalDamage)
            Main.waveManager.updateBossBar()
        }
    }

    @EventHandler
    fun onTarget(event: EntityTargetEvent) {
        if (event.entity is Monster && event.target is Monster) event.isCancelled = true
    }

    @EventHandler
    fun onMonsterFriendlyFire(event: EntityDamageByEntityEvent) {
        if (event.entity is Monster && event.damager is Arrow) event.isCancelled = true
        if (event.entity is Monster && event.damager is Monster) event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.LOW)
    fun onExplosion(event: ExplosionPrimeEvent) {
        if (event.entity !is TNTPrimed) return

        event.radius = 3f
        event.entity.location.world?.spawnParticle(
            Particle.EXPLOSION_HUGE,
            event.entity.location,
            5,
            1.0,
            1.0,
            1.0,
            0.01
        )
    }

    @EventHandler
    fun onSpawn(event: EntitySpawnEvent) {
        if (Main.gameManager.state != GameState.INGAME) {
            event.isCancelled = true
            return
        }
        Bukkit.getScheduler().runTaskLater(Main.instance, Runnable {
            if (event.entity is Player) return@Runnable
            if (!event.entity.hasMetadata("mob")) {
                event.isCancelled = true
            } else {
                // Has metadata
                if (!Main.waveManager.entities.containsKey(event.entity.entityId)) {
                    Main.waveManager.entities[event.entity.entityId] = event.entity
                }
            }
        }, 1)
    }

}