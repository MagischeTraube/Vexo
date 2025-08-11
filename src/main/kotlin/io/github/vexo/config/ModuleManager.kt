package io.github.vexo.config

object ModuleManager {
    private val modules = mutableListOf<Module>()
    private val enabledModules = mutableSetOf<Module>()

    fun register(module: Module) {
        if (!modules.contains(module)) modules.add(module)
    }

    fun register(modules: Collection<Module>) {
        modules.forEach { register(it) }
    }

    fun unregister(module: Module) {
        enabledModules.remove(module)
    }

    fun getModules() = modules.toList()
    fun getModules(category: String) = modules.filter { it.category == category }

    fun getCategories() = modules.map { it.category }.distinct()
}


