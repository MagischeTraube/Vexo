package io.github.vexo.utils.dungeon

import io.github.vexo.events.PacketEvent
import net.minecraft.network.play.server.S38PacketPlayerListItem
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraft.client.Minecraft

val FORMATTING_CODE_PATTERN = Regex("§[0-9a-fk-or]", RegexOption.IGNORE_CASE)
inline val String?.noControlCodes: String
    get() = this?.replace(FORMATTING_CODE_PATTERN, "") ?: ""


var tabListEntries: List<String> = emptyList()
val myIgn: String by lazy {
    Minecraft.getMinecraft().thePlayer.name
}


val DungeonClass: MutableMap<String, String?> = mutableMapOf(
    "Tank" to null,
    "Archer" to null,
    "Berserk" to null,
    "Mage" to null,
    "Healer" to null
)

object IGNUtils {
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
            if (ign != null) {
                DungeonClass[role] = ign
            }
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

fun ownClass(): String? {
    return when (myIgn) {
        DungeonClass["Tank"] -> "Tank"
        DungeonClass["Archer"] -> "Archer"
        DungeonClass["Berserk"] -> "Berserk"
        DungeonClass["Mage"] -> "Mage"
        DungeonClass["Healer"] -> "Healer"
        else -> null
    }

}