package io.github.vexo.features.dungeons

import io.github.vexo.config.Module
import io.github.vexo.events.ChatPacketEvent
import io.github.vexo.utils.HUD.*
import io.github.vexo.utils.skyblock.TickDelayUtils
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import io.github.vexo.utils.skyblock.modMessage
import net.minecraft.client.Minecraft

object ImidNukeAlert : Module(
    name = "Imid Nuke Alert",
    description = "Tells u if u can Nuke for imid",
    category = "Dungeons"
) {
    private val hudText = "Clear to Nuke"

    private val hud = HUDSetting(
        name = hudText,
        x = 50f,
        y = 50f,
        scale = 1f,
        toggleable = true,
        description = "Shows a simple test message",
        module = this
    )

    private var revived = false

    @SubscribeEvent(receiveCanceled = true)
    fun onChat(event: ChatPacketEvent) {
        val player = Minecraft.getMinecraft().thePlayer ?: return@onChat
        val regex = Regex(".* was revived by ${Regex.escape(player.name)}!")

        if (event.message == "You must be in a party to join the party channel!" && !revived) {
            hud.value.enabled = !hud.value.enabled
            TickDelayUtils.tickDelay(40){
                hud.value.enabled = !hud.value.enabled
            }
        } else if (regex.matches(event.message)) {
            modMessage("triggered")
            revived = true
        }
    }

}
//The Core entrance is opening!