package io.github.vexo.utils.skyblock

import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent

object TickDelay {
    private var ticks = 0
    private var task: (() -> Unit)? = null

    init { MinecraftForge.EVENT_BUS.register(this) }

    fun runLater(delay: Int, action: () -> Unit) {
        ticks = delay
        task = action
    }

    @SubscribeEvent
    fun onTick(e: TickEvent.ClientTickEvent) {
        if (e.phase == TickEvent.Phase.END && task != null && --ticks <= 0) {
            task!!.invoke()
            task = null
        }
    }
}
