package io.github.vexo.features.dungeons


import io.github.vexo.events.ChatPacketEvent
import io.github.vexo.events.ServerTickEvent
import io.github.vexo.utils.skyblock.modMessage
import io.github.vexo.utils.skyblock.sendCommand
import net.minecraft.command.CommandBase
import net.minecraft.command.CommandException
import net.minecraft.command.ICommandSender
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.util.*

object tyfrData {
    var tyfr = false
    val TyfrTrigger = listOf(
        Regex("Score:"),
        Regex("Tokens Earned:")
    )
    var msgDelay = 0
    var EndOfRun = false
}

object Tyfr : CommandBase() {

    override fun getCommandName(): String {
        return "tyfr"
    }

    override fun getCommandUsage(sender: ICommandSender?): String {
        return ""
    }

    @Throws(CommandException::class)
    override fun processCommand(sender: ICommandSender?, args: Array<String?>?) {
        tyfrData.tyfr = !tyfrData.tyfr
        if (tyfrData.tyfr) {
            modMessage("TYFR activated! – waiting for the end of the run")
        } else {
            modMessage("TYFR deactivated!")
        }
    }

    override fun canCommandSenderUseCommand(sender: ICommandSender?): Boolean {
        return true
    }

    override fun getCommandAliases(): MutableList<String?> {
        return Arrays.asList("TYFR")
    }
}

object EndOfRun {
    @SubscribeEvent
    fun onChat(event: ChatPacketEvent) {
        if (tyfrData.tyfr && tyfrData.TyfrTrigger.any { it.containsMatchIn(event.message) }){
            tyfrData.EndOfRun = true
            sendCommand("p leave")
            tyfrData.msgDelay = 5
        }
    }

    @SubscribeEvent
    fun onServerTick(event: ServerTickEvent) {
        if (!tyfrData.tyfr || !tyfrData.EndOfRun) return
        if (tyfrData.msgDelay > 0) {
            tyfrData.msgDelay--
        }
        else {
            tyfrData.tyfr = false
            tyfrData.EndOfRun = false
            sendCommand("ac tyfr o/")
        }
    }
}