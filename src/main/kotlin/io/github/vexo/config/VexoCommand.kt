package io.github.vexo.config

import io.github.vexo.Vexo
import net.minecraft.command.CommandBase
import net.minecraft.command.CommandException
import net.minecraft.command.ICommandSender
import java.util.*


object VexoCommand : CommandBase() {

    override fun getCommandName(): String {
        return "vexo"
    }

    override fun getCommandUsage(sender: ICommandSender?): String {
        return "/vexo [update]"
    }

    @Throws(CommandException::class)
    override fun processCommand(sender: ICommandSender?, args: Array<String?>?) {
        if (args == null || args.isEmpty() || args[0].isNullOrBlank()) {
            Vexo.screenToOpenNextTick = VexoGui()

        } else if (args[0].equals("update", ignoreCase = true)) {
            vexoUpdater(true)

        } else {
            Vexo.screenToOpenNextTick = VexoGui()
        }
    }

    override fun canCommandSenderUseCommand(sender: ICommandSender?): Boolean {
        return true
    }

    override fun getCommandAliases(): MutableList<String?> {
        return Arrays.asList()
    }
}