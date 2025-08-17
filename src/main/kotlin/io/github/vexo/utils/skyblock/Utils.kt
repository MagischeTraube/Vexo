package io.github.vexo.utils.skyblock

fun String.removeFormatting() : String {
    return this.replace(Regex("§."), "")
}