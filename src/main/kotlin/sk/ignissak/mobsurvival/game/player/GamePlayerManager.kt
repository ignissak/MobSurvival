package sk.ignissak.mobsurvival.game.player


import org.bukkit.entity.Player


class GamePlayerManager {
    val playerMap: HashMap<Player, GamePlayer> = hashMapOf()

    fun onJoin(player: Player, gamePlayer: GamePlayer) {
        playerMap[player] = gamePlayer
    }

    fun onQuit(gamePlayer: GamePlayer) {
        playerMap.remove(gamePlayer.player)
    }

    fun getPlayingPlayers(): List<GamePlayer> {
        return playerMap.values.filter { it.state == GamePlayerState.PLAYING }
    }

    fun getSpectatingPlayers(): List<GamePlayer> {
        return playerMap.values.filter { it.state == GamePlayerState.SPECTATING }
    }

    fun getDeadPlayers(): List<GamePlayer> {
        return playerMap.values.filter { it.state == GamePlayerState.DEAD }
    }

    fun getIngamePlayers(): List<GamePlayer> {
        return playerMap.values.filter { it.state == GamePlayerState.PLAYING || it.state == GamePlayerState.SPECTATING }
    }

    fun getRandomPlayingPlayer(): GamePlayer? {
        return getPlayingPlayers().randomOrNull()
    }

    fun chooseOnePlayingPlayerApartFrom(excluded: Player): GamePlayer? {
        val players = getPlayingPlayers()
        if (players.size == 1) return null
        return players.filter { it.player != excluded }.randomOrNull()
    }

}