package sk.ignissak.mobsurvival.game.wave

import org.bukkit.Location
import org.bukkit.entity.Ageable
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Skeleton
import org.bukkit.entity.Zombie
import org.bukkit.metadata.FixedMetadataValue
import sk.ignissak.mobsurvival.Main

enum class Mob(val entityType: EntityType,
               val baseHealth: Double,
               val sinceStage: Int?
) {

    ZOMBIE(EntityType.ZOMBIE, 35.0, 1),
    SKELETON(EntityType.SKELETON, 30.0, 5),
    PILLAGER(EntityType.PILLAGER, 45.0, 10),
    WITCH(EntityType.WITCH, 50.0, 15),
    PIGLIN_BRUTE(EntityType.PIGLIN_BRUTE, 60.0, 20),
    SILVERFISH(EntityType.SILVERFISH, 25.0, 25),
    BLAZE(EntityType.BLAZE, 40.0, 30),
    EVOKER(EntityType.EVOKER, 60.0, 35),
    ENDERMITE(EntityType.ENDERMITE, 25.0, null), // Spawns while Enderman performs an ability
    ;

    private fun getHealth(): Double {
        var health = baseHealth
        health += Main.gameManager.getExcessPlayers() * 10.0
        return health
    }

    fun spawn(location: Location? = null): Entity {
        var loc: Location? = location
        if (location == null) loc = Main.waveManager.getNextSpawnLocation()
        val e = Main.arenaMananger.world.spawnEntity(loc!!, entityType) as LivingEntity
        e.customName = "§c" + entityType.name.split("_").joinToString { it.lowercase().capitalize() }
        e.isCustomNameVisible = true
        e.removeWhenFarAway = false
        e.canPickupItems = false
        e.setAI(true)
        e.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH)?.baseValue = getHealth()
        e.health = getHealth()
        e.canPickupItems = false
        if (e is Ageable)
            e.setAdult()

        e.setMetadata("mob", FixedMetadataValue(Main.instance, "mob"))
        return e
    }

    companion object {

        fun getMobsByWave(wave: Int): List<Mob> {
            return values().filter { it.sinceStage != null }.filter { it.sinceStage!! <= wave }
        }

    }
}