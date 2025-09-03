package io.github.vexo.utils.HUD // Change this to match your actual package

import io.github.vexo.config.Module // Change this to match your Module location
import io.github.vexo.utils.HUD.TestHUD
import kotlin.reflect.KProperty
import net.minecraft.client.Minecraft
import io.github.vexo.Vexo.Companion.mc

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
    scale: Float,
    val toggleable: Boolean,
    val description: String,
    val module: Module
) {

    init {
        HUDRenderer.addHUD(this)
    }

    val font = mc.fontRendererObj
    val height = (font.FONT_HEIGHT).toFloat()
    val width = (font.getStringWidth(name)).toFloat()
    val block = width to height
    val value = HudElement(x, y, scale, true) { _ -> block }


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

            // Apply scaling and draw the HUD content
            net.minecraft.client.renderer.GlStateManager.pushMatrix()
            net.minecraft.client.renderer.GlStateManager.scale(value.scale, value.scale, 1.0f)

            val scaledX = (value.x / value.scale).toInt()
            val scaledY = (value.y / value.scale).toInt()

            mc.fontRendererObj.drawString(
                name,
                scaledX,
                scaledY,
                0xFFFFFF // White color
            )

            net.minecraft.client.renderer.GlStateManager.popMatrix()

            // Update actual dimensions after scaling
            lastWidth *= value.scale
            lastHeight *= value.scale

            width to height
        } else {
            0f to 0f
        }
    }

    // Special render function for edit mode
    fun renderInEditMode(isSelected: Boolean): Pair<Number, Number> {
        // Get Minecraft instance
        val mc = Minecraft.getMinecraft()

        // Call the user's drawing function and get dimensions
        val (width, height) = value.draw(false)
        lastWidth = width.toFloat()
        lastHeight = height.toFloat()

        // Apply scaling and draw the HUD content
        net.minecraft.client.renderer.GlStateManager.pushMatrix()
        net.minecraft.client.renderer.GlStateManager.scale(value.scale, value.scale, 1.0f)

        val scaledX = (value.x / value.scale).toInt()
        val scaledY = (value.y / value.scale).toInt()

        mc.fontRendererObj.drawString(
            name,
            scaledX,
            scaledY,
            0xFFFFFF // White color
        )

        net.minecraft.client.renderer.GlStateManager.popMatrix()

        // Update actual dimensions after scaling
        lastWidth *= value.scale
        lastHeight *= value.scale

        // Draw border in edit mode
        drawEditBorder(isSelected)

        return width to height
    }

    private fun drawEditBorder(isSelected: Boolean = false) {
        // Choose border color based on selection
        val borderColor = if (isSelected) 0xFF00FF00.toInt() else 0xFFFFFFFF.toInt() // Green if selected, white otherwise

        val x1 = value.x.toInt() - 3
        val y1 = value.y.toInt() - 3
        val x2 = (value.x + lastWidth).toInt() + 3
        val y2 = (value.y + lastHeight).toInt() + 3

        // Draw border
        val borderWidth = 1

        net.minecraft.client.gui.Gui.drawRect(x1, y1, x2, y1 + borderWidth, borderColor) // Top (thicker if selected)
        net.minecraft.client.gui.Gui.drawRect(x1, y2 - borderWidth, x2, y2, borderColor) // Bottom
        net.minecraft.client.gui.Gui.drawRect(x1, y1, x1 + borderWidth, y2, borderColor) // Left
        net.minecraft.client.gui.Gui.drawRect(x2 - borderWidth, y1, x2, y2, borderColor) // Right
    }

    // Check if mouse is over this HUD
    fun isMouseOver(mouseX: Float, mouseY: Float): Boolean {
        return mouseX >= value.x && mouseX <= value.x + lastWidth &&
                mouseY >= value.y && mouseY <= value.y + lastHeight
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
    fun handleMouseRelease(): Boolean {
        val wasDragging = isDragging
        isDragging = false
        return wasDragging // Return true if position might have changed
    }
}