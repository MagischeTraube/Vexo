package io.github.vexo.features.dungeons

import net.minecraft.command.CommandBase
import net.minecraft.command.CommandException
import net.minecraft.command.ICommandSender
import net.minecraft.util.ChatComponentText
import java.util.*

class SuckTrap : CommandBase() {
    override fun getCommandName(): String {
        return "sucktrap"
    }

    override fun getCommandUsage(sender: ICommandSender?): String {
        return ""
    }

    @Throws(CommandException::class)
    override fun processCommand(sender: ICommandSender?, args: Array<String?>?) {

        val message = "test"

        sender?.addChatMessage(ChatComponentText("§7[Print] §f$message"))
    }

    override fun canCommandSenderUseCommand(sender: ICommandSender?): Boolean {
        return true
    }

    override fun getCommandAliases(): MutableList<String?> {
        return Arrays.asList("dontcrashme")
    }
}