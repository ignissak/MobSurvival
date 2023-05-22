package sk.ignissak.mobsurvival.game.wave

import org.bukkit.Location
import org.bukkit.attribute.Attribute
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld
import org.bukkit.entity.Ageable
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.metadata.FixedMetadataValue
import sk.ignissak.mobsurvival.Main
import sk.ignissak.mobsurvival.game.wave.entity.EndermanBoss
import sk.ignissak.mobsurvival.game.wave.entity.GolemBoss

enum class Boss(
    val entityType: EntityType,
    val bossName: String,
    val description: String,
    val baseHealth: Double,
    val baseDamage: Double,
    val speed: Double?,
    val specialBoss: Boolean = false
) {

    /*WITHER_SKELETON(EntityType.WITHER_SKELETON,
        "Wither Skeleton",
        "Dokáže sa healovať za časť damage, ktorý spôsobuje.",
        350.0,
        8.0,
        null,
        true),*/

    GOLEM(EntityType.IRON_GOLEM,
        "Golem",
        "Dokáže omráčiť hráčov na pár sekúnd.",
        400.0,
        7.0,
        null,
        false), //TODO

    /*ENDERMAN(EntityType.ENDERMAN,
        "Enderman",
        "Náhodne sa teleportuje medzi hráčmi. Pri teleportácií spawne endermitov.",
        450.0,
        7.0,
        null,
        true),

    RAVAGER(EntityType.RAVAGER,
        "Ravager",
        "Spawnuje až 3 Low-HP vindicatorov.",
        500.0,
        6.0,
        0.35,
        true),

    CAVE_SPIDER(EntityType.CAVE_SPIDER,
        "Cave Spider",
        "",
        300.0,
        6.0,
        null,
        false),

    HUSK(EntityType.HUSK,
        "Husk",
        "",
        300.0,
        6.0,
        null,
        false),*/

    ;

    fun getHealth(): Double {
        var health = baseHealth
        health += Main.gameManager.getExcessPlayers() * 100.0
        health += (Main.waveManager.wave / 5 - 1) * 100.0
        return health
    }

    fun spawn(): Entity {
        val loc: Location = Main.waveManager.getNextSpawnLocation()
        val e: LivingEntity
        when (this) {
            /*ENDERMAN -> {
                val endermanBoss =
                    EndermanBoss(net.minecraft.world.entity.EntityType.ENDERMAN, (loc.world as CraftWorld).handle)
                (loc.world as CraftWorld).handle.addFreshEntity(endermanBoss, CreatureSpawnEvent.SpawnReason.CUSTOM)

                e = endermanBoss.bukkitEntity as LivingEntity
                e.teleport(loc)
            }*/
            GOLEM -> {
                val golemBoss = GolemBoss(net.minecraft.world.entity.EntityType.IRON_GOLEM, (loc.world as CraftWorld).handle)
                (loc.world as CraftWorld).handle.addFreshEntity(golemBoss, CreatureSpawnEvent.SpawnReason.CUSTOM)

                e = golemBoss.bukkitEntity as LivingEntity
                e.teleport(loc)
            }
            else -> {
                e = Main.arenaMananger.world.spawnEntity(loc, entityType) as LivingEntity
            }
        }
        e.customName = "§c$bossName"
        e.isCustomNameVisible = true
        e.removeWhenFarAway = false
        e.canPickupItems = false
        e.setAI(true)
        e.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)?.baseValue = baseDamage
        e.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.baseValue = getHealth()
        if (speed != null) {
            e.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)?.baseValue = speed
        }
        e.health = getHealth()
        e.canPickupItems = false
        if (e is Ageable)
            e.setAdult()

        e.setMetadata("mob", FixedMetadataValue(Main.instance, "mob"))
        return e
    }

}