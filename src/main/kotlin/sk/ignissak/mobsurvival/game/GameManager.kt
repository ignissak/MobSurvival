package sk.ignissak.mobsurvival.game

import cz.craftmania.craftcore.xseries.XSound
import cz.craftmania.craftcore.xseries.messages.Titles
import cz.craftmania.craftcore.xseries.particles.ParticleDisplay
import cz.craftmania.craftcore.xseries.particles.XParticle
import org.bukkit.*
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import sk.ignissak.mobsurvival.Main
import sk.ignissak.mobsurvival.board.BoardManager
import sk.ignissak.mobsurvival.game.items.Items
import sk.ignissak.mobsurvival.item.CustomItem
import sk.ignissak.mobsurvival.task.TargetTask
import sk.ignissak.mobsurvival.utils.MessageUtils
import sk.ignissak.mobsurvival.utils.removePotions
import sk.ignissak.mobsurvival.utils.removeTarget
import java.util.concurrent.TimeUnit
import kotlin.math.max
import kotlin.math.min

class GameManager {

    val minPlayers = 3
    val maxPlayers = 6
    var state: GameState = GameState.LOBBY
    var startTime = 0L
    var endTime = 0L
    var timeSpent = 0L
    var startupCooldown = 30
    var forceStart = false
    var enabledChat = true
    var startingPlayers = 3
    var targetTask: TargetTask = TargetTask()

    fun checkStart() {
        if (Bukkit.getOnlinePlayers().size >= minPlayers) {
            startCountdown()
        }
    }

    fun startCountdown() {
        if (state == GameState.INGAME) return
        clearEntities()
        if (state == GameState.STARTING) {
            if (Bukkit.getOnlinePlayers().size == maxPlayers && startupCooldown > 15) {
                startupCooldown = 15
            }
            return
        }

        state = GameState.STARTING
        Bukkit.getScheduler().runTaskTimer(Main.instance, Runnable { BoardManager.updateAll() }, 0L, 20L)

        val runnable = object: BukkitRunnable() {
            override fun run() {
                if (state == GameState.INGAME) {
                    this.cancel()
                    return
                }
                if (Bukkit.getOnlinePlayers().size < minPlayers && !forceStart) {
                    MessageUtils.broadcast("§cNedostatok hráčov na začiatok hry!")
                    Bukkit.getOnlinePlayers().forEach {
                        it.level = 0
                        if (startupCooldown <= 10) {
                            it.teleport(Main.arenaMananger.lobbyLoc)
                        }
                    }
                    state = GameState.LOBBY
                    startupCooldown = 30
                    this.cancel()
                    return
                }

                Bukkit.getOnlinePlayers().forEach {
                    it.level = startupCooldown
                }

                if (intArrayOf(20, 10, 5, 4, 3, 2, 1).contains(startupCooldown)) {
                    MessageUtils.broadcast("§cHra začne za §7$startupCooldown §csekúnd!")
                    Bukkit.getOnlinePlayers().forEach { XSound.BLOCK_LEVER_CLICK.play(it) }
                }

                if (startupCooldown == 10) {
                    enabledChat = false
                    Bukkit.getOnlinePlayers().forEach {
                        it.closeInventory()
                        it.inventory.clear()
                        it.health = 20.0
                        it.foodLevel = 20
                        it.teleport(Main.arenaMananger.ingameLoc)
                        clearEntities()
                        it.removePotions()
                    }
                }

                if (startupCooldown in 1..5) {
                    Bukkit.getOnlinePlayers().forEach {
                        Titles(formatCountdown(startupCooldown), "", 0, 30, 20).send(it)
                    }
                }

                if (startupCooldown <= 0) {
                    startGame()
                    this.cancel()
                }

                BoardManager.updateAll()
                startupCooldown--
            }
        }

        runnable.runTaskTimer(Main.instance, 20L, 20L)
    }

    fun startGame() {
        enabledChat = true
        startTime = System.currentTimeMillis()
        state = GameState.INGAME
        startingPlayers = Bukkit.getOnlinePlayers().size
        BoardManager.updateAll()

        setGamerules()
        clearEntities()

        Bukkit.getOnlinePlayers().forEach {
            Titles("§a§lHRA ZAČÍNA!", "", 0, 50, 20).send(it)
            XSound.ENTITY_LIGHTNING_BOLT_IMPACT.play(it)

            it.inventory.clear()
            it.inventory.setItem(8, CustomItem.SHOP_ITEM.item)
            it.inventory.setItem(0, Items.SWORD_TIER_1.itemStack)
            it.equipment?.chestplate = Items.CHESTPLATE_TIER_1.itemStack
            it.equipment?.leggings = Items.LEGGINGS_TIER_1.itemStack
            it.inventory.setItem(1, Items.STEAK.itemStack)
            it.gameMode = GameMode.SURVIVAL
        }

        Bukkit.getScheduler().runTaskTimer(Main.instance, targetTask, 20L, 20L)

        MessageUtils.broadcast("§aHra začala!")
        Main.waveManager.spawnNextWave(false)
        BoardManager.updateAll()
    }

    fun forceStart() {
        forceStart = true
        startupCooldown = 11
        startCountdown()
    }

    fun checkEnd() {
        if (Main.gamePlayerManager.getPlayingPlayers().isEmpty() && state == GameState.INGAME) {
            endGame()
        }
    }

    private fun endGame() {
        endTime = System.currentTimeMillis()
        timeSpent = System.currentTimeMillis() - startTime
        state = GameState.ENDING

        Bukkit.getOnlinePlayers().forEach {
            Titles("§4§lGAME OVER", "§7Max wave: §f${Main.waveManager.wave}", 0, 50, 20).send(it)

            XParticle.explosionWave(Main.instance, 0.5, ParticleDisplay.simple(Main.arenaMananger.ingameLoc, Particle.DRAGON_BREATH).withCount(50), ParticleDisplay.simple(Main.arenaMananger.ingameLoc, Particle.CRIT_MAGIC).withCount(200))
            XSound.UI_TOAST_CHALLENGE_COMPLETE.play(it)
        }

        Main.waveManager.entities.values.forEach {
            it.removeTarget()
        }

        Bukkit.getScheduler().runTaskLater(Main.instance, Runnable { stopServer() }, 10 * 20L)
    }

    private fun stopServer() {
        clearEntities()
        clearEntities()
        Bukkit.shutdown()
    }

    fun getExcessPlayers(): Int {
        return Bukkit.getOnlinePlayers().count() - minPlayers
    }

    fun clearEntities() {
        val c1 = Location(Main.arenaMananger.world, 130.0, 76.0, -72.0).chunk
        val c2 = Location(Main.arenaMananger.world, 52.0, 33.0, -153.0).chunk
        val xMin = min(c1.x, c2.x)
        val xMax = max(c1.x, c2.x)
        val zMin = min(c1.z, c2.z)
        val zMax = max(c1.z, c2.z)
        for (x in xMin..xMax) {
            for (z in zMin..zMax) {
                val chunk: Chunk = Main.arenaMananger.world.getChunkAt(x, z)
                Main.arenaMananger.world.loadChunk(chunk)
                for (entity in chunk.entities) {
                    if (entity.type == EntityType.PLAYER) continue
                    entity.remove()
                }
            }
        }
    }

    fun clearPotions() {
        Bukkit.getOnlinePlayers().forEach { player: Player ->
            player.removePotions()
        }
    }

    fun formatCountdown(sec: Int): String {
        return if (sec == 5) {
            "§a§l5"
        } else if (sec == 4) {
            "§a§l4"
        } else if (sec == 3) {
            "§6§l3"
        } else if (sec == 2) {
            "§6§l2"
        } else if (sec == 1) {
            "§c§l1"
        } else {
            ""
        }
    }

    fun getFormattedTime(time: Long): String {
        val str = StringBuilder()
        val min = TimeUnit.MILLISECONDS.toMinutes(time)
        val sec = TimeUnit.MILLISECONDS.toSeconds(time) % 60
        str.append(String.format("%02d", min) + ":")
        str.append(String.format("%02d", sec))

        return str.toString()
    }
    
    private fun setGamerules() {
        Main.arenaMananger.world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false)
        Main.arenaMananger.world.setGameRule(GameRule.DO_ENTITY_DROPS, false)
        Main.arenaMananger.world.setGameRule(GameRule.DO_TILE_DROPS, false)
        Main.arenaMananger.world
            .setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false)
        Main.arenaMananger.world.setGameRule(GameRule.MOB_GRIEFING, false)
        Main.arenaMananger.world
            .setGameRule(GameRule.SHOW_DEATH_MESSAGES, false)
        Main.arenaMananger.world.setGameRule(GameRule.DO_MOB_SPAWNING, true)
        Main.arenaMananger.world.setGameRule(GameRule.MAX_ENTITY_CRAMMING, 100)
        Main.arenaMananger.world
            .setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, false)
        Main.arenaMananger.world.spawnLocation = Main.arenaMananger.lobbyLoc
        Main.arenaMananger.world.difficulty = Difficulty.NORMAL
    }

}