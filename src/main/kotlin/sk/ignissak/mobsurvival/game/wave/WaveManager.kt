package sk.ignissak.mobsurvival.game.wave

import cz.craftmania.craftcore.messages.BossBar
import cz.craftmania.craftcore.xseries.XPotion
import cz.craftmania.craftcore.xseries.messages.Titles
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Entity
import org.bukkit.entity.Monster
import sk.ignissak.mobsurvival.Main
import sk.ignissak.mobsurvival.utils.MessageUtils

class WaveManager {

    val entities: HashMap<Int, Entity> = hashMapOf()
    var boss: BossHolder? = null
    private var previousBossType: Boss? = null
    private val spawnLocations: ArrayList<Location> = arrayListOf()
    var bossBar: BossBar = BossBar("", "RED", "SOLID", .0)

    private var currentLocation = 0
    var wave = 0
    
    init {
        bossBar.setVisible(false)

        spawnLocations.add(Location(Main.arenaMananger.world, 93.5, 46.0, -83.5, 180f, 0f))
        spawnLocations.add(Location(Main.arenaMananger.world, 120.5, 46.0, -110.5, 90f, 0f))
        spawnLocations.add(Location(Main.arenaMananger.world, 93.5, 46.0, -137.5, 0f, 0f))
        spawnLocations.add(Location(Main.arenaMananger.world, 66.5, 46.0, -110.5, 90f, 0f))
    }

    fun getEntitiesCount(): Int {
        return if (wave in 1..5) {
            12
        } else if (wave in 6..10) {
            14
        } else if (wave in 11..15) {
            16
        } else if (wave in 16..20) {
            18
        } else if (wave in 21..25) {
            20
        } else {
            22 + ((wave - 25) / 2)
        }
    }

    fun getNextSpawnLocation(): Location {
        val out: Location
        if (this.currentLocation < 3) {
            out = spawnLocations[this.currentLocation]
            this.currentLocation += 1
        } else {
            out = spawnLocations[this.currentLocation]
            this.currentLocation = 0
        }
        return out
    }

    fun addEntity(entity: Entity) {
        entities[entity.entityId] = entity
    }

    fun isBossWave(wave: Int): Boolean {
        return wave % 5 == 0 || true
    }

    fun checkForNextWave() {
        if (entities.isEmpty()) {
            if (isBossWave(wave + 1)) {
                Main.gameManager.clearPotions()
                MessageUtils.broadcast("§c§lINCOMING BOSS WAVE §7- §c${wave + 1}")
                Bukkit.getScheduler().runTaskLater(Main.instance, Runnable {
                    spawnNextWave(false)
                }, 400L)
            } else {
                MessageUtils.broadcast("§c§lINCOMING WAVE §7- §c${wave + 1}")
                Bukkit.getScheduler().runTaskLater(Main.instance, Runnable {
                    spawnNextWave(false)
                }, 200L)
            }
        }
    }

    fun spawnNextWave(force: Boolean) {
        wave += 1
        if (!force) Main.gameManager.clearEntities()
        if (isBossWave(wave)) {
            MessageUtils.broadcast("§c§lBOSS WAVE §7- §c$wave")
            Bukkit.getOnlinePlayers().forEach { Titles("§c§lBOSS WAVE", "§c$wave", 0, 40, 20).send(it) }
        } else {
            MessageUtils.broadcast("§c§lWAVE §7- §c$wave")
            Bukkit.getOnlinePlayers().forEach { Titles("§c§lWAVE", "§c$wave", 0, 40, 20).send(it) }
        }

        val mobsTypes = Mob.getMobsByWave(wave)
        val entitiesCount = getEntitiesCount()

        Main.gamePlayerManager.playerMap.values.forEach {
            it.respawn()
            it.player.level = entitiesCount
        }

        if (wave > 1) {
            Bukkit.getOnlinePlayers().forEach {
                XPotion.REGENERATION.buildPotionEffect(30, 2)?.apply(it)
            }
        }

        for (i in 1..entitiesCount) {
            val entity = mobsTypes.random().spawn()
            entities[entity.entityId] = entity
        }

        if (isBossWave(wave)) {
            val isSpecial = wave % 10 == 0
            val boss = Boss.values().filter { it != previousBossType && isSpecial == it.specialBoss }.random()
            val entity = boss.spawn()

            MessageUtils.broadcast("§a")
            MessageUtils.broadcast("§c§l${boss.name}")
            MessageUtils.broadcast("§a")
            MessageUtils.broadcast("§7${boss.description}")
            MessageUtils.broadcast("§a")

            this.boss = BossHolder(entity as org.bukkit.entity.Mob, boss)
            Bukkit.getOnlinePlayers().forEach { bossBar.addPlayer(it) }
            bossBar.setTitle("§c${boss.bossName} (${entity.health.toInt()} HP)")
            bossBar.setVisible(true)
            bossBar.setProgress(1.0)
        }
    }

    fun updateBossBar() {
        if (boss == null) return
        val health = boss!!.entity.health
        val maxHealth = boss!!.entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.value
        val progress = health / maxHealth
        bossBar.setTitle("§c${boss!!.bossType.bossName} (${boss!!.entity.health.toInt()} HP)")
        bossBar.setProgress(progress)
    }
}