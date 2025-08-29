package io.github.vexo.features.dungeons

import io.github.vexo.config.*
import io.github.vexo.events.ChatPacketEvent
import io.github.vexo.utils.skyblock.modMessage
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent


object RagAxeNow : Module(
    name = "RagAxe Alert",
    description = "Triggers when RagAxe is mentioned in chat.",
    category = "Dungeons"
) {
    @SubscribeEvent
    fun onChat(event: ChatPacketEvent) {
        if (RagAxeTriggers.any { it.containsMatchIn(event.message) }) {
            modMessage("RagAxe Now!")
        }
    }
    private val RagAxeTriggers = listOf(
        Regex("\\[BOSS] Wither King: I no longer wish to fight, but I know that will not stop you."),
        Regex("\\[BOSS] Livid: I can now turn those Spirits into shadows of myself, identical to their creator."),
        Regex("\\[BOSS] Sadan: I am the bridge between this realm and the world below! You shall not pass!")
    )
}