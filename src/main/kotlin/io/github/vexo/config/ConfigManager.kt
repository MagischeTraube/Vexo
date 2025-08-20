package io.github.vexo.config

import java.io.File
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.awt.Color

data class ColorData(val r: Int, val g: Int, val b: Int, val a: Int)

fun colorToData(color: Color): ColorData =
    ColorData(color.red, color.green, color.blue, color.alpha)

fun dataToColor(data: Map<String, Double>): Color {
    val r = (data["r"] ?: 0.0).toInt()
    val g = (data["g"] ?: 0.0).toInt()
    val b = (data["b"] ?: 0.0).toInt()
    val a = (data["a"] ?: 255.0).toInt()
    return Color(r, g, b, a)
}

object ConfigManager {
    private val gson = Gson()
    private val configFile = File("config/Vexo/vexo.cfg")

    fun load() {
        if (!configFile.exists()) return
        val text = configFile.readText()
        val type = object : TypeToken<List<ModuleConfig>>() {}.type
        val configs: List<ModuleConfig> = gson.fromJson(text, type)

        configs.forEach { cfg ->
            ModuleManager.getModules().find { it.name == cfg.name }?.apply {
                setEnabled(cfg.enabled)
                cfg.settings.forEach { (key, value) ->
                    settings.find { it.name == key }?.let { setting ->
                        when (setting) {
                            is ColorSetting -> {
                                @Suppress("UNCHECKED_CAST")
                                val map = value as? Map<String, Double>
                                if (map != null) {
                                    setting.value = dataToColor(map)
                                }
                            }
                            else -> {
                                @Suppress("UNCHECKED_CAST")
                                (setting as Setting<Any?>).value = value
                            }
                        }
                    }
                }
            }
        }
    }

    fun save() {
        val cfgList = ModuleManager.getModules().map { mod ->
            val settingsMap = mod.settings.associate { setting ->
                val value = when (setting) {
                    is ColorSetting -> colorToData(setting.value)
                    else -> setting.value
                }
                setting.name to value
            }
            ModuleConfig(mod.name, mod.enabled, settingsMap)
        }
        configFile.parentFile.mkdirs()
        configFile.writeText(GsonBuilder().setPrettyPrinting().create().toJson(cfgList))
    }
}
