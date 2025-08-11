package io.github.vexo.features


import io.github.vexo.config.*
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object Example3 : Module(
    name = "Example3",
    description = "Keine Optionen",
    category = "Test"
) {

    @SubscribeEvent
    fun onChat(event: ClientChatReceivedEvent?) {
        println("Example3")
    }

}
