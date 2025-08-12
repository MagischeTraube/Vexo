package io.github.vexo.features.dungeons

import io.github.vexo.config.*
import io.github.vexo.utils.skyblock.removeHitbox
import net.minecraftforge.client.event.RenderLivingEvent
import net.minecraft.entity.passive.EntitySheep
import net.minecraft.util.AxisAlignedBB
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.event.entity.living.LivingEvent

object HideMageSheep : Module (
    name = "Hide Mage Sheep",
    description = "Hides Mage Sheep in Dungeons",
    category = "Dungeons"
){
    @SubscribeEvent
    fun onEntityRender(event: RenderLivingEvent.Pre<*,>) {
        val sheep = event.entity as? EntitySheep ?: return
        sheep.removeHitbox()
        event.isCanceled = true
    }

    @SubscribeEvent
    fun onPosUpdate(event: LivingEvent.LivingUpdateEvent) {
        val entity = event.entity as? EntitySheep ?: return
        event.isCanceled = true
    }
}