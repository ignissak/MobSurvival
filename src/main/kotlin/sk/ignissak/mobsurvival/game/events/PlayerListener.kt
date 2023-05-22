package sk.ignissak.mobsurvival.game.events

import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Endermite
import org.bukkit.entity.IronGolem
import org.bukkit.entity.Mob
import org.bukkit.entity.Player
import org.bukkit.entity.TNTPrimed
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.*
import sk.ignissak.mobsurvival.Main
import sk.ignissak.mobsurvival.board.BoardManager
import sk.ignissak.mobsurvival.game.GameState
import sk.ignissak.mobsurvival.game.items.PlayerItems
import sk.ignissak.mobsurvival.game.player.GamePlayer

class PlayerListener : Listener {

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player
        val gamePlayer = GamePlayer(player)

        gamePlayer.playerItems = PlayerItems(gamePlayer)

        gamePlayer.onJoin()
        BoardManager.updateAll()

        Main.gameManager.clearEntities()
        Main.gameManager.checkStart()

        event.joinMessage = "§c${player.name} §7sa §apripojil §7(§c${Bukkit.getOnlinePlayers().count()}§7/§c${Main.gameManager.maxPlayers}§7)"
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        val player = event.player
        val gamePlayer = Main.gamePlayerManager.playerMap[player] ?: return

        gamePlayer.onQuit()
        Main.gameManager.checkEnd()

        event.quitMessage = "§c${player.name} §7sa §codpojil §7(§c${Bukkit.getOnlinePlayers().count()}§7/§c${Main.gameManager.maxPlayers}§7)"
    }

    @EventHandler
    fun onLogin(event: PlayerLoginEvent) {
        if (Main.gameManager.state != GameState.LOBBY && Main.gameManager.state != GameState.STARTING) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "§cUž sa nemôžeš pripojiť, hra už započala!")
        }
    }

    @EventHandler
    fun onPlayerDeath(event: EntityDamageByEntityEvent) {
        if (event.entity is Player && event.damager is Player) {
            // Disable friendly fire
            event.isCancelled = true
            return
        } else if (event.entity is Mob && event.damager is IronGolem) {
            event.isCancelled = true
            return
        }
        if (event.entity !is Player) return
        if (event.damager is Player) return
        if (event.damager is TNTPrimed) {
            event.isCancelled = true
            return
        }
        val player = event.entity as Player
        if (event.finalDamage < player.health) return

        val gamePlayer = Main.gamePlayerManager.playerMap[player] ?: return
        gamePlayer.onDeath()

        Main.gameManager.checkEnd()
        event.isCancelled = true
    }

    @EventHandler
    fun onPlayerDeath2(event: EntityDamageEvent) {
        if (event.entity !is Player) return
        if (Main.gameManager.state != GameState.INGAME) {
            event.isCancelled = true
            return
        }
        if (event.finalDamage >= (event.entity as Player).health) {
            val gamePlayer = Main.gamePlayerManager.playerMap[event.entity] ?: return
            when (event.cause) {
                EntityDamageEvent.DamageCause.WITHER -> {
                    gamePlayer.onDeath()
                }
                EntityDamageEvent.DamageCause.ENTITY_EXPLOSION, EntityDamageEvent.DamageCause.BLOCK_EXPLOSION -> {
                    event.isCancelled = true
                }
                else -> {}
            }
            Main.gameManager.checkEnd()
        }
    }

    @EventHandler
    fun onChat(e: AsyncPlayerChatEvent) {
        val p = e.player
        if (!Main.gameManager.enabledChat) {
            e.isCancelled = true
            return
        }
        e.format = "§c" + p.name + "§8: §7%2\$s"
    }

    @EventHandler
    fun onDrop(e: PlayerDropItemEvent) {
        if (!e.player.isOp && e.player.gameMode != GameMode.CREATIVE) e.isCancelled = true
    }

    @EventHandler
    fun onItemDamage(event: PlayerItemDamageEvent) {
        if (Main.gameManager.state === GameState.INGAME) event.isCancelled = true
    }

    @EventHandler
    fun onHungerChange(event: FoodLevelChangeEvent) {
        if (Main.gameManager.state !== GameState.INGAME) event.isCancelled = true
    }

    @EventHandler
    fun onMove(event: PlayerMoveEvent) {
        val p = event.player
        if (p.location.y <= 0 && Main.gameManager.state !== GameState.INGAME
        ) p.teleport(Main.arenaMananger.lobbyLoc) else if (p.location.y <= 0 && Main.gameManager.state === GameState.INGAME
        ) p.teleport(Main.arenaMananger.ingameLoc)
    }

    @EventHandler
    fun onDrag(event: InventoryClickEvent) {
        if (event.inventory.type == InventoryType.CRAFTING) {
            if (event.currentItem!!.type == Material.AIR || event.currentItem == null) return
            if (event.currentItem!!.type == Material.NETHER_STAR) event.isCancelled =
                true else if (event.slotType == InventoryType.SlotType.ARMOR) event.isCancelled =
                true else if (event.slotType == InventoryType.SlotType.CRAFTING) event.isCancelled = true
        }
    }

}