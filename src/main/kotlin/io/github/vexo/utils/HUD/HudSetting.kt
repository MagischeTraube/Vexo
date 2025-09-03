package io.github.vexo.utils.HUD // Change this to match your actual package

import io.github.vexo.config.Module // Change this to match your Module location
import kotlin.reflect.KProperty
import net.minecraft.client.Minecraft

// Simple HUD element that stores position, scale, and rendering logic
class HudElement(
    var x: Float,
    var y: Float,
    var scale: Float,
    var enabled: Boolean = true,
    val draw: (Boolean) -> Pair<Number, Number>
)

// HUDSetting that works as a property delegate
class HUDSetting(
    val name: String,
    x: Float,
    y: Float,
    val scale: Float,
    val toggleable: Boolean,
    val description: String,
    val module: Module,
    val block: (Boolean) -> Pair<Number, Number>
) {
    val value = HudElement(x, y, scale, true, block)

    // Dragging variables
    private var isDragging = false
    private var dragOffsetX = 0f
    private var dragOffsetY = 0f
    private var lastWidth = 100f
    private var lastHeight = 20f

    // These functions make it work as a delegate (for the 'by' keyword)
    operator fun getValue(thisRef: Any?, property: KProperty<*>): HUDSetting = this
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: HUDSetting) {}

    // Called when HUD should render
    fun render(): Pair<Number, Number> {
        return if (module.enabled && value.enabled) {
            // Get Minecraft instance
            val mc = Minecraft.getMinecraft()

            // Call the user's drawing function and get dimensions
            val (width, height) = value.draw(false)
            lastWidth = width.toFloat()
            lastHeight = height.toFloat()

            // Draw the HUD content
            mc.fontRendererObj.drawString(
                name,
                value.x.toInt(),
                value.y.toInt(),
                0xFFFFFF // White color
            )

            // Draw border in edit mode
            if (HUDEditManager.editMode) {
                drawEditBorder()
            }

            width to height
        } else {
            0f to 0f
        }
    }

    private fun drawEditBorder() {
        val mc = Minecraft.getMinecraft()
        // Draw a border around the HUD in edit mode
        val x1 = value.x.toInt() - 2
        val y1 = value.y.toInt() - 2
        val x2 = (value.x + lastWidth).toInt() + 2
        val y2 = (value.y + lastHeight).toInt() + 2

        // Draw border (you might need to adjust this based on your rendering system)
        net.minecraft.client.gui.Gui.drawRect(x1, y1, x2, y1 + 1, 0xFFFFFFFF.toInt()) // Top
        net.minecraft.client.gui.Gui.drawRect(x1, y2 - 1, x2, y2, 0xFFFFFFFF.toInt()) // Bottom
        net.minecraft.client.gui.Gui.drawRect(x1, y1, x1 + 1, y2, 0xFFFFFFFF.toInt()) // Left
        net.minecraft.client.gui.Gui.drawRect(x2 - 1, y1, x2, y2, 0xFFFFFFFF.toInt()) // Right
    }

    // Handle mouse click for dragging
    fun handleMouseClick(mouseX: Float, mouseY: Float, mouseButton: Int) {
        if (mouseButton == 0) { // Left mouse button
            // Check if mouse is over this HUD
            if (mouseX >= value.x && mouseX <= value.x + lastWidth &&
                mouseY >= value.y && mouseY <= value.y + lastHeight) {

                isDragging = true
                dragOffsetX = mouseX - value.x
                dragOffsetY = mouseY - value.y
            }
        }
    }

    // Handle mouse drag
    fun handleMouseDrag(mouseX: Float, mouseY: Float) {
        if (isDragging) {
            val mc = Minecraft.getMinecraft()

            // Update position while dragging
            value.x = mouseX - dragOffsetX
            value.y = mouseY - dragOffsetY

            // Keep HUD on screen
            val screenWidth = mc.displayWidth / mc.gameSettings.guiScale.coerceAtLeast(1)
            val screenHeight = mc.displayHeight / mc.gameSettings.guiScale.coerceAtLeast(1)

            value.x = value.x.coerceIn(0f, (screenWidth - lastWidth).coerceAtLeast(0f))
            value.y = value.y.coerceIn(0f, (screenHeight - lastHeight).coerceAtLeast(0f))
        }
    }

    // Handle mouse release
    fun handleMouseRelease() {
        isDragging = false
    }
}