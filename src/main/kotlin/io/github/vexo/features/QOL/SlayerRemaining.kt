package io.github.vexo.features.QOL

import io.github.vexo.config.*
import io.github.vexo.events.ChatPacketEvent
import io.github.vexo.utils.skyblock.*
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent



object SlayerRemaining : Module(
    name = "Slayer Bosses Remaiing",
    description = "Tells you how many Slayer bosses you need to kill to reach the new level",
    category = "QOL"
) {
    private val XP_PerKill = registerSetting(InputSetting("Slyer EXP pe Kill:", "500", "Einstellen"))
    private val xp_given = registerSetting(InputSetting("Your curren Remaining xp:", "1000000", "Einstellen"))

    private var current_xp = xp_given.value.toInt()
    private var remainin_xp =
        current_xp / XP_PerKill.value.toInt()


    @SubscribeEvent(receiveCanceled = true)
    fun onChat(event: ChatPacketEvent) {
        if (event.message == "SLAYER QUEST COMPLETE!") {
            current_xp = current_xp - XP_PerKill.value.toInt()
            modMessage(remainin_xp)
        }
    }
}