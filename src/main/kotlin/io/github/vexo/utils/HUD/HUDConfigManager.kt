package io.github.vexo.utils.HUD

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import net.minecraft.client.Minecraft
import java.io.File
import java.io.FileReader
import java.io.FileWriter

// Data class to store HUD position info
data class HUDPosition(
    val name: String,
    val x: Float,
    val y: Float,
    val scale: Float = 1.0f,
    val enabled: Boolean = false  // Changed from true to false
)

object HUDConfigManager {
    private val gson = GsonBuilder().setPrettyPrinting().create()
    private val configFile: File
        get() = File(Minecraft.getMinecraft().mcDataDir, "config/Vexo/hud-positions.json")

    // Load positions from file
    fun loadPositions(): Map<String, HUDPosition> {
        return try {
            if (!configFile.exists()) {
                emptyMap()
            } else {
                FileReader(configFile).use { reader ->
                    val type = object : TypeToken<Map<String, HUDPosition>>() {}.type
                    gson.fromJson(reader, type) ?: emptyMap()
                }
            }
        } catch (e: Exception) {
            println("Failed to load HUD positions: ${e.message}")
            emptyMap()
        }
    }

    // Save positions to file
    fun savePositions(positions: Map<String, HUDPosition>) {
        try {
            // Create config directory if it doesn't exist
            configFile.parentFile.mkdirs()

            FileWriter(configFile).use { writer ->
                gson.toJson(positions, writer)
            }
        } catch (e: Exception) {
            println("Failed to save HUD positions: ${e.message}")
        }
    }

    // Save a single HUD position
    fun saveHUDPosition(hud: HUDSetting) {
        val positions = loadPositions().toMutableMap()
        positions[hud.name] = HUDPosition(
            name = hud.name,
            x = hud.value.x,
            y = hud.value.y,
            scale = hud.value.scale,
        )
        savePositions(positions)
    }

    // Load a single HUD position
    fun loadHUDPosition(hud: HUDSetting) {
        val positions = loadPositions()
        positions[hud.name]?.let { savedPosition ->
            hud.value.x = savedPosition.x
            hud.value.y = savedPosition.y
            hud.value.scale = savedPosition.scale
            hud.value.enabled = savedPosition.enabled
        }
    }

    // Save all registered HUDs
    fun saveAllHUDs() {
        val positions = mutableMapOf<String, HUDPosition>()
        for (hud in HUDRenderer.getAllHUDs()) {
            positions[hud.name] = HUDPosition(
                name = hud.name,
                x = hud.value.x,
                y = hud.value.y,
                scale = hud.value.scale,
                enabled = hud.value.enabled
            )
        }
        savePositions(positions)
    }

    // Load all registered HUDs
    fun loadAllHUDs() {
        val positions = loadPositions()
        for (hud in HUDRenderer.getAllHUDs()) {
            positions[hud.name]?.let { savedPosition ->
                hud.value.x = savedPosition.x
                hud.value.y = savedPosition.y
                hud.value.scale = savedPosition.scale
                hud.value.enabled = savedPosition.enabled
            }
        }
    }
}