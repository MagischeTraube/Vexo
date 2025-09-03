package io.github.vexo.utils.HUD

import io.github.vexo.config.Module

// Minimal HUD element that just stores position, scale, and rendering function
class HudElement(
    var x: Float,
    var y: Float,
    var scale: Float,
    var enabled: Boolean = true,
    val draw: (Boolean) -> Pair<Number, Number>
)

// Minimal HUDSetting that just creates and stores a HudElement
class HUDSetting(
    val name: String,
    val x: Float,
    val y: Float,
    val scale: Float,
    val toggleable: Boolean,
    val description: String,
    val module: Module,
    val block: (Boolean) -> Pair<Number, Number>
) {
    val value = HudElement(x, y, scale, true, block)

    // This is what gets called when your HUD renders
    // Called when HUD should render
    fun render(): Pair<Number, Number> {
        return if (module.enabled && value.enabled) {
            // Get Minecraft instance
            val mc = net.minecraft.client.Minecraft.getMinecraft()

            // Call the user's drawing function and get dimensions
            val (width, height) = value.draw(false)

            // For testing, draw some text at the HUD position
            mc.fontRendererObj.drawString(
                name,
                value.x.toInt(),
                value.y.toInt(),
                0xFFFFFF // White color
            )

            width to height
        } else {
            0f to 0f
        }
    }
}