package io.github.vexo

import io.github.vexo.config.*
import io.github.vexo.features.*
import io.github.vexo.features.QOL.*
import io.github.vexo.utils.dungeon.*
import io.github.vexo.features.chat.*
import io.github.vexo.features.dungeons.*
import io.github.vexo.events.EventTrigger
import io.github.vexo.utils.skyblock.PriceUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.client.ClientCommandHandler
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent

@Mod(modid = Vexo.MOD_ID, version = Vexo.VERSION, useMetadata = true)
class Vexo {
    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {

        listOf(
            EventTrigger, EndOfRun, checkForUpdateOnStartup, PriceUtils
        ).forEach { MinecraftForge.EVENT_BUS.register(it) }


        /**
         * Commands
        */
        listOf(
            Tyfr, VexoCommand, PrintTest, SuckTrap
        ).forEach { ClientCommandHandler.instance.registerCommand(it) }


        /**
         * Features
         */
        val FEATURES = listOf(
            RagAxeNow, ChatCleaner, HideMageSheep, AnnounceClass, ProfitTracker, AutoRejoin, MuteWalkingSounds, PadTimer,
            SuperboomGrabber
        )
        ModuleManager.register(FEATURES)

        MinecraftForge.EVENT_BUS.register(this)

        ConfigManager.load()
    }

    @SubscribeEvent
    fun onTick(event: ClientTickEvent) {
        if (event.phase == TickEvent.Phase.END) return
        if (screenToOpenNextTick != null) {
            Minecraft.getMinecraft().displayGuiScreen(screenToOpenNextTick)
            screenToOpenNextTick = null
        }
    }

    companion object {
        const val MOD_ID = "vexo"
        const val VERSION = "1.0.0"
        const val VERSION_NUMBER = 1

        @JvmStatic
        val mc : Minecraft = Minecraft.getMinecraft()

        @JvmStatic
        var screenToOpenNextTick: GuiScreen? = null

    }
}
