package io.github.vexo.utils.dungeon

import io.github.vexo.events.ServerTickEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraft.client.Minecraft
import net.minecraft.scoreboard.ScorePlayerTeam
import net.minecraft.scoreboard.Scoreboard
import net.minecraft.util.ChatComponentText

@Mod(modid = "scoreboarddebug", name = "Scoreboard Debug", version = "1.0")
class ScoreboardDebug {

    private var ticks = 0

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        MinecraftForge.EVENT_BUS.register(this)
    }

    @SubscribeEvent
    fun onServerTick(event: ServerTickEvent) {
        val mc = Minecraft.getMinecraft()
        ticks++
        if (mc.theWorld != null && mc.thePlayer != null && ticks >= 20) { // 1 Sekunde
            ticks = 0
            printScoreboard(mc.theWorld.scoreboard)
        }
    }

    private fun printScoreboard(scoreboard: Scoreboard) {
        val mc = Minecraft.getMinecraft()
        val objective = scoreboard.getObjectiveInDisplaySlot(1) ?: return // Sidebar
        val scores = scoreboard.getSortedScores(objective)

        mc.thePlayer.addChatMessage(ChatComponentText("§6--- Scoreboard ---"))
        for (score in scores) {
            val team = scoreboard.getPlayersTeam(score.playerName)
            val line = ScorePlayerTeam.formatPlayerName(team, score.playerName)
            mc.thePlayer.addChatMessage(ChatComponentText("§7$line: §a${score.scorePoints}"))
        }
    }
}
