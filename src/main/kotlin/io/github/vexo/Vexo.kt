package io.github.vexo

import io.github.vexo.features.PrintTest
import io.github.vexo.features.chat.ChatCleaner
import io.github.vexo.features.dungeons.HideMageSheep
import net.minecraftforge.client.ClientCommandHandler
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent


@Mod(modid = "vexo", useMetadata = true)
class Vexo {
    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        MinecraftForge.EVENT_BUS.register(MyEventHandlerClass())
        MinecraftForge.EVENT_BUS.register(HideMageSheep())
        MinecraftForge.EVENT_BUS.register(ChatCleaner())

        ClientCommandHandler.instance.registerCommand(PrintTest())
        MinecraftForge.EVENT_BUS.register(this)
    }
}
