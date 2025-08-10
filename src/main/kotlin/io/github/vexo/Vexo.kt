package io.github.vexo

import io.github.vexo.features.PrintTest
import io.github.vexo.features.chat.ChatCleaner
import io.github.vexo.features.dungeons.HideMageSheep
import io.github.vexo.features.dungeons.RagAxeNow
import io.github.vexo.features.dungeons.Tyfr
import io.github.vexo.features.dungeons.EndOfRun
import net.minecraft.client.Minecraft
import net.minecraft.command.ICommand
import net.minecraftforge.client.ClientCommandHandler
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent

fun register(handler: Any) {
    MinecraftForge.EVENT_BUS.register(handler)
}

fun cmd(command: ICommand) {
    ClientCommandHandler.instance.registerCommand(command)
}

@Mod(modid = Vexo.MOD_ID, useMetadata = true)
class Vexo {
    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        register(MyEventHandlerClass())
        register(HideMageSheep())
        register(ChatCleaner())
        register(RagAxeNow())
        register(EndOfRun())

        cmd(PrintTest())
        cmd(Tyfr())

        register(this)
    }

    companion object {
        const val MOD_ID = "vexo"

        @JvmStatic
        val mc : Minecraft = Minecraft.getMinecraft()
    }
}
