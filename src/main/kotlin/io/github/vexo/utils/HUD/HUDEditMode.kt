package io.github.vexo.utils.HUD

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.InputEvent
import net.minecraftforge.common.MinecraftForge
import org.lwjgl.input.Keyboard

// Simple HUD Edit GUI
class HUDEditScreen : GuiScreen() {

    private var hoveredHUD: HUDSetting? = null

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        // Draw semi-transparent background
        drawRect(0, 0, width, height, 0x80000000.toInt())

        // Draw instructions
        val instructions = listOf(
            "HUD Edit Mode - Press ESC to exit",
            "Click and drag HUD elements to move them",
            "Scroll wheel to resize selected HUD",
            "Hover over HUD to see module info"
        )

        var yOffset = 10
        for (instruction in instructions) {
            fontRendererObj.drawString(instruction, 10, yOffset, 0xFFFFFF)
            yOffset += 12
        }

        // Update hovered HUD
        hoveredHUD = HUDRenderer.getHoveredHUD(mouseX.toFloat(), mouseY.toFloat())

        // Render all HUDs in edit mode
        HUDRenderer.renderInEditMode(mouseX, mouseY, hoveredHUD)

        // Draw tooltip for hovered HUD
        hoveredHUD?.let { hud ->
            drawHoveringText(
                listOf(
                    "Module: ${hud.module.name}",
                    "Position: (${hud.value.x.toInt()}, ${hud.value.y.toInt()})",
                    "Scale: ${String.format("%.1f", hud.value.scale)}x"
                ),
                mouseX +10, mouseY
            )
        }

        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        // Handle HUD dragging
        HUDRenderer.handleMouseClick(mouseX.toFloat(), mouseY.toFloat(), mouseButton)
        super.mouseClicked(mouseX, mouseY, mouseButton)
    }

    override fun mouseReleased(mouseX: Int, mouseY: Int, state: Int) {
        HUDRenderer.handleMouseRelease()
        super.mouseReleased(mouseX, mouseY, state)
    }

    override fun mouseClickMove(mouseX: Int, mouseY: Int, clickedMouseButton: Int, timeSinceLastClick: Long) {
        HUDRenderer.handleMouseDrag(mouseX.toFloat(), mouseY.toFloat())
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick)
    }

    override fun handleMouseInput() {
        super.handleMouseInput()

        // Handle scroll wheel for scaling
        val scroll = org.lwjgl.input.Mouse.getEventDWheel()
        if (scroll != 0 && hoveredHUD != null) {
            val hud = hoveredHUD!!
            val scaleChange = if (scroll > 0) 0.1f else -0.1f
            val newScale = (hud.value.scale + scaleChange).coerceIn(0.5f, 3.0f)
            hud.value.scale = newScale

            // Save the scale change
            HUDConfigManager.saveHUDPosition(hud)
        }
    }

    override fun doesGuiPauseGame(): Boolean = false

    override fun onGuiClosed() {
        HUDEditManager.editMode = false
        super.onGuiClosed()
    }
}

// HUD Edit Manager
object HUDEditManager {
    var editMode = false

    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    fun toggleEditMode() {
        val mc = Minecraft.getMinecraft()
        if (!editMode) {
            mc.displayGuiScreen(HUDEditScreen())
            editMode = true
        } else {
            mc.displayGuiScreen(null)
            editMode = false
        }
    }

    // Handle key presses for HUD edit mode
    @SubscribeEvent
    fun onKeyInput(event: InputEvent.KeyInputEvent) {
        // Press Shift + H to toggle HUD edit mode
        if (Keyboard.isKeyDown(Keyboard.KEY_H) && Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            toggleEditMode()
        }
    }
}