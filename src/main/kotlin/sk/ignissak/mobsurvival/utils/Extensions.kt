package sk.ignissak.mobsurvival.utils

import org.bukkit.entity.Entity
import org.bukkit.entity.Mob
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect

fun Entity.removeTarget() {
    if (this is Mob) {
        this.target = null
    }
}

fun Entity.removeTargetters() {
    this.world.entities.forEach {
        if (it is Mob) {
            if (it.target == this) {
                it.target = null
            }
        }
    }
}

fun Player.removePotions() {
    activePotionEffects.forEach { potionEffect: PotionEffect ->
        removePotionEffect(potionEffect.type)
    }
}