package io.github.vexo.features.QOL

import io.github.vexo.config.*
import io.github.vexo.events.ChatPacketEvent
import io.github.vexo.utils.skyblock.*
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent



object AutoRejoin : Module(
    name = "Slayer Bosses Remaiing",
    description = "Tells you how many Slayer bosses you need to kill to reach the new level",
    category = "QOL"
) {
    private val XP_PerKill = registerSetting(InputSetting("Slyer EXP pe Kill:", "500", "Einstellen"))
    private val current_xp = registerSetting(InputSetting("Your curren Remaining xp:", "1000000", "Einstellen"))

    private var remainin_xp =
        current_xp.value.toInt() / XP_PerKill.value.toInt()


    @SubscribeEvent
    fun onChat(event: ChatPacketEvent) {
        if (event.message == "  SLAYER QUEST COMPLETE!") {
            modMessage(remainin_xp)
        }
    }
}