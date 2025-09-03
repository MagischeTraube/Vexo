package io.github.vexo.utils.HUD

import io.github.vexo.config.*
import io.github.vexo.events.ChatPacketEvent
import net.minecraft.client.Minecraft
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object TestHUD : Module(
    name = "Test HUD",
    description = "Simple test HUD to verify the system works",
    category = "Dungeons"
) {
    private val mc = Minecraft.getMinecraft()
    private val hudText = "Test HUD 12313123121"

    private val hud = HUDSetting(
        name = hudText,
        x = 50f,
        y = 50f,
        scale = 1f,
        toggleable = true,
        description = "Shows a simple test message",
        module = this,
        block = {
            val font = mc.fontRendererObj
            val width = (font.getStringWidth(hudText)).toFloat()
            val height = (font.FONT_HEIGHT).toFloat()
            width to height
        }
    )
    init {
        HUDRenderer.addHUD(hud)
    }

    @SubscribeEvent(receiveCanceled = true)
    fun onChat(event: ChatPacketEvent) {
        if (event.message == "Welcome to Hypixel SkyBlock!") {
            hud.value.enabled = false
        } else if (event.message == "You must be in a party to join the party channel!") {
            hud.value.enabled = true
        }
    }
}
