package io.github.vexo.utils.HUD

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.InputEvent
import net.minecraftforge.common.MinecraftForge
import org.lwjgl.input.Keyboard

// Simple HUD Edit GUI
class HUDEditScreen : GuiScreen() {

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        // Draw semi-transparent background
        drawRect(0, 0, width, height, 0x80000000.toInt())

        // Draw instructions
        val instructions = listOf(
            "HUD Edit Mode - Press ESC to exit",
            "Click and drag HUD elements to move them",
            "All HUDs are visible for editing"
        )

        var yOffset = 10
        for (instruction in instructions) {
            fontRendererObj.drawString(instruction, 10, yOffset, 0xFFFFFF)
            yOffset += 12
        }

        // Render all HUDs in edit mode
        HUDRenderer.renderInEditMode(mouseX, mouseY)

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
        if (Keyboard.isKeyDown(Keyboard.KEY_H)) {
            toggleEditMode()
        }
    }
}