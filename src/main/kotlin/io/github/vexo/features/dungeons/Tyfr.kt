package io.github.vexo.features.dungeons


import io.github.vexo.utils.skyblock.modMessage
import io.github.vexo.utils.skyblock.sendCommand
import net.minecraft.command.CommandBase
import net.minecraft.command.CommandException
import net.minecraft.command.ICommandSender
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import java.util.*

object tyfrData {
    var tyfr = false
    val TyfrTrigger = listOf(
        Regex("<[^>]+>\\sScore:.+:")
    )
    var msgDelay = 0
    var EndOfRun = false
}

class Tyfr : CommandBase() {

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
        return Arrays.asList()
    }
}

class EndOfRun {
    @SubscribeEvent
    fun onChat(event: ClientChatReceivedEvent) {
        if (event.message.getFormattedText().contains("Score") && tyfrData.tyfr) {
            tyfrData.EndOfRun = true
            sendCommand("p leave")
            tyfrData.msgDelay = 5
        }
    }

    @SubscribeEvent
    fun onServerTick(event: TickEvent.ServerTickEvent) {
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