package io.github.vexo.events

import io.github.vexo.utils.skyblock.FORMATTING_CODE_PATTERN
import io.github.vexo.utils.skyblock.modMessage
import net.minecraft.network.play.server.S02PacketChat
import net.minecraft.network.play.server.S32PacketConfirmTransaction
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.Event
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

/**
 * Posts an event to the event bus and catches any errors.
 * @author Skytils
 */
fun Event.postAndCatch(): Boolean =
    runCatching {
        MinecraftForge.EVENT_BUS.post(this)
    }.onFailure {
        modMessage(it, this.toString())
    }.getOrDefault(isCanceled)


/**
 * @author Odin
*/

object EventTrigger {

    @SubscribeEvent
    fun onPacket(event: PacketEvent.Receive) {
        if (event.packet is S32PacketConfirmTransaction && !event.packet.func_148888_e()) ServerTickEvent().postAndCatch()

        if (event.packet is S02PacketChat && ChatPacketEvent(event.packet.chatComponent.unformattedText.replace(FORMATTING_CODE_PATTERN, "") ?: "").postAndCatch())
            event.isCanceled = true
    }
}