package io.github.vexo.utils.dungeon

import io.github.vexo.events.ChatPacketEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

val DungeonEnterMessage = listOf(
    Regex("entered MM The Catacombs, Floor"),
    Regex("entered The Catacombs, Floor")
)
var inDungeon = false

@SubscribeEvent
fun onChat(event: ChatPacketEvent){
    if (DungeonEnterMessage.any { it.containsMatchIn(event.message) }) {
        inDungeon = true
    }
}
