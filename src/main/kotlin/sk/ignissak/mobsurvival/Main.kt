package sk.ignissak.mobsurvival

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import sk.ignissak.mobsurvival.game.GameManager
import sk.ignissak.mobsurvival.game.arena.ArenaManager
import sk.ignissak.mobsurvival.game.events.MobListener
import sk.ignissak.mobsurvival.game.events.PlayerListener
import sk.ignissak.mobsurvival.game.events.WorldListener
import sk.ignissak.mobsurvival.game.player.GamePlayerManager
import sk.ignissak.mobsurvival.game.wave.WaveManager
import sk.ignissak.mobsurvival.item.CustomItemListener

class Main : JavaPlugin(), CommandExecutor {

    override fun onEnable() {
        instance = this
        saveDefaultConfig()

        arenaMananger = ArenaManager()
        arenaMananger.parse()

        gameManager = GameManager()
        gamePlayerManager = GamePlayerManager()
        waveManager = WaveManager()

        registerListeners()
        getCommand("forcestart")?.setExecutor(this)
    }

    override fun onDisable() {
    }

    private fun registerListeners() {
        Bukkit.getPluginManager().registerEvents(PlayerListener(), this)
        Bukkit.getPluginManager().registerEvents(WorldListener(), this)
        Bukkit.getPluginManager().registerEvents(MobListener(), this)
        Bukkit.getPluginManager().registerEvents(CustomItemListener(), this)
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String?>): Boolean {
        if (command.name.equals("forcestart", ignoreCase = true) && sender.isOp) {
            gameManager.forceStart()
            return true
        }
        return true
    }

    companion object {
        lateinit var instance: Main
        lateinit var gameManager: GameManager
        lateinit var gamePlayerManager: GamePlayerManager
        lateinit var waveManager: WaveManager
        lateinit var arenaMananger: ArenaManager
    }
}