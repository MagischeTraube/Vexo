package io.github.vexo.features


import io.github.vexo.config.*
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

object Example2 : Module(
    name = "Example2",
    description = "Ka.",
    category = "Test"
) {
    private val toggleSetting = registerSetting(BooleanSetting("ToggleFeature", false, "Schaltet das Feature ein/aus"))
    private val toggleSetting2 = registerSetting(BooleanSetting("ToggleFeature", false, "Schaltet das Feature ein/aus"))
    private val toggleSetting3 = registerSetting(BooleanSetting("ToggleFeature", false, "Schaltet das Feature ein/aus"))

    @SubscribeEvent
    fun onChat(event: ClientChatReceivedEvent?) {
        println("Example2")
    }

}
