package io.github.vexo.utils.HUD // Change to match your package

import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.common.MinecraftForge

object HUDRenderer {

    // List of all HUDs to render
    private val huds = mutableListOf<io.github.vexo.utils.HUD.HUDSetting>()

    init {
        // Register this object to receive events
        MinecraftForge.EVENT_BUS.register(this)
    }

    // Add a HUD to be rendered
    fun addHUD(hud: HUDSetting) {
        huds.add(hud)
    }

    // This runs every frame when the game overlay is being rendered
    @SubscribeEvent
    fun onRenderOverlay(event: RenderGameOverlayEvent.Post) {
        // Only render during the TEXT phase (after everything else)
        if (event.type == RenderGameOverlayEvent.ElementType.TEXT) {
            // Render all registered HUDs
            for (hud in huds) {
                hud.render()
            }
        }
    }
}