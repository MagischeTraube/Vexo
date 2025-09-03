package io.github.vexo.features.dungeons

import io.github.vexo.config.*
import io.github.vexo.events.ChatPacketEvent
import io.github.vexo.events.ServerTickEvent
import io.github.vexo.utils.skyblock.modMessage
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object PadTimer : Module (
    name = "Pad Timer",
    description = "Timer for Pad in M/F7",
    category = "Dungeons"
    ){
    private val PadSetting = registerSetting(DropdownSetting("Pad", "Green", listOf("Green", "Purple"), "Choose pad mode"))

    private var ServerTicks = 0

    private val PadTimer = listOf(
        Regex("\\[BOSS] Storm: ENERGY HEED MY CALL!"),
        Regex("\\[BOSS] Storm: THUNDER LET ME BE YOUR CATALYST!")
    )

    @SubscribeEvent(receiveCanceled = true)
    fun onChat(event: ChatPacketEvent) {
        if (PadTimer.any { it.containsMatchIn(event.message) })
        when (PadSetting.value) {
            "Green" -> ServerTicks = 181
            "Purple" -> ServerTicks = 106
        }
    }

    @SubscribeEvent
    fun onTick (event: ServerTickEvent){
        if (ServerTicks != 0)
            ServerTicks --

        when(ServerTicks){
            60 -> modMessage("Pad in §a2.5s!")
            50 -> modMessage("Pad in §a2.0s!")
            40 -> modMessage("Pad in §a1.5s!")
            30 -> modMessage("Pad in §e1.0s!")
            20 -> modMessage("Pad in §e0.5s!")
            10 -> modMessage("Pad §cNOW!")
        }
    }
}
