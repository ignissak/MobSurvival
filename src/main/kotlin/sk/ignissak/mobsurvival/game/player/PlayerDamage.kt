package sk.ignissak.mobsurvival.game.player

import org.bukkit.entity.Player

data class PlayerDamage(val player: Player, var damage: Double = 0.0)
