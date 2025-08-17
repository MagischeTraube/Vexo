package io.github.vexo.utils.skyblock

import io.github.vexo.Vexo.Companion.mc
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.Gui.drawRect
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.inventory.IInventory
import java.awt.Color
import java.awt.Point

fun GuiContainer.getGuiTitle(): String? {
    val inv = this.inventorySlots.inventorySlots.firstOrNull()?.inventory
    if (inv is IInventory) {
        return if (inv.hasCustomName()) {
            inv.name
        } else {
            inv.displayName.unformattedText
        }
    }
    return null
}

fun getGuixySize(gui: GuiContainer): Point {
    val minX = gui.inventorySlots.inventorySlots.minOf { it.xDisplayPosition }
    val minY = gui.inventorySlots.inventorySlots.minOf { it.yDisplayPosition }

    val maxX = gui.inventorySlots.inventorySlots.maxOf { it.xDisplayPosition }
    val maxY = gui.inventorySlots.inventorySlots.maxOf { it.yDisplayPosition }

    val xSize = maxX - minX + 16
    val ySize = maxY - minY + 16
    return Point(xSize, ySize)
}

fun getGuiTopLeft(gui: GuiContainer): Point {
    val minX = gui.inventorySlots.inventorySlots.minOf { it.xDisplayPosition }
    val minY = gui.inventorySlots.inventorySlots.minOf { it.yDisplayPosition }
    val xySize = getGuixySize(gui)
    val offsetY = 9

    val guiLeft = (gui.width - xySize.x) / 2 - minX
    val guiTop = (gui.height - xySize.y + offsetY) / 2 - minY
    return Point(guiLeft, guiTop)
}

fun getSlotPosition(gui: GuiContainer, slotIndex: Int): Point? {
    val slot = gui.inventorySlots.getSlot(slotIndex) ?: return null
    val guiPosition = getGuiTopLeft(gui)

    val x = guiPosition.x + slot.xDisplayPosition
    val y = guiPosition.y + slot.yDisplayPosition

    return Point(x, y)
}

fun highlightSlot(gui: GuiContainer, slotIndex: Int, color: Color, aboveItem: Boolean = false) {
    val pos = getSlotPosition(gui, slotIndex) ?: return

    val z = if (aboveItem) 350f else 200f

    GlStateManager.pushMatrix()

    GlStateManager.translate(pos.x.toDouble(), pos.y.toDouble(), z.toDouble())

    drawRect(0, 0, 16, 16, color.rgb)

    GlStateManager.popMatrix()
}

fun recolorSlot(gui: GuiContainer, slotIndex: Int, color: Color, aboveItem: Boolean = false) {
    val pos = getSlotPosition(gui, slotIndex) ?: return

    val z = if (aboveItem) 300f else 150f

    GlStateManager.pushMatrix()

    GlStateManager.translate((pos.x-1).toDouble(), (pos.y-1).toDouble(), z.toDouble())

    drawRect(0, 0, 18, 18, color.rgb)

    GlStateManager.popMatrix()
}

fun writeAboveSlot(gui: GuiContainer, slotIndex: Int, text: String, color: Color) {
    val xySize = getGuixySize(gui)
    val guiPos = getGuiTopLeft(gui)
    val slotPos = getSlotPosition(gui, slotIndex) ?: return
    val font: FontRenderer = mc.fontRendererObj

    val textWidth = font.getStringWidth(text)
    var textX = slotPos.x + 8 - textWidth / 2
    var textY = slotPos.y - font.FONT_HEIGHT - 4

    if ((textX - 3) < guiPos.x) textX = guiPos.x + 11
    if ((textX + textWidth + 3) > (guiPos.x + xySize.x)) textX = guiPos.x + xySize.x - textWidth + 5

    GlStateManager.pushMatrix()
    GlStateManager.translate(0.0, 0.0, 400.0)

    font.drawString(text, textX, textY, color.rgb)

    GlStateManager.popMatrix()
}

fun writeBelowSlot(gui: GuiContainer, slotIndex: Int, text: String, color: Color) {
    val xySize = getGuixySize(gui)
    val guiPos = getGuiTopLeft(gui)
    val slotPos = getSlotPosition(gui, slotIndex) ?: return
    val font: FontRenderer = mc.fontRendererObj

    val textWidth = font.getStringWidth(text)
    var textX = slotPos.x + 8 - textWidth / 2
    var textY = slotPos.y + 16 + 6

    if ((textX - 3) < guiPos.x) textX = guiPos.x + 11
    if ((textX + textWidth + 3) > (guiPos.x + xySize.x)) textX = guiPos.x + xySize.x - textWidth + 5

    GlStateManager.pushMatrix()
    GlStateManager.translate(0.0, 0.0, 400.0)

    font.drawString(text, textX, textY, color.rgb)

    GlStateManager.popMatrix()
}