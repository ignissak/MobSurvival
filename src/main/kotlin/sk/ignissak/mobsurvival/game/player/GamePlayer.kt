package sk.ignissak.mobsurvival.game.player

import cz.craftmania.craftcore.xseries.XPotion
import cz.craftmania.craftcore.xseries.XSound
import cz.craftmania.craftcore.xseries.messages.Titles
import org.bukkit.GameMode
import org.bukkit.Particle
import org.bukkit.entity.Player
import sk.ignissak.mobsurvival.Main
import sk.ignissak.mobsurvival.board.BoardManager
import sk.ignissak.mobsurvival.game.GameState
import sk.ignissak.mobsurvival.game.items.PlayerItems
import sk.ignissak.mobsurvival.item.CustomItem
import sk.ignissak.mobsurvival.utils.MessageUtils
import sk.ignissak.mobsurvival.utils.removePotions
import sk.ignissak.mobsurvival.utils.removeTargetters

data class GamePlayer(
    val player: Player,
    var playerItems: PlayerItems? = null,
    var kills: Int = 0,
    var gold: Int = 0,
    var lives: Int = 3,
    var state: GamePlayerState = GamePlayerState.PLAYING
) {

    fun onJoin() {
        Main.gamePlayerManager.onJoin(player, this)
        BoardManager.setupPlayer(this)
        Main.waveManager.bossBar.addPlayer(player)

        player.teleport(Main.arenaMananger.lobbyLoc)
        player.health = 20.0
        player.foodLevel = 20
        player.level = 0
        player.exp = 0f
        player.gameMode = GameMode.SURVIVAL
        player.inventory.clear()
        player.removePotions()
        player.equipment?.armorContents = arrayOf()
        player.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH)?.baseValue = 20.0

        player.inventory.setItem(8, CustomItem.SHOP_ITEM.item)
        XSound.AMBIENT_CAVE.play(player)
    }

    fun onQuit() {
        if (Main.gameManager.state == GameState.INGAME) {
            if (Main.gamePlayerManager.getIngamePlayers().size < Main.gameManager.minPlayers - 1) {
                Main.gamePlayerManager.getIngamePlayers().forEach {
                    if (gold > 0) {
                        it.gold += gold / (Main.gamePlayerManager.getIngamePlayers().size - 1)
                    }
                    MessageUtils.broadcast("&cHrá menej ako minimálny počet hráčov, počet HP zvýšený na 15 ♥.")
                    it.player.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH)?.baseValue = 30.0
                }
            }
        }

        Main.gamePlayerManager.onQuit(this)
        BoardManager.removePlayer(this)
        player.removeTargetters()
    }

    fun onDeath() {
        player.world.spawnParticle(Particle.SMOKE_LARGE, player.location, 20, 0.5, 0.5, 0.5, 0.1)
        player.gameMode = GameMode.SPECTATOR
        player.removePotions()
        player.removeTargetters()
        if (lives == 0) {
            // Dead
            state = GamePlayerState.DEAD

            player.health = 20.0
            player.foodLevel = 20
            player.inventory.clear()
            Titles("§4§lGAME OVER", "", 0, 60, 20).send(player)
        } else {
            // Lose a life
            lives -= 1

            player.health = 10.0
            state = GamePlayerState.SPECTATING

            Titles("§c-1 ♥", "", 0, 60, 20).send(player)
        }

        BoardManager.updateAll()
    }

    fun respawn() {
        if (state != GamePlayerState.SPECTATING) return

        Titles("§c$lives ♥", "", 0, 40, 10).send(player)
        player.teleport(Main.arenaMananger.ingameLoc)
        player.gameMode = GameMode.SURVIVAL
        state = GamePlayerState.PLAYING

        BoardManager.updateAll()
    }

    fun addGold(amount: Int) {
        if (state != GamePlayerState.PLAYING) return
        gold += amount
        MessageUtils.sendActionBar(this, "§6+$amount gold")
    }



}