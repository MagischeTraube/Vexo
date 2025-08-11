package io.github.vexo.config


sealed class Setting<T>(
    val name: String,
    var value: T,
    val desc: String? = null
)

class BooleanSetting(name: String, value: Boolean, desc: String? = null) : Setting<Boolean>(name, value, desc)
class DropdownSetting(name: String, value: String, val options: List<String>, desc: String? = null) : Setting<String>(name, value, desc)
class KeybindSetting(name: String, value: Int, desc: String? = null) : Setting<Int>(name, value, desc)
class InputSetting(name: String, value: String, desc: String? = null) : Setting<String>(name, value, desc)
class ColorSetting(name: String, value: java.awt.Color, desc: String? = null) : Setting<java.awt.Color>(name, value, desc)
class SliderSetting(name: String, value: Float, val min: Int, val max: Int, desc: String? = null) : Setting<Float>(name, value, desc)
