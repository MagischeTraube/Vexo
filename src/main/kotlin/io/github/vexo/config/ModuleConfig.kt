package io.github.vexo.config

data class ModuleConfig(
    val name: String,
    val enabled: Boolean,
    val settings: Map<String, Any?>
)
