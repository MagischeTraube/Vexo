package io.github.vexo.utils.dungeon

import io.github.vexo.events.PacketEvent
import io.github.vexo.utils.skyblock.modMessage
import net.minecraft.network.play.server.S38PacketPlayerListItem
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraft.client.Minecraft

val FORMATTING_CODE_PATTERN = Regex("§[0-9a-fk-or]", RegexOption.IGNORE_CASE)
inline val String?.noControlCodes: String
    get() = this?.replace(FORMATTING_CODE_PATTERN, "") ?: ""

object DungeonTabList {
    var tabListEntries: List<String> = emptyList()

    private val lastKnownRoles: MutableMap<String, String?> = mutableMapOf(
        "Tank" to null,
        "Archer" to null,
        "Berserk" to null,
        "Mage" to null,
        "Healer" to null
    )


    @SubscribeEvent
    fun onPacket(event: PacketEvent.Receive) {
        if (event.packet !is S38PacketPlayerListItem) return
        if (event.packet.action !in listOf(
                S38PacketPlayerListItem.Action.UPDATE_DISPLAY_NAME,
                S38PacketPlayerListItem.Action.ADD_PLAYER
            )
        ) return

        tabListEntries = event.packet.entries
            ?.mapNotNull { it.displayName?.unformattedText?.noControlCodes }
            ?: emptyList()

        listOf("Tank", "Archer", "Berserk", "Mage", "Healer").forEach { role ->
            val ign = findIGNForRole(role)
            if (ign != null) { // Nur aktualisieren, wenn ein Spieler mit der Rolle gefunden wird
                lastKnownRoles[role] = ign
            }
            modMessage("$role: ${lastKnownRoles[role] ?: "none"}")
        }
    }

    fun findIGNForRole(role: String): String? {
        val pattern = Regex("(?<=\\s)\\w+(?=\\s.*\\($role\\s)", RegexOption.IGNORE_CASE)
        return tabListEntries.firstNotNullOfOrNull { entry ->
            if (entry.contains("($role", ignoreCase = true)) {
                pattern.find(entry)?.value
            } else null
        }
    }
}

fun myIGN(): String? {
    val myName = Minecraft.getMinecraft().thePlayer.name
    return DungeonTabList.tabListEntries.firstOrNull { entry ->
        entry.noControlCodes.equals(myName, ignoreCase = true)
    }
}

fun ownClass(): String? {
    val myName = myIGN() ?: return null

    listOf("Tank", "Archer", "Berserk", "Mage", "Healer").forEach { role ->
        if (DungeonTabList.findIGNForRole(role)?.equals(myName, ignoreCase = true) == true) {
            return role
        }
    }

    return null
}
