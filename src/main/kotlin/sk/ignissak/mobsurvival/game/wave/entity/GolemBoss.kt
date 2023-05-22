package sk.ignissak.mobsurvival.game.wave.entity

import cz.craftmania.craftcore.xseries.messages.Titles
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.goal.FloatGoal
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.animal.IronGolem
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import org.bukkit.Particle
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer
import org.bukkit.event.entity.EntityPotionEffectEvent
import sk.ignissak.mobsurvival.Main

class GolemBoss(entitytypes: EntityType<out IronGolem>, world: Level) : IronGolem(entitytypes, world) {

    private var stunTickCooldown: Int = 100

    override fun registerGoals() {
        goalSelector.removeAllGoals()
        targetSelector.removeAllGoals()

        goalSelector.addGoal(0, FloatGoal(this))
        goalSelector.addGoal(1, MeleeAttackGoal(this, 1.0, true))
        goalSelector.addGoal(2, LookAtPlayerGoal(this, Player::class.java, 16.0f))

        targetSelector.addGoal(0, GolemBossGoal(this))
        targetSelector.addGoal(1, NearestAttackableTargetGoal(this, Player::class.java, false, false))
    }


    override fun doHurtTarget(entity: Entity): Boolean {
        val v = super.doHurtTarget(entity)

        if (stunTickCooldown <= 0) {
            stunTickCooldown = 100
            if (entity is LivingEntity) {
                if (entity is Player) {
                    Titles("§c§lSTUNNED", "", 0, 40, 10).send(entity.bukkitEntity as org.bukkit.entity.Player)
                }
                entity.addEffect(MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1), EntityPotionEffectEvent.Cause.PLUGIN)
                entity.addEffect(MobEffectInstance(MobEffects.DARKNESS, 40, 1), EntityPotionEffectEvent.Cause.PLUGIN)
            }
            if (entity is Player)
                Main.waveManager.boss?.removeThreat(entity.bukkitEntity as org.bukkit.entity.Player)
        }
        return v
    }

    class GolemBossGoal(private val golem: GolemBoss) : Goal() {

        override fun canUse(): Boolean {
            return true //golem.isAlive && Main.gamePlayerManager.getPlayingPlayers().size > 1
        }

        override fun canContinueToUse(): Boolean {
            return true //Main.gamePlayerManager.getPlayingPlayers().size > 1
        }

        override fun tick() {
            if (golem.stunTickCooldown > 0) {
                golem.stunTickCooldown--
                return
            } else if (golem.stunTickCooldown == 0) {
                // Can use stun ability
                golem.level.world.spawnParticle(Particle.VILLAGER_ANGRY, golem.bukkitEntity.location.clone().add(.0, 1.0, .0), 1)
            }
        }

    }
}