package sk.ignissak.mobsurvival.utils

import org.bukkit.Bukkit
import org.bukkit.Location

object LocUtils {

    fun parseLocation(unparsed: String, yawpitch: Boolean): Location {
        val loc = unparsed.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val world = loc[0]
        val x = loc[1].toDouble()
        val y = loc[2].toDouble()
        val z = loc[3].toDouble()
        val yaw = if (yawpitch) loc[4].toFloat() else 0f
        val pitch = if (yawpitch) loc[5].toFloat() else 0f
        return Location(Bukkit.getWorld(world), x, y, z, yaw, pitch)
    }

}