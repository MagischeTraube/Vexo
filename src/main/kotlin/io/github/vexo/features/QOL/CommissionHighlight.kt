package io.github.vexo.features.QOL

import io.github.vexo.Vexo.Companion.mc
import io.github.vexo.config.ColorSetting
import io.github.vexo.config.Module
import io.github.vexo.utils.skyblock.getGuiTitle
import io.github.vexo.utils.skyblock.getItemLore
import io.github.vexo.utils.skyblock.highlightSlot
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.item.ItemStack
import net.minecraftforge.client.event.GuiScreenEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.awt.Color

object CommissionHighlight : Module(
    name = "Commission Highlight",
    description = "Highlights completed commissions.",
    category = "QOL"
) {
    private val customHighlightColor = registerSetting(ColorSetting("Highlight Color", Color(57, 204, 35)))

    val gui_title = "Commissions"
    @SubscribeEvent
    fun onGuiRender(event: GuiScreenEvent.DrawScreenEvent.Pre) {
        val gui = mc.currentScreen as? GuiContainer ?: return
        val title = gui.getGuiTitle() ?: return

        if (gui_title == title) {
            forEachSlot(gui) { slot, stack ->
                val item_lore = stack?.getItemLore() ?: return
                if ("COMPLETED" in item_lore) {
                    highlightSlot(gui, slot.slotNumber, customHighlightColor.value)
                }
            }

        }
    }

    private inline fun forEachSlot(
        gui: GuiContainer,
        action: (slot: net.minecraft.inventory.Slot, stack: ItemStack?) -> Unit
    ) {
        val maxSlot = gui.inventorySlots.inventory.size - 37
        for (slot in gui.inventorySlots.inventorySlots) {
            if (slot.slotNumber <= maxSlot) {
                val stack = slot.stack
                action(slot, stack)
            }
        }
    }
}