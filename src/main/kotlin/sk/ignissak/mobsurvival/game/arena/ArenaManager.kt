package sk.ignissak.mobsurvival.game.arena

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import sk.ignissak.mobsurvival.Main
import sk.ignissak.mobsurvival.utils.LocUtils

class ArenaManager {

    lateinit var lobbyLoc: Location
    lateinit var ingameLoc: Location
    lateinit var world: World

    fun parse() {
        lobbyLoc = Main.instance.config.getString("locations.lobby")?.let { LocUtils.parseLocation(it, true) }!!
        ingameLoc = Main.instance.config.getString("locations.ingame")?.let { LocUtils.parseLocation(it, true) }!!
        world = Bukkit.getServer().getWorld(Main.instance.config.getString("locations.world")!!)!!
    }
}