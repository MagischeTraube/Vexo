package io.github.vexo.features.dungeons

import io.github.vexo.config.*
import io.github.vexo.events.ChatPacketEvent
import io.github.vexo.utils.dungeon.inDungeon
import io.github.vexo.utils.dungeon.ownClass
import io.github.vexo.utils.skyblock.modMessage
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.event.world.WorldEvent

object AnnounceClass : Module(
    name = "Announce Class",
    description = "Tells you what class you are at the start of a Dungeon",
    category = "Dungeons"
) {
    private var sent = false

    @SubscribeEvent
        fun onServerTick(event: ChatPacketEvent) {
            if (inDungeon) modMessage("You are in a dungeon, waiting for the class announcement...")
            if (ownClass() != null && !sent) {
                modMessage("You are ${ownClass()}")
                sent = true
            }
        modMessage("test ${ownClass()}")
    }

    @SubscribeEvent
    fun onWorldUnload(event: WorldEvent.Unload) {
        sent = false
    }
}