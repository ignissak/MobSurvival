package sk.ignissak.mobsurvival.board

import cz.craftmania.craftcore.fastboard.FastBoard
import sk.ignissak.mobsurvival.Main
import sk.ignissak.mobsurvival.game.GameState
import sk.ignissak.mobsurvival.game.player.GamePlayer
import java.util.UUID

class BoardManager {
    companion object {
        private val boards: HashMap<GamePlayer, FastBoard> = hashMapOf()
        private const val title = "§c§lMOBSURVIVAL"

        fun setupPlayer(gamePlayer: GamePlayer) {
            val board = FastBoard(gamePlayer.player)
            board.updateTitle(title)
            board.updateLines(getLines(gamePlayer))

            boards[gamePlayer] = board
        }

        fun removePlayer(gamePlayer: GamePlayer) {
            boards.remove(gamePlayer)
        }

        fun updateAll() {
            for ((gamePlayer, board) in boards) {
                board.updateLines(getLines(gamePlayer))
            }
        }

        private fun getLines(gamePlayer: GamePlayer): List<String> {
            val list = arrayListOf<String>()
            when (Main.gameManager.state) {
                GameState.LOBBY -> {
                    list.add("§1")
                    list.add("§fPlayers: §c${Main.gamePlayerManager.getPlayingPlayers().size}§8/§c${Main.gameManager.maxPlayers}")
                    list.add("§fMin. players: §c${Main.gameManager.minPlayers}")
                    list.add("§2")
                }

                GameState.STARTING -> {
                    list.add("§1")
                    list.add("§fPlayers: §c${Main.gamePlayerManager.getPlayingPlayers().size}§8/§c${Main.gameManager.maxPlayers}")
                    list.add("§fStart in: §c${Main.gameManager.startupCooldown}")
                    list.add("§2")
                }

                GameState.INGAME -> {
                    list.add("§1")
                    list.add("§7Wave: §c${Main.waveManager.wave}")
                    list.add("§7Kills: §c${gamePlayer.kills}")
                    list.add("§7Gold: §6${gamePlayer.gold}g")
                    list.add("§7Lives: §c${gamePlayer.lives} ♥")
                    list.add("§2")
                    list.add("§7Alive: §c${Main.gamePlayerManager.getPlayingPlayers().size}${if (Main.gamePlayerManager.getSpectatingPlayers()
                            .isNotEmpty()
                    ) "§7+${Main.gamePlayerManager.getSpectatingPlayers().size}" else ""}§8/§c${Main.gameManager.startingPlayers}")
                    list.add("§7Time: §c${Main.gameManager.getFormattedTime(System.currentTimeMillis() - Main.gameManager.startTime)}")
                    list.add("§3")
                }

                GameState.ENDING -> {
                    list.add("§1")
                    list.add("§7Wave: §c${Main.waveManager.wave}")
                    list.add("§7Kills: §c${gamePlayer.kills}")
                    list.add("§7Time: §c${Main.gameManager.getFormattedTime(Main.gameManager.timeSpent)}")
                    list.add("§2")
                }

                else -> return list
            }
            list.add("§7(c) Jakub Bordáš")
            return list
        }
    }
}