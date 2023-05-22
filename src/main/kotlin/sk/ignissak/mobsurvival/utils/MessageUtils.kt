package sk.ignissak.mobsurvival.utils

import cz.craftmania.craftcore.xseries.messages.ActionBar
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import sk.ignissak.mobsurvival.game.player.GamePlayer

object MessageUtils {

    fun broadcast(message: String) {
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', message))
    }

    fun sendMessage(gamePlayer: GamePlayer, message: String) {
        gamePlayer.player.sendMessage(ChatColor.translateAlternateColorCodes('&', message))
    }

    fun sendActionBar(gamePlayer: GamePlayer, message: String) {
        ActionBar.sendActionBar(gamePlayer.player, ChatColor.translateAlternateColorCodes('&', message))
    }

}