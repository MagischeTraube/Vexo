package io.github.vexo.utils.HUD // Change to match your package

import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.common.MinecraftForge

object HUDRenderer {

    // List of all HUDs to render
    private val huds = mutableListOf<HUDSetting>()

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
            // Don't render in edit mode (edit screen handles this)
            if (!HUDEditManager.editMode) {
                for (hud in huds) {
                    hud.render()
                }
            }
        }
    }

    // Render HUDs in edit mode (called from HUDEditScreen)
    fun renderInEditMode(mouseX: Int, mouseY: Int) {
        for (hud in huds) {
            // Force render all HUDs in edit mode (even disabled ones)
            val originalEnabled = hud.value.enabled
            hud.value.enabled = true
            hud.render()
            hud.value.enabled = originalEnabled
        }
    }

    // Handle mouse clicks in edit mode
    fun handleMouseClick(mouseX: Float, mouseY: Float, mouseButton: Int) {
        for (hud in huds) {
            hud.handleMouseClick(mouseX, mouseY, mouseButton)
        }
    }

    // Handle mouse dragging in edit mode
    fun handleMouseDrag(mouseX: Float, mouseY: Float) {
        for (hud in huds) {
            hud.handleMouseDrag(mouseX, mouseY)
        }
    }

    // Handle mouse release in edit mode
    fun handleMouseRelease() {
        for (hud in huds) {
            hud.handleMouseRelease()
        }
    }
}