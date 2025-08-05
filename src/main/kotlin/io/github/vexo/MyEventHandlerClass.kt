package io.github.vexo
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

class MyEventHandlerClass {
    var chatCount: Int = 0

    @SubscribeEvent
    fun onChat(event: ClientChatReceivedEvent?) {
        chatCount++
        println("Chats received total: " + chatCount)
    }
}