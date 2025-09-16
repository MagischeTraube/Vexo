package io.github.vexo.features.QOL

import io.github.vexo.utils.minecraft.MinecraftUUIDUtils
import io.github.vexo.utils.skyblock.modMessage
import net.minecraft.command.CommandBase
import net.minecraft.command.CommandException
import net.minecraft.command.ICommandSender
import net.minecraft.util.EnumChatFormatting
import kotlin.concurrent.thread

object UuidCommand : CommandBase() {

    override fun getCommandName(): String {
        return "uuid"
    }

    override fun getCommandUsage(sender: ICommandSender?): String {
        return "/uuid <username> [< or > username2]"
    }

    @Throws(CommandException::class)
    override fun processCommand(sender: ICommandSender?, args: Array<String?>?) {
        if (sender == null) return

        if (args == null || args.isEmpty() || args[0].isNullOrBlank()) {
            modMessage("${EnumChatFormatting.RED}Usage: /uuid <username> [< or > username2]")
            return
        }

        val username1 = args[0]!!.trim()

        // Check if there's a comparison (< or > symbol and second username)
        val isComparison = args.size >= 3 && (args[1] == "<" || args[1] == ">") && !args[2].isNullOrBlank()
        val username2 = if (isComparison) args[2]!!.trim() else null

        // Validate first username
        if (!username1.matches(Regex("^[a-zA-Z0-9_]{3,16}$"))) {
            modMessage("${EnumChatFormatting.RED}Invalid username format for '$username1'! Must be 3-16 characters, alphanumeric and underscores only.")
            return
        }

        // Validate second username if provided
        if (username2 != null && !username2.matches(Regex("^[a-zA-Z0-9_]{3,16}$"))) {
            modMessage("${EnumChatFormatting.RED}Invalid username format for '$username2'! Must be 3-16 characters, alphanumeric and underscores only.")
            return
        }

        if (isComparison) {
            modMessage("${EnumChatFormatting.YELLOW}Comparing UUIDs for $username1 and $username2...")
        } else {
            modMessage("${EnumChatFormatting.YELLOW}Looking up UUID for $username1...")
        }

        // Run in separate thread to avoid blocking the game
        thread {
            try {
                if (isComparison) {
                    // Compare two usernames
                    val uuid1 = MinecraftUUIDUtils.getUUID(username1)
                    val uuid2 = MinecraftUUIDUtils.getUUID(username2!!)

                    val aresSame = uuid1.equals(uuid2, ignoreCase = true)

                    if (aresSame) {
                        modMessage("${EnumChatFormatting.GREEN}true ${EnumChatFormatting.GRAY}($username1 and $username2 are the same player)")
                    } else {
                        modMessage("${EnumChatFormatting.RED}false ${EnumChatFormatting.GRAY}($username1 and $username2 are different players)")
                    }

                } else {
                    // Regular UUID lookup
                    val uuid = MinecraftUUIDUtils.getUUID(username1)
                    modMessage("${EnumChatFormatting.GREEN}UUID for $username1: ${EnumChatFormatting.AQUA}$uuid")
                }

            } catch (e: IllegalArgumentException) {
                if (isComparison) {
                    modMessage("${EnumChatFormatting.RED}One or both players not found!")
                } else {
                    modMessage("${EnumChatFormatting.RED}Player '$username1' not found!")
                }

            } catch (e: Exception) {
                modMessage("${EnumChatFormatting.RED}Failed to lookup UUID: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    override fun canCommandSenderUseCommand(sender: ICommandSender?): Boolean {
        return true
    }

    override fun getCommandAliases(): MutableList<String> {
        return mutableListOf("getuuid", "playeruuid")
    }
}