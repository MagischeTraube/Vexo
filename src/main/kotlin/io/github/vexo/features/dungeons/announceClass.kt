package io.github.vexo.features.dungeons

import io.github.vexo.config.*
import io.github.vexo.events.ServerTickEvent
import io.github.vexo.utils.dungeon.*
import io.github.vexo.utils.dungeon.ownClass
import io.github.vexo.utils.skyblock.modMessage
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object AnnounceClass : Module(
    name = "Announce Class",
    description = "Tells you what class you are at the start of a Dungeon",
    category = "Dungeons"
) {
    @SubscribeEvent
    fun onServerTick(event: ServerTickEvent) {
        modMessage("You are playing as ${myIGN()} the ${ownClass()} class.")
    }
}