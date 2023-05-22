package sk.ignissak.mobsurvival.task

import org.bukkit.entity.Mob
import sk.ignissak.mobsurvival.Main
import sk.ignissak.mobsurvival.game.GameState

class TargetTask : Runnable {

    override fun run() {
        if (Main.gameManager.state != GameState.INGAME) return
        Main.arenaMananger.world.entities.forEach {
            if (it is Mob) {
                it.target = Main.gamePlayerManager.getRandomPlayingPlayer()?.player
            }
        }
    }
}