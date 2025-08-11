package io.github.vexo.features


import io.github.vexo.config.*
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.awt.Color

object Example : Module(
    name = "Example",
    description = "Ein Beispielmodul, das verschiedene Einstellungen demonstriert.",
    category = "Test"
) {
    private val toggleSetting = registerSetting(BooleanSetting("ToggleFeature", false, "Schaltet das Feature ein/aus"))
    private val toggleSetting2 = registerSetting(BooleanSetting("ToggleFeature", false, "Schaltet das Feature ein/aus"))
    private val toggleSetting3 = registerSetting(BooleanSetting("ToggleFeature", false, "Schaltet das Feature ein/aus"))

    private val modeSetting = registerSetting(DropdownSetting("Mode", "Easy", listOf("Easy", "Medium", "Hard"), "Wähle den Modus"))

    private val keybindSetting = registerSetting(KeybindSetting("ActivateKey", 32, "Taste zum Aktivieren"))

    private val speedSetting = registerSetting(SliderSetting("Speed", 1.0f, 10, 125, "Geschwindigkeit"))

    private val textSetting = registerSetting(InputSetting("TestInput:", "Testen", "Einstellen"))

    private val colorChanger = registerSetting(ColorSetting("ColorChanger", Color(255, 0, 0, 255), "Change Color") )

    @SubscribeEvent
    fun onChat(event: ClientChatReceivedEvent?) {
        if (toggleSetting.value) {
            println("Example1")
        }
    }
}
