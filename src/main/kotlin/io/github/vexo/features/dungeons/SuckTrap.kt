package io.github.vexo.features.dungeons

import io.github.vexo.utils.skyblock.partyMessage
import net.minecraft.command.CommandBase
import net.minecraft.command.CommandException
import net.minecraft.command.ICommandSender
import java.util.*



object SuckTrap : CommandBase() {
    override fun getCommandName(): String {
        return "sucktrap"
    }

    override fun getCommandUsage(sender: ICommandSender?): String {
        return ""
    }

    @Throws(CommandException::class)
    override fun processCommand(sender: ICommandSender?, args: Array<String?>?) {
        partyMessage("please learn how to do Trap properly... https://youtu.be/JUnrH_AJ5Nc?si=8FVgBTAO7EvG7Xro")
    }

    override fun canCommandSenderUseCommand(sender: ICommandSender?): Boolean {
        return true
    }

    override fun getCommandAliases(): MutableList<String?> {
        return Arrays.asList()
    }
}