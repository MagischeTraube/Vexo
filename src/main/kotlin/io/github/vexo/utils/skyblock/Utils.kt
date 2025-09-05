package io.github.vexo.utils.skyblock

fun String.removeFormatting() : String {
    return this.replace(Regex("§."), "")
}

object TickDelayUtils {

    private val delayedActions = mutableMapOf<Int, MutableList<() -> Unit>>()
    private var currentTick = 0

    fun tickDelay(ticks: Int, action: () -> Unit) {
        val targetTick = currentTick + ticks
        delayedActions.getOrPut(targetTick) { mutableListOf() }.add(action)
    }

    fun onTick() {
        currentTick++
        delayedActions[currentTick]?.let { actions ->
            actions.forEach { it() }
            delayedActions.remove(currentTick)
        }
    }
}