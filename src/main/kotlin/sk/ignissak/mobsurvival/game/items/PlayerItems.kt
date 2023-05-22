package sk.ignissak.mobsurvival.game.items

import cz.craftmania.craftcore.messages.chat.ChatInfo
import cz.craftmania.craftcore.xseries.XSound
import sk.ignissak.mobsurvival.Main
import sk.ignissak.mobsurvival.game.GameState
import sk.ignissak.mobsurvival.game.player.GamePlayer

class PlayerItems(
    val gamePlayer: GamePlayer,
    var sword: Items = Items.SWORD_TIER_1,
    var chestplate: Items = Items.CHESTPLATE_TIER_1,
    var leggings: Items = Items.LEGGINGS_TIER_1
) {

    fun getTier(itemType: ItemType): Int {
        return when (itemType) {
            ItemType.SWORD -> sword.tier
            ItemType.CHESTPLATE -> chestplate.tier
            ItemType.LEGGINGS -> leggings.tier
            else -> 1
        }
    }

    fun upgrade(itemType: ItemType) {
        if (Main.gameManager.state != GameState.INGAME) return
        val maxTier = Items.getMaxTierByItemType(itemType)
        val currentTier = getTier(itemType)
        val currentTierItem = Items.getByTierAndItemType(currentTier, itemType) ?: return

        if (maxTier == currentTier) return
        // Not on max tier

        val nextTier = currentTier + 1
        val nextItem = Items.getByTierAndItemType(nextTier, itemType) ?: return

        if (gamePlayer.gold < nextItem.price) {
            // Not enough gold
            ChatInfo.error(gamePlayer.player, "Nemáš dostatok goldov!")
            return
        }

        // Buy the next tier
        when (itemType) {
            ItemType.SWORD -> {
                gamePlayer.player.inventory.remove(currentTierItem.itemStack)
                gamePlayer.player.inventory.addItem(nextItem.itemStack)
                sword = nextItem
            }
            ItemType.CHESTPLATE -> {
                gamePlayer.player.equipment?.chestplate = nextItem.itemStack
                chestplate = nextItem
            }
            ItemType.LEGGINGS -> {
                gamePlayer.player.equipment?.leggings = nextItem.itemStack
                leggings = nextItem
            }
            else -> return
        }
        ChatInfo.success(
            gamePlayer.player,
            "Kúpil si ${nextItem.itemStack.itemMeta?.displayName}§a za §6${nextItem.price}g§a!"
        )
        XSound.ENTITY_EXPERIENCE_ORB_PICKUP.play(gamePlayer.player)

        gamePlayer.player.updateInventory()
        gamePlayer.gold -= nextItem.price
    }

    // Only for buying non-upgradable items
    fun buy(item: Items) {
        if (Main.gameManager.state != GameState.INGAME) return
        if (item.upgradable) return
        if (gamePlayer.gold < item.price) {
            // Not enough gold
            ChatInfo.error(gamePlayer.player, "Nemáš dostatok goldov!")
            return
        }

        gamePlayer.player.inventory.addItem(item.itemStack)
        ChatInfo.success(
            gamePlayer.player,
            "Kúpil si ${item.itemStack.itemMeta?.displayName}§a za §6${item.price}g§a!"
        )
        XSound.ENTITY_EXPERIENCE_ORB_PICKUP.play(gamePlayer.player)

        gamePlayer.player.updateInventory()
        gamePlayer.gold -= item.price
    }


}