package io.github.vexo.features.QOL

import io.github.vexo.config.*
import net.minecraft.client.audio.ISound
import net.minecraftforge.client.event.sound.PlaySoundEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent


object MuteWalkingSounds : Module(
    name = "Mute Walking Sounds",
    description = "Prevents walking sounds from being played.",
    category = "QOL"
) {
    private val walkingSounds = listOf(
        "step.cloth",
        "step.grass",
        "step.gravel",
        "step.ladder",
        "step.sand",
        "step.snow",
        "step.stone",
        "step.wood"
    )

    @SubscribeEvent
    fun onChat(event: PlaySoundEvent) {
        val sound: ISound = event.sound ?: return

        if (walkingSounds.any { sound.soundLocation.toString().contains(it) }) {
            event.result = null
        }
    }
}
