package sk.ignissak.mobsurvival.game.wave

import org.bukkit.entity.Mob
import org.bukkit.entity.Player
import sk.ignissak.mobsurvival.Main

data class BossHolder(val entity: Mob, val bossType: Boss) {

    private val threat: HashMap<Player, Double> = hashMapOf()

    fun onDamage(player: Player, damage: Double) {
        if (threat.containsKey(player)) {
            threat[player] = threat[player]!! + damage
        } else {
            threat[player] = damage
        }

        if (true) //bossType != Boss.ENDERMAN
            calculateTarget()
    }

    fun removeThreat(player: Player) {
        threat[player] = .0
        calculateTarget()
    }

    private fun calculateTarget() {
        val sorted = threat.toList().sortedByDescending { it.second }
        if (sorted.size == 1) {
            entity.target = sorted[0].first.player
            Main.instance.logger.info("Boss target changed to ${sorted[0].first.name}")
            return
        }
        val first = sorted[0]
        val second = sorted[1]

        if (entity.target?.entityId == second.first.entityId) {
            if (first.second > second.second * 2) {
                entity.target = first.first.player
                Main.instance.logger.info("Boss target changed to ${first.first.name}")
            }
        }
    }
}
