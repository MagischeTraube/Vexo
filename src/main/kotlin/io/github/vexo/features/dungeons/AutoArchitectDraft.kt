package io.github.vexo.features.dungeons

import io.github.vexo.config.*
import io.github.vexo.events.ChatPacketEvent
import io.github.vexo.utils.dungeon.myIgn
import io.github.vexo.utils.skyblock.sendCommand
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent



object AutoArchitectDraft : Module(
    name = "Auto get Architects draft",
    description = "Retrieves Architects Draft on Puzzle fail",
    category = "Dungeons"
) {
    val PuzzleFail = listOf(
        Regex("""^PUZZLE FAIL! (\w{1,16}) .*${Regex.escape(myIgn)}.*$"""),
        Regex("""^\[STATUE] Oruo the Omniscient: (\w{1,16}) chose the wrong answer! I shall never forget this moment of misrememberance\. .*${Regex.escape(myIgn)}.*$""")
    )

    @SubscribeEvent(receiveCanceled = true)
    fun onChat(event: ChatPacketEvent){
        if(PuzzleFail.any {it.containsMatchIn(event.message)})
            sendCommand("gfs architect's first draft")
    }
}