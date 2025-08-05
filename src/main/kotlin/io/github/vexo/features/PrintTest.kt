package io.github.vexo.features

import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.util.ChatComponentText
import net.minecraft.command.CommandException
import java.util.*

class PrintTest : CommandBase() {
    override fun getCommandName(): String {
        return "printtest"
    }

    override fun getCommandUsage(sender: ICommandSender): String {
        return ""
    }

    @Throws(CommandException::class)
    override fun processCommand(sender: ICommandSender, args: Array<String>) {
        val message = "test"

        sender.addChatMessage(ChatComponentText("§7[Print] §f$message"))
    }

    override fun canCommandSenderUseCommand(sender: ICommandSender): Boolean {
        return true // Erlaubt allen Spielern, den Befehl zu nutzen
    }

    override fun getCommandAliases(): MutableList<String?> {
        return Arrays.asList("test")
    }

}