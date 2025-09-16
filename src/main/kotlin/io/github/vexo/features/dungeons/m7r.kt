package io.github.vexo.features.dungeons

import io.github.vexo.utils.skyblock.partyMessage
import net.minecraft.command.CommandBase
import net.minecraft.command.CommandException
import net.minecraft.command.ICommandSender

object M7r : CommandBase() {

    private const val TANK = "TANK: ss / 1 3 / 2 dev / 1"
    private const val ARCH = "ARCH: bl ee2 i2 / 4 3 / 4 bl / 3"
    private const val BERS = "BERS: i4 / 5 3 / 3 dev / bl"
    private const val MAGE = "MAGE: 2 1 / 2 / core / 4"
    private const val HEAL = "HEAL: 4 3 / bl ee3 / 1 bl / 2"

    override fun getCommandName(): String {
        return "m7r"
    }

    override fun getCommandUsage(sender: ICommandSender?): String {
        return "/m7r"
    }

    @Throws(CommandException::class)
    override fun processCommand(sender: ICommandSender?, args: Array<String?>?) {
        if (args == null || args.isEmpty() || args[0].isNullOrBlank()) {
            Thread {
                val roles = listOf(TANK, ARCH, BERS, MAGE, HEAL)

                for (role in roles) {
                    partyMessage(role)
                    Thread.sleep(230)
                }
            }.start()


        } else
            when (args[0]?.lowercase()) {
            "tank" -> partyMessage(TANK)
            "arch" -> partyMessage(ARCH)
            "bers" -> partyMessage(BERS)
            "mage" -> partyMessage(MAGE)
            "healer" -> partyMessage(HEAL)
        }
    }

    override fun canCommandSenderUseCommand(sender: ICommandSender?): Boolean {
        return true
    }

    override fun getCommandAliases(): MutableList<String?> {
        return mutableListOf("m7roles")

    }
}