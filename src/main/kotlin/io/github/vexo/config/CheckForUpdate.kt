package io.github.vexo.config
import moe.nea.libautoupdate.*
import io.github.vexo.Vexo
import io.github.vexo.Vexo.Companion.VERSION_NUMBER
import io.github.vexo.events.ServerTickEvent
import io.github.vexo.utils.skyblock.modMessage
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object checkForUpdateOnStartup {
    @SubscribeEvent
    fun onTick(event: ServerTickEvent) {
        vexoUpdater(false)
        MinecraftForge.EVENT_BUS.unregister(this)
    }
}

fun vexoUpdater(sendNoUpdate: Boolean) {

    Thread {
        val updated = checkForUpdate()
        if (updated) {
            modMessage("§aUpdate downloaded. Please restart your game.")
        } else {
            if (sendNoUpdate) {
                modMessage("§cNo Update Available")
            }
        }
    }.start()
}

fun checkForUpdate(): Boolean {
    val updateContext = UpdateContext(
        UpdateSource.gistSource("MagischeTraube", "1ee18188e051e5e019d7ac51f90522a4"),
        UpdateTarget.deleteAndSaveInTheSameFolder(Vexo::class.java),
        CurrentVersion.of(VERSION_NUMBER),
        "VexoUpdateCheck"
    )

    return try {
        val newVersion = updateContext.checkUpdate("VexoCurrentVersion")
        val potentialUpdate = newVersion.get()
        if (potentialUpdate.isUpdateAvailable) {
            modMessage("§aNew Version found: ${potentialUpdate.update.versionName}")
            //potentialUpdate.launchUpdate()
            true
        } else {
            false
        }
    } catch (ex: Exception) {
        ex.printStackTrace()
        modMessage("§4Update check failed: ${ex.message}")
        false
    }
}

