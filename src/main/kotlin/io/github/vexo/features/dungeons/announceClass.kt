package io.github.vexo.features.dungeons

import io.github.vexo.config.*
import io.github.vexo.events.ChatPacketEvent
import io.github.vexo.utils.dungeon.ownClass
import io.github.vexo.utils.skyblock.modMessage
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object AnnounceClass : Module(
    name = "Announce Class",
    description = "tells you what class you are at start and in boss fight",
    category = "Dungeons"
) {
    private val boss = registerSetting(BooleanSetting("Announce in Boss", true, "Schaltet das Feature ein/aus"))
    private val start = registerSetting(BooleanSetting("Announce at start of teh Dungeons", true, "Schaltet das Feature ein/aus"))

    @SubscribeEvent
    fun onChat(event: ChatPacketEvent) {
        if (event.message.contains("You should find it useful if you get lost.") && start.value) {
            modMessage("Your class is ${ownClass()}")
        }
    }
}
