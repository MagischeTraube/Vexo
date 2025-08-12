package io.github.vexo.features.dungeons

import io.github.vexo.config.*
import net.minecraftforge.client.event.RenderLivingEvent
import net.minecraft.entity.passive.EntitySheep
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.event.entity.living.LivingEvent

object HideMageSheep : Module (
    name = "Hide Mage Sheep",
    description = "Hides Mage Sheep in Dungeons",
    category = "Dungeons"
){
    @SubscribeEvent
    fun onEntityRender(event: RenderLivingEvent.Pre<*,>){
        val entity = event.entity as? EntitySheep ?: return
        event.isCanceled = true
    }

    @SubscribeEvent
    fun onPosUpdate(event: LivingEvent.LivingUpdateEvent) {
        val entity = event.entity as? EntitySheep ?: return
        event.entity.posX = 9999999.0
        event.isCanceled = true
    }
}