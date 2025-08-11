package io.github.vexo.features


import io.github.vexo.config.*
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object Example4 : Module(
    name = "Example4",
    description = "Keine Optionen",
    category = "Render"
) {
    val RagAxeTriggers = listOf(
        Regex("[BOSS] Wither King: I no longer wish to fight, but I know that will not stop you."),
        Regex("[BOSS] Livid: I can now turn those Spirits into shadows of myself, identical to their creator."),
        Regex("[BOSS] Sadan: I am the bridge between this realm and the world below! You shall not pass!")
    )

    @SubscribeEvent
    fun onChat(event: ClientChatReceivedEvent) {
        if (RagAxeTriggers.any { it.containsMatchIn(event.message.getFormattedText()) }) {
            System.out.println("RagAxe Now!")
        }
    }

}
