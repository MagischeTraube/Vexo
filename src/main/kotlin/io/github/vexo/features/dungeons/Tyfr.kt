package io.github.vexo.features.dungeons


import io.github.vexo.utils.skyblock.modMessage
import io.github.vexo.utils.skyblock.sendCommand
import net.minecraft.command.CommandBase
import net.minecraft.command.CommandException
import net.minecraft.command.ICommandSender
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.util.*
object tyfrData {
    var tyfr = false
    val TyfrTrigger = listOf(
        Regex("<[^>]+>\\s*Score:.*:")
    )
    var msgDelay = 0
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
        tyfrData.tyfr = true
        tyfrData.msgDelay = 5
        modMessage("TYFR activated! – waiting for the end of the run")}

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
        if (tyfrData.TyfrTrigger.any { it.containsMatchIn(event.message.getFormattedText()) } && tyfrData.tyfr) {
            sendCommand("p leave")
            if (tyfrData.msgDelay > 0) tyfrData.msgDelay--
            else {
                sendCommand("ac tyfr o/")
            }
        }
    }
}