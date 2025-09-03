package io.github.vexo.utils.HUD

import io.github.vexo.config.*
import io.github.vexo.events.ChatPacketEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent


object TestHUD : Module(
    name = "Test HUD",
    description = "Simple test HUD to verify the system works",
    category = "Dungeons"
) {
    var x = 0
    private val hud = HUDSetting(
        name = "Test HUD 12313123121",
        x = 50f,   // 👈 X coordinate
        y = 50f,   // 👈 Y coordinate
        scale = 1f,
        toggleable = true,
        description = "Shows a simple test message",
        module = this,
        block = {  // 👈 ADD THIS - the rendering block
            100f to 20f  // Return width and height
        }
    )

    init {
        // Register this HUD to be rendered
        HUDRenderer.addHUD(hud)
    }

    @SubscribeEvent(receiveCanceled = true)
    fun onChat(event: ChatPacketEvent) {
        if (event.message == "Welcome to Hypixel SkyBlock!"){
            // Hide the HUD by disabling it
            hud.value.enabled = false

            // Or you could disable the entire module:
            // this.enabled = false
        }
        else if (event.message == "You must be in a party to join the party channel!"){
            hud.value.enabled = true
        }
    }
}
