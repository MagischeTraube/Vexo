package io.github.vexo.features.dungeons

import io.github.vexo.utils.skyblock.partyMessage
import io.github.vexo.utils.skyblock.sendCommand
import net.minecraft.command.CommandBase
import net.minecraft.command.CommandException
import net.minecraft.command.ICommandSender
import net.minecraft.util.ChatComponentText
import java.util.*
import kotlinx.coroutines.*

class Tyfr : CommandBase() {
    override fun getCommandName(): String {
        return "tyfr"
    }

    override fun getCommandUsage(sender: ICommandSender?): String {
        return ""
    }

    @Throws(CommandException::class)
    override fun processCommand(sender: ICommandSender?, args: Array<String?>?) {

        sendCommand("p leave")


    }

    override fun canCommandSenderUseCommand(sender: ICommandSender?): Boolean {
        return true
    }

    override fun getCommandAliases(): MutableList<String?> {
        return Arrays.asList()
    }
}