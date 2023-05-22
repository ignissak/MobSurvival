package sk.ignissak.mobsurvival.game.wave.entity

import cz.craftmania.craftcore.xseries.XSound
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.ai.goal.FloatGoal
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.monster.EnderMan
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Monster
import sk.ignissak.mobsurvival.Main
import sk.ignissak.mobsurvival.game.wave.Mob
import java.util.*

class EndermanBoss(entitytypes: EntityType<out EnderMan>?, world: Level?) : EnderMan(entitytypes, world) {

    override fun registerGoals() {
        goalSelector.removeAllGoals()
        targetSelector.removeAllGoals()

        goalSelector.addGoal(0, FloatGoal(this))
        goalSelector.addGoal(1, MeleeAttackGoal(this, 1.0, true))
        goalSelector.addGoal(2, LookAtPlayerGoal(this, Player::class.java, 16.0f))

        targetSelector.addGoal(0, EndermanBossGoal(this))
        targetSelector.addGoal(1, NearestAttackableTargetGoal(this, Player::class.java, false, false))
    }

    // Random teleports
    override fun teleport(): Boolean {
        return false
    }

    override fun customServerAiStep() {}


    class EndermanBossGoal(private val enderman: EndermanBoss) : Goal() {

        private var delay: Int = 0

        init {
            flags = EnumSet.of(Flag.TARGET, Flag.MOVE, Flag.LOOK)
        }

        override fun canUse(): Boolean {
            if (Main.gamePlayerManager.getIngamePlayers().isEmpty()) return false
            return true
        }

        private fun getAbilityDelay(): Int = 20 * 8

        override fun start() {
            delay = adjustedTickDelay(getAbilityDelay())
        }

        override fun canContinueToUse(): Boolean {
            return enderman.isAlive && delay > 0 && Main.gamePlayerManager.getPlayingPlayers().isNotEmpty()
        }

        private fun teleportAbility() {
            val nextTarget =
                Main.gamePlayerManager.chooseOnePlayingPlayerApartFrom(enderman.target as org.bukkit.entity.Player)
                    ?: return

            Main.instance.logger.info("ENDERMAN: Teleporting to ${nextTarget.player.name}...")
            // Choose next target
            if (Main.waveManager.entities.values.count { it.type == org.bukkit.entity.EntityType.ENDERMITE } < Main.gamePlayerManager.getPlayingPlayers().size) {
                val endermite = Mob.ENDERMITE.spawn(enderman.bukkitEntity.location.clone().add(.0, 1.0, .0)) as Monster
                Main.waveManager.addEntity(endermite)
                endermite.target = enderman.target?.bukkitEntity as LivingEntity
                Bukkit.getOnlinePlayers().forEach {
                    it.level = Main.waveManager.entities.count()
                }
                Main.instance.logger.info("ENDERMAN: Spawning an endermite.")
            }

            XSound.ENTITY_ENDERMAN_TELEPORT.play(enderman.bukkitEntity.location)
            enderman.teleportTo(
                nextTarget.player.location.x,
                nextTarget.player.location.y,
                nextTarget.player.location.z
            )
            XSound.ENTITY_ENDERMAN_TELEPORT.play(enderman.bukkitEntity.location)
        }

        override fun tick() {
            --delay
            val target = enderman.target
            if (target != null) {
                enderman.target = enderman.level.getNearestPlayer(enderman, 64.0)
            }

            if (delay <= 0) {
                teleportAbility()
                delay = adjustedTickDelay(getAbilityDelay())
            }
        }

    }
}