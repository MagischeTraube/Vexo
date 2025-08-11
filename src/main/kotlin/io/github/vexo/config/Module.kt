package io.github.vexo.config

import net.minecraftforge.common.MinecraftForge

abstract class Module(
    val name: String,
    val description: String,
    val category: String
) {
    var enabled = false
        private set

    val settings = mutableListOf<Setting<*>>()

    fun <T> registerSetting(setting: Setting<T>): Setting<T> {
        settings += setting
        return setting
    }

    fun setEnabled(enabled: Boolean) {
        if (this.enabled == enabled) return
        this.enabled = enabled

        if (enabled) {
            ModuleManager.register(this)
            MinecraftForge.EVENT_BUS.register(this)
        } else {
            ModuleManager.unregister(this)
            MinecraftForge.EVENT_BUS.unregister(this)
        }
    }
}


