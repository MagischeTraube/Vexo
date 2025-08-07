package io.github.vexo.features.dungeons

import io.github.vexo.utils.skyblock.sendCommand
import net.minecraft.command.CommandBase
import net.minecraft.command.CommandException
import net.minecraft.command.ICommandSender
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.util.*

var tyfr = false
val TyfrTrigger = listOf(
    Regex("<[^>]+>\\s*Score:.*:")
)

class Tyfr : CommandBase() {

    var msgDelay = 0

    override fun getCommandName(): String {
        return "tyfr"
    }

    override fun getCommandUsage(sender: ICommandSender?): String {
        return ""
    }

    @Throws(CommandException::class)
    override fun processCommand(sender: ICommandSender?, args: Array<String?>?) {
        tyfr = true
        msgDelay = 5
    }
    override fun canCommandSenderUseCommand(sender: ICommandSender?): Boolean {
        return true
    }

    override fun getCommandAliases(): MutableList<String?> {
        return Arrays.asList()
    }


    @SubscribeEvent
    fun onChat(event: ClientChatReceivedEvent) {
        if (TyfrTrigger.any { it.containsMatchIn(event.message.getFormattedText()) } && tyfr) {
            sendCommand("p leave")
            if (msgDelay > 0) msgDelay--
            else {
                sendCommand("ac tyfr o/")
            }
        }
    }



}