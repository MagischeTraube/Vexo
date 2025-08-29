package io.github.vexo.features.dungeons

import io.github.vexo.config.*
import io.github.vexo.events.ChatPacketEvent
import io.github.vexo.utils.skyblock.sendCommand
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object SuperboomGrabber: Module(
    name = "Superboom Grabber",
    description = "Automatically grabs Superboom at the start of a Dungeon Run, requires Odin",
    category = "Dungeon"
) {
    @SubscribeEvent
    fun onChat(event: ChatPacketEvent){
        if (event.message == "Starting in 2 seconds.")
            sendCommand("od sb", true)
    }
}