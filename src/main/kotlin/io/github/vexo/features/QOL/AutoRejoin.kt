package io.github.vexo.features.QOL

import io.github.vexo.config.*
import io.github.vexo.events.ChatPacketEvent
import io.github.vexo.utils.skyblock.*
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

private var rejoining = false
private val dtkickMessages = listOf(
    Regex("You were kicked while joining that server!")
)

object AutoRejoin : Module(
name = "Auto Rejoin on Kick",
description = "Automatically rejoins Skyblock after being kicked.",
category = "Test"
) {

    @SubscribeEvent
    fun onChat(event: ChatPacketEvent) {
        if (rejoining && event.message == "Welcome to Hypixel SkyBlock!"){
            rejoining = false
            partyMessage("I'm in Skyblock now")
            return
        } else if (dtkickMessages.any { it.containsMatchIn(event.message)} && !rejoining) {
            rejoining = true
            partyMessage("Kicked, trying to rejoining in 65 Seconds",)
            modMessage("65 Seconds until rejoin")
            Thread.sleep(30000)
            modMessage("30 Seconds until rejoin")
            Thread.sleep(35000)
            modMessage("Rejoining now")
            sendCommand("play skyblock", false)
        }
    }
}


/*


registerWhen(register("Chat", () => {
    if(!rejoining) return;
    else {
        ChatLib.command(`pc I'm in Skyblock now`, true);
        rejoining = false;
    }
}).setCriteria("Welcome to Hypixel SkyBlock!"), () => config.rejoin)*/