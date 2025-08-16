package io.github.vexo.utils.dungeon

import io.github.vexo.events.PacketEvent
import io.github.vexo.utils.skyblock.modMessage
import net.minecraft.network.play.server.S38PacketPlayerListItem
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

val FORMATTING_CODE_PATTERN = Regex("§[0-9a-fk-or]", RegexOption.IGNORE_CASE)

inline val String?.noControlCodes: String
    get() = this?.replace(FORMATTING_CODE_PATTERN, "") ?: ""

object DungeonTabList {
    var tabListEntries: List<String> = emptyList()

    @SubscribeEvent
    fun onPacket(event: PacketEvent.Receive) {
        when (event.packet) {
            is S38PacketPlayerListItem -> {
                if (event.packet.action !in listOf(
                        S38PacketPlayerListItem.Action.UPDATE_DISPLAY_NAME,
                        S38PacketPlayerListItem.Action.ADD_PLAYER
                    )) return

                tabListEntries = event.packet.entries
                    ?.mapNotNull { it.displayName?.unformattedText?.noControlCodes }
                    ?: emptyList()
                tankIGN()
                archerIGN()
                bersIGN()
                mageIGN()
                healIGN()
            }
        }
    }
}
fun tankIGN() {
    val tankPattern = Regex("(?<=\\s)\\w+(?=\\s.*\\(Tank\\s)", RegexOption.IGNORE_CASE)

    val tankIGN = DungeonTabList.tabListEntries.firstNotNullOfOrNull { entry ->
        tankPattern.find(entry)?.value
    }
}

fun archerIGN() {
    val archerPattern = Regex("(?<=\\s)\\w+(?=\\s.*\\(Archer\\s)", RegexOption.IGNORE_CASE)

    val archerIGN = DungeonTabList.tabListEntries.firstNotNullOfOrNull { entry ->
        archerPattern.find(entry)?.value
    }
}

fun bersIGN() {
    val berserkPattern = Regex("(?<=\\s)\\w+(?=\\s.*\\(Berserk\\s)", RegexOption.IGNORE_CASE)

    val berserkIGN = DungeonTabList.tabListEntries.firstNotNullOfOrNull { entry ->
        berserkPattern.find(entry)?.value
    }
}

fun mageIGN() {
    val magePattern = Regex("(?<=\\s)\\w+(?=\\s.*\\(Mage\\s)", RegexOption.IGNORE_CASE)

    val mageIGN = DungeonTabList.tabListEntries.firstNotNullOfOrNull { entry ->
        magePattern.find(entry)?.value
    }
}

fun healIGN() {
    val healerPattern = Regex("(?<=\\s)\\w+(?=\\s.*\\(Healer\\s)", RegexOption.IGNORE_CASE)

    val healerIGN = DungeonTabList.tabListEntries.firstNotNullOfOrNull { entry ->
        healerPattern.find(entry)?.value
    }
}