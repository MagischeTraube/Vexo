package io.github.vexo.features.dungeons

import io.github.vexo.config.InputSetting
import io.github.vexo.config.Module
import io.github.vexo.config.SliderSetting
import io.github.vexo.events.ChatPacketEvent
import io.github.vexo.events.ServerTickEvent
import io.github.vexo.utils.HUD.HUDSetting
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent


object HealerWishNotification : Module(
        name = "Wish Notification",
        description = "Tells you when Maxor is enraged for Healer Wish",
        category = "Dungeons"
    ) {
    private val textSetting = registerSetting(InputSetting("Wish Text Input:", "WISH!", "Einstellen"))
    //private val speedSetting = registerSetting(SliderSetting("Notification Duration", 1.0f, 0,5, "Geschwindigkeit"))
    private val hudText = textSetting.value
    private var HudTicks = 0


        private val hud = HUDSetting(
            name = hudText,
            x = 50f,
            y = 50f,
            scale = 1f,
            toggleable = true,
            description = "Shows a simple test message",
            module = this
        )

    @SubscribeEvent(receiveCanceled = true)
    fun showHud(event: ChatPacketEvent) {
        if (event.message == "⚠ Maxor is enraged! ⚠" && HudTicks == 0) {
            hud.value.enabled
            HudTicks = 20
        }
    }
    @SubscribeEvent
    fun onTick(event: ServerTickEvent){
        if(HudTicks != 0)
            HudTicks --
        else if (HudTicks == 0){
            hud.value.enabled = false
        }
    }

}