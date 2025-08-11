package io.github.vexo.config

import io.github.vexo.Vexo
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.GuiTextField
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import java.awt.Color

class VexoGui : GuiScreen() {

    private val mc = Minecraft.getMinecraft()
    val scaled = net.minecraft.client.gui.ScaledResolution(mc)
    private val scaledWidth = scaled.scaledWidth
    private val scaledHeight = scaled.scaledHeight
    private val guiWidth = (scaledWidth * 0.7).toInt()
    private val guiHeight = (scaledHeight * 0.7).toInt()
    private val guiLeft = (scaledWidth - guiWidth) / 2
    private val guiTop = (scaledHeight - guiHeight) / 2


    private val categoryWidth = (guiWidth * 0.2).toInt()
    private val moduleWidth = (guiWidth * 0.4).toInt()
    private val settingsWidth = guiWidth - categoryWidth - moduleWidth

    private var selectedCategory: String? = null
    private var selectedModule: Module? = null
    private var searchText = ""
    private lateinit var searchBox: GuiTextField

    private var categoryScroll = 0f
    private var moduleScroll = 0f
    private var settingsScroll = 0f

    private var maxCategoryScroll = 0f
    private var maxModuleScroll = 0f
    private var maxSettingsScroll = 0f

    private val headerHeight = 25

    private var focusedSetting: Setting<*>? = null
    private var keybindListening = false

    private var dropdownOpen: DropdownSetting? = null
    private var sliderDragging: SliderSetting? = null
    private var colorPickerOpen: ColorSetting? = null
    private var inputField: GuiTextField? = null

    private var hue = 0f          // 0..1
    private var saturation = 0f   // 0..1
    private var brightness = 1f   // 0..1

    private val pickerSize = 256
    private val pickerX = 100
    private val pickerY = 100

    fun hsvToRgb(h: Float, s: Float, v: Float): Color {
        return Color.getHSBColor(h, s, v)
    }

    override fun initGui() {

        Keyboard.enableRepeatEvents(true)
        searchBox = GuiTextField(0, mc.fontRendererObj, 0, 0, moduleWidth, 14)
        searchBox.setMaxStringLength(50)
        searchBox.text = ""

        inputField = GuiTextField(1, mc.fontRendererObj, 0, 0, settingsWidth - 160, 14)

        if (selectedCategory == null) {
            selectedCategory = ModuleManager.getCategories().firstOrNull()
        }
        super.initGui()
    }


    override fun onGuiClosed() {
        Keyboard.enableRepeatEvents(false)
        ConfigManager.save()
        super.onGuiClosed()
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        // Draw main GUI box
        drawRoundedRect(guiLeft, guiTop, guiLeft + guiWidth, guiTop + guiHeight, 12, Color(15, 15, 15).rgb)

        // Header
        val headerHeight = 20
        drawRect(guiLeft, guiTop, guiLeft + guiWidth, guiTop + headerHeight, Color(20, 20, 20).rgb)

        // Left: Vexo 1.0.0
        mc.fontRendererObj.drawStringWithShadow("Vexo ${Vexo.VERSION}", guiLeft + 10f, guiTop + 6f, Color(50, 150, 255).rgb)

        val searchBoxX = guiLeft + categoryWidth + moduleWidth + 5
        searchBox.xPosition = searchBoxX
        searchBox.yPosition = guiTop + 3
        searchBox.width = settingsWidth - 10



        // Draw vertical separators
        drawRect(guiLeft + categoryWidth, guiTop, guiLeft + categoryWidth + 1, guiTop + guiHeight, Color(50, 50, 50).rgb)
        drawRect(guiLeft + categoryWidth + moduleWidth, guiTop, guiLeft + categoryWidth + moduleWidth + 1, guiTop + guiHeight, Color(50, 50, 50).rgb)

        drawCategories(mouseX, mouseY)
        drawModules(mouseX, mouseY)
        drawSettings(mouseX, mouseY)

        searchBox.drawTextBox()

        // Settings Area
        startScissor(guiLeft + categoryWidth + moduleWidth, guiTop + 40, settingsWidth, guiHeight - 40)


        colorPickerOpen?.let { cp ->

            // Color Area
            for (x in 0 until pickerSize) {
                val hue = x.toFloat() / (pickerSize - 1)

                for (y in 0 until pickerSize) {
                    val yRatio = y.toFloat() / (pickerSize - 1)

                    val saturation: Float
                    val brightness: Float

                    if (yRatio < 0.5f) {
                        // white - color
                        saturation = yRatio / 0.5f  // 0 ... 1
                        brightness = 1f
                    } else {
                        // color - black
                        saturation = 1f
                        brightness = 1f - ((yRatio - 0.5f) / 0.5f)  // 1 ... 0
                    }

                    val color = hsvToRgb(hue, saturation, brightness)
                    drawRect(pickerX + x, pickerY + y, pickerX + x + 1, pickerY + y + 1, color.rgb)
                }
            }



            val markerX = (hue * (pickerSize - 1)).toInt() + pickerX
            val markerY = if (brightness == 1f) {
                (saturation * 0.5f * (pickerSize - 1)).toInt() + pickerY
            } else {
                ((0.5f + (1f - brightness) * 0.5f) * (pickerSize - 1)).toInt() + pickerY
            }
            drawCircle(markerX, markerY, 5, Color.WHITE.rgb)
        }

        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    private fun drawCategories(mouseX: Int, mouseY: Int) {
        val categories = ModuleManager.getCategories()
        val lineHeight = 20
        maxCategoryScroll = (categories.size * lineHeight - (guiHeight - headerHeight)).coerceAtLeast(0).toFloat()

        handleScroll(categoryScroll, maxCategoryScroll, guiLeft, guiTop + headerHeight, categoryWidth, guiHeight - headerHeight, mouseX, mouseY)
        startScissor(guiLeft, guiTop + headerHeight, categoryWidth, guiHeight - headerHeight)

        var yPos = (guiTop + headerHeight - categoryScroll).toInt()
        for (cat in categories) {
            val isSelected = cat == selectedCategory
            val bgColor = if (isSelected) Color(50, 150, 255) else Color(40, 40, 40)
            drawRoundedRect(guiLeft + 5, yPos, guiLeft + categoryWidth - 5, yPos + lineHeight - 2, 6, bgColor.rgb)
            val textColor = if (isSelected) Color.WHITE else Color.LIGHT_GRAY
            mc.fontRendererObj.drawString(cat, (guiLeft + 10), (yPos + 6), textColor.rgb)
            yPos += lineHeight
        }
    }


    private fun drawModules(mouseX: Int, mouseY: Int) {
        if (selectedCategory == null) return
        val allModules = ModuleManager.getModules(selectedCategory!!)
        val filteredModules = if (searchText.isBlank()) allModules else allModules.filter { it.name.contains(searchText, true) }

        val lineHeight = 20
        maxModuleScroll = (filteredModules.size * lineHeight - guiHeight).coerceAtLeast(0).toFloat()
        handleScroll(moduleScroll, maxModuleScroll, guiLeft + categoryWidth, guiTop + 20, moduleWidth, guiHeight - 20, mouseX, mouseY)

        startScissor(guiLeft + categoryWidth, guiTop + 20, moduleWidth, guiHeight - 20)

        var yPos = (guiTop + 20 - moduleScroll).toInt()

        for (mod in filteredModules) {
            val enabled = mod.enabled

            val hovered = mouseX in (guiLeft + categoryWidth) until (guiLeft + categoryWidth + moduleWidth) && mouseY in yPos until (yPos + lineHeight)
            val bgColor = when {
                hovered -> Color(60, 60, 60)
                else -> Color(35, 35, 35)
            }
            drawRoundedRect(guiLeft + categoryWidth + 5, yPos, guiLeft + categoryWidth + moduleWidth - 5, yPos + lineHeight - 2, 6, bgColor.rgb)

            // Module name
            mc.fontRendererObj.drawString(mod.name, (guiLeft + categoryWidth + 10), (yPos + 6), Color.WHITE.rgb)

            // Enabled switch right
            val switchWidth = 30
            val switchHeight = 12
            val switchX = guiLeft + categoryWidth + moduleWidth - switchWidth - 10
            val switchY = (yPos + (lineHeight - switchHeight) / 2 ) - 1

            drawSwitch(switchX, switchY, switchWidth, switchHeight, enabled)

            yPos += lineHeight
        }
    }

    private fun drawWrappedString(text: String, x: Int, y: Int, maxWidth: Int, lineHeight: Int, color: Int): Int {
        var curY = y
        val words = text.split(" ")
        var line = ""
        for (word in words) {
            val testLine = if (line.isEmpty()) word else "$line $word"
            val width = mc.fontRendererObj.getStringWidth(testLine)
            if (width > maxWidth) {
                mc.fontRendererObj.drawString(line, x, curY, color)
                line = word
                curY += lineHeight
            } else {
                line = testLine
            }
        }

        if (line.isNotEmpty()) {
            mc.fontRendererObj.drawString(line, x, curY, color)
            curY += lineHeight
        }
        return curY
    }

    override fun doesGuiPauseGame(): Boolean {
        return false
    }

    private fun drawSettings(mouseX: Int, mouseY: Int) {
        val mod = selectedModule ?: run {
            mc.fontRendererObj.drawString("Select a feature to see settings", guiLeft + categoryWidth + moduleWidth + 10, guiTop + 40, Color.LIGHT_GRAY.rgb)
            return
        }

        val lineHeight = 22
        val descriptionAreaHeight = 60
        maxSettingsScroll = (mod.settings.size * (lineHeight + 15) + descriptionAreaHeight - guiHeight).coerceAtLeast(0).toFloat()
        handleScroll(settingsScroll, maxSettingsScroll, guiLeft + categoryWidth + moduleWidth, guiTop + 40, settingsWidth, guiHeight - 40, mouseX, mouseY)

        startScissor(guiLeft + categoryWidth + moduleWidth, guiTop + 40, settingsWidth, guiHeight - 40)

        var yPos = (guiTop + 40 - settingsScroll).toInt()

        mod.description?.let { desc ->
            val descHeight = 12 * (desc.length / (settingsWidth / 7) + 1)

            val descTop = yPos
            val descBottom = yPos + descHeight

            if (descBottom >= guiTop + 40 && descTop <= guiTop + guiHeight) {
                yPos = drawWrappedString(desc, guiLeft + categoryWidth + moduleWidth + 10, yPos, settingsWidth - 20, 12, Color.LIGHT_GRAY.rgb)
                yPos += 10

            } else {

                yPos += descHeight + 10
            }
        }

        for (setting in mod.settings) {
            if (yPos + lineHeight >= guiTop + 40 && yPos <= guiTop + guiHeight - 20) {
                drawSetting(setting, guiLeft + categoryWidth + moduleWidth + 10, yPos, mouseX, mouseY)

                if (setting == dropdownOpen && setting is DropdownSetting) {
                    val optionHeight = 15
                    val optionX = guiLeft + categoryWidth + moduleWidth + 10 + 150
                    var optionY = yPos + 20

                    for (option in setting.options) {
                        val isHovered = mouseX in optionX..(optionX + 100) && mouseY in optionY..(optionY + optionHeight)
                        val bgColor = if (isHovered) Color(60, 60, 60) else Color(40, 40, 40)
                        drawRect(optionX, optionY, optionX + 100, optionY + optionHeight, bgColor.rgb)
                        mc.fontRendererObj.drawString(option, optionX + 5, optionY + 3, Color.WHITE.rgb)
                        optionY += optionHeight
                    }

                    yPos += (setting.options.size * optionHeight)
                }
            }
            yPos += lineHeight + 15
        }
    }

    private fun drawSetting(setting: Setting<*>, x: Int, y: Int, mouseX: Int, mouseY: Int) {
        mc.fontRendererObj.drawString(setting.name, x, y, Color.WHITE.rgb)

        val controlAreaWidth = settingsWidth - 30
        val controlRightX = x + controlAreaWidth

        when (setting) {
            is BooleanSetting -> {
                val switchWidth = 30
                val switchHeight = 14
                drawSwitch(controlRightX - switchWidth, y - 3, switchWidth, switchHeight, setting.value)
            }
            is DropdownSetting -> {
                val dropdownText = setting.value
                val dropdownWidth = mc.fontRendererObj.getStringWidth(dropdownText)
                mc.fontRendererObj.drawString(dropdownText, controlRightX - dropdownWidth, y, Color.LIGHT_GRAY.rgb)
            }
            is KeybindSetting -> {
                val display = if (keybindListening && focusedSetting == setting) {
                    "Press a key..."
                } else {
                    if (setting.value < 0) "None" else Keyboard.getKeyName(setting.value)
                }
                val keybindWidth = mc.fontRendererObj.getStringWidth(display)
                mc.fontRendererObj.drawString(display, controlRightX - keybindWidth, y, Color.LIGHT_GRAY.rgb)
            }
            is InputSetting -> {
                val inputX = x + 130
                val inputY = y - 2

                if (focusedSetting == setting && inputField != null) {
                    inputField!!.xPosition = inputX
                    inputField!!.yPosition = inputY
                    inputField!!.width = settingsWidth - 160
                    inputField!!.drawTextBox()
                } else {
                    val valueText = setting.value
                    val valueWidth = mc.fontRendererObj.getStringWidth(valueText)
                    mc.fontRendererObj.drawString(
                        valueText,
                        controlRightX - valueWidth,
                        y,
                        Color.LIGHT_GRAY.rgb
                    )
                }
            }


            is ColorSetting -> {
                val colorBoxSize = 20
                drawRect(controlRightX - colorBoxSize, y, controlRightX, y + 14, setting.value.rgb)
            }
            is SliderSetting -> {
                val sliderHeight = 6
                val sliderY = y - 1

                val (sliderX, sliderWidth) = getSliderXAndWidth()

                drawRect(sliderX, sliderY, sliderX + sliderWidth, sliderY + sliderHeight, Color.DARK_GRAY.rgb)

                val clampedValue = setting.value.coerceIn(setting.min.toFloat(), setting.max.toFloat())
                val pos = (((clampedValue - setting.min) / (setting.max - setting.min)) * sliderWidth).toInt()

                drawRect(sliderX, sliderY, sliderX + pos, sliderY + sliderHeight, Color(50, 150, 255).rgb)

                val currentValue = (setting.min + ((setting.max - setting.min) * (pos.toFloat() / sliderWidth))).toInt()
                val valueText = currentValue.toString()

                mc.fontRendererObj.drawString(
                    valueText,
                    sliderX + sliderWidth + 5,
                    y - 1,
                    Color.LIGHT_GRAY.rgb
                )
            }
        }
    }


    private fun drawSwitch(x: Int, y: Int, width: Int, height: Int, enabled: Boolean) {
        val bgColor = if (enabled) Color(50, 150, 255) else Color(100, 100, 100)
        drawRoundedRect(x, y, x + width, y + height, height / 2, bgColor.rgb)
        val circleX = if (enabled) x + width - height / 2 else x + height / 2
        val circleColor = Color.WHITE.rgb
        drawCircle(circleX, y + height / 2, height / 2 - 2, circleColor)
    }

    private fun drawRoundedRect(left: Int, top: Int, right: Int, bottom: Int, radius: Int, color: Int) {
        // not round
        drawRect(left, top, right, bottom, color)
    }

    private fun drawCircle(x: Int, y: Int, radius: Int, color: Int) {
        // rect no circle
        drawRect(x - radius, y - radius, x + radius, y + radius, color)
    }

    private fun handleScroll(scroll: Float, maxScroll: Float, x: Int, y: Int, width: Int, height: Int, mouseX: Int, mouseY: Int) {
        if (mouseX in x until (x + width) && mouseY in y until (y + height)) {
            val dWheel = Mouse.getDWheel()
            if (dWheel != 0) {
                val newScroll = (scroll - dWheel * 0.25f).coerceIn(0f, maxScroll)
                when {
                    x == guiLeft -> categoryScroll = newScroll
                    x == guiLeft + categoryWidth -> moduleScroll = newScroll
                    x == guiLeft + categoryWidth + moduleWidth -> settingsScroll = newScroll
                }
            }
        }
    }


    private fun startScissor(x: Int, y: Int, w: Int, h: Int) {
        val windowHeight = mc.displayHeight
        val sx = x
        val sy = windowHeight - (y + h)
        val sw = w
        val sh = h
        org.lwjgl.opengl.GL11.glScissor(sx, sy, sw, sh)
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {

        // 1.1 Dropdown-Options
        dropdownOpen?.let { dd ->
            val optionHeight = 15
            val optionX = guiLeft + categoryWidth + moduleWidth + 10 + 150 // ggf. anpassen
            val settingIndex = selectedModule?.settings?.indexOf(dd) ?: -1
            if (settingIndex >= 0) {
                var optionY = (guiTop + 40 - settingsScroll).toInt()
                selectedModule?.description?.let {
                    optionY += mc.fontRendererObj.listFormattedStringToWidth(it, settingsWidth - 20).size * 12 + 10
                }
                optionY += settingIndex * (22 + 15) + 22
                for (option in dd.options) {
                    if (mouseX in optionX..(optionX + 100) && mouseY in optionY..(optionY + optionHeight)) {
                        dd.value = option
                        dropdownOpen = null
                        focusedSetting = null
                        inputField?.setFocused(false)
                        return
                    }
                    optionY += optionHeight
                }
            }
        }

        // 1.2 ColorPicker
        colorPickerOpen?.let { cp ->
            if (mouseX in pickerX until (pickerX + pickerSize) && mouseY in pickerY until (pickerY + pickerSize)) {
                val relativeX = mouseX - pickerX
                val relativeY = mouseY - pickerY

                hue = relativeX.toFloat() / (pickerSize - 1)

                val yRatio = relativeY.toFloat() / (pickerSize - 1)
                saturation = if (yRatio < 0.5f) {
                    yRatio / 0.5f
                } else {
                    1f
                }

                brightness = if (yRatio < 0.5f) {
                    1f
                } else {
                    1f - ((yRatio - 0.5f) / 0.5f)
                }

                val newColor = hsvToRgb(hue, saturation, brightness)
                cp.value = newColor
                focusedSetting = null
                inputField?.setFocused(false)
                return
            }
        }

        // 1.3 InputField
        selectedModule?.let { mod ->
            var yPos = (guiTop + 40 - settingsScroll).toInt()
            mod.description?.let {
                yPos += mc.fontRendererObj.listFormattedStringToWidth(it, settingsWidth - 20).size * 12 + 10
            }

            for (setting in mod.settings) {
                val settingX = guiLeft + categoryWidth + moduleWidth + 10
                val inputY = yPos

                if (setting is InputSetting) {
                    inputField?.xPosition = settingX
                    inputField?.yPosition = inputY
                    inputField?.width = settingsWidth - 30
                    inputField?.setText(setting.value)

                    if (mouseX in inputField!!.xPosition..(inputField!!.xPosition + inputField!!.width) &&
                        mouseY in inputField!!.yPosition..(inputField!!.yPosition + 14)) {
                        if (mouseButton == 0) {
                            focusedSetting = setting
                            inputField?.setFocused(true)
                            dropdownOpen = null
                            colorPickerOpen = null
                        }
                        return
                    } else if (focusedSetting == setting) {
                        inputField?.setFocused(false)
                        focusedSetting = null
                    }
                }

                yPos += 22 + 15
            }
        }

        // 2. Categories
        val categories = ModuleManager.getCategories()
        val catLineHeight = 20
        var catYPos = (guiTop + headerHeight - categoryScroll).toInt()
        for (cat in categories) {
            if (mouseX in (guiLeft + 5)..(guiLeft + categoryWidth - 5) &&
                mouseY in catYPos..(catYPos + catLineHeight)) {
                selectedCategory = cat
                selectedModule = null
                moduleScroll = 0f
                settingsScroll = 0f
                dropdownOpen = null
                colorPickerOpen = null
                inputField?.setFocused(false)
                focusedSetting = null
                return
            }
            catYPos += catLineHeight
        }

        // 3. Modules
        if (selectedCategory != null) {
            val modules = ModuleManager.getModules(selectedCategory!!)
            val filteredModules = if (searchText.isBlank()) modules else modules.filter { it.name.contains(searchText, true) }
            var modYPos = (guiTop + 20 - moduleScroll).toInt()
            val modLineHeight = 20

            for (mod in filteredModules) {
                val modXStart = guiLeft + categoryWidth + 5
                val modXEnd = guiLeft + categoryWidth + moduleWidth - 5

                // Switch-Hitbox berechnen
                val switchWidth = 30
                val switchHeight = 12
                val switchX = guiLeft + categoryWidth + moduleWidth - switchWidth - 10
                val switchY = (modYPos + (modLineHeight - switchHeight) / 2) - 1

                if (mouseX in modXStart..modXEnd && mouseY in modYPos..(modYPos + modLineHeight)) {
                    if (mouseButton == 0) {
                        if (mouseX in switchX..(switchX + switchWidth) && mouseY in switchY..(switchY + switchHeight)) {
                            mod.setEnabled(!mod.enabled)
                        } else {
                            selectedModule = mod
                            settingsScroll = 0f
                        }

                        dropdownOpen = null
                        colorPickerOpen = null
                        inputField?.setFocused(false)
                        focusedSetting = null
                    } else if (mouseButton == 1) {
                        selectedModule = mod
                        settingsScroll = 0f
                        dropdownOpen = null
                        colorPickerOpen = null
                        inputField?.setFocused(false)
                        focusedSetting = null
                    }
                    return
                }
                modYPos += modLineHeight
            }
        }


        // 4. Settings
        selectedModule?.let { mod ->
            val lineHeight = 22
            var yPos = (guiTop + 40 - settingsScroll).toInt()

            mod.description?.let {
                yPos = drawWrappedString(it, guiLeft + categoryWidth + moduleWidth + 10, yPos, settingsWidth - 20, 12, Color.LIGHT_GRAY.rgb)
                yPos += 10
            }

            val settingX = guiLeft + categoryWidth + moduleWidth + 10
            val controlRightX = settingX + settingsWidth - 30

            for (setting in mod.settings) {
                if (setting == focusedSetting && setting is InputSetting) {
                    inputField?.xPosition = settingX
                    inputField?.yPosition = yPos
                    inputField?.width = settingsWidth - 30
                }

                when (setting) {
                    is BooleanSetting -> {
                        val hitboxXStart = controlRightX - 30
                        val hitboxXEnd = controlRightX
                        val hitboxYStart = yPos - 5
                        val hitboxYEnd = yPos + 14
                        if (mouseX in hitboxXStart..hitboxXEnd && mouseY in hitboxYStart..hitboxYEnd) {
                            if (mouseButton == 0) setting.value = !setting.value
                            return
                        }
                    }

                    is DropdownSetting -> {
                        val dropdownText = setting.value
                        val textWidth = mc.fontRendererObj.getStringWidth(dropdownText)
                        val hitboxXStart = controlRightX - textWidth - 5
                        val hitboxXEnd = controlRightX + 5
                        val hitboxYStart = yPos - 3
                        val hitboxYEnd = yPos + 12
                        if (mouseX in hitboxXStart..hitboxXEnd && mouseY in hitboxYStart..hitboxYEnd) {
                            if (mouseButton == 0) {
                                dropdownOpen = if (dropdownOpen == setting) null else setting
                                colorPickerOpen = null
                                inputField?.setFocused(false)
                                focusedSetting = null
                            }
                            return
                        }
                    }

                    is KeybindSetting -> {
                        val display = if (keybindListening && focusedSetting == setting) "Press a key..." else if (setting.value < 0) "None" else Keyboard.getKeyName(setting.value)
                        val textWidth = mc.fontRendererObj.getStringWidth(display)
                        val hitboxXStart = controlRightX - textWidth - 5
                        val hitboxXEnd = controlRightX + 5
                        val hitboxYStart = yPos - 3
                        val hitboxYEnd = yPos + 12
                        if (mouseX in hitboxXStart..hitboxXEnd && mouseY in hitboxYStart..hitboxYEnd) {
                            if (mouseButton == 0) {
                                keybindListening = true
                                focusedSetting = setting
                                dropdownOpen = null
                                colorPickerOpen = null
                                inputField?.setFocused(false)
                            }
                            return
                        }
                    }

                    is InputSetting -> {
                        inputField?.let { input ->
                            if (mouseX in input.xPosition..(input.xPosition + input.width) &&
                                mouseY in input.yPosition..(input.yPosition + 14)) {
                                if (mouseButton == 0) {
                                    focusedSetting = setting
                                    input.setFocused(true)
                                    dropdownOpen = null
                                    colorPickerOpen = null
                                }
                                return
                            }
                        }
                    }

                    is ColorSetting -> {
                        val boxSize = 20
                        val hitboxXStart = controlRightX - boxSize
                        val hitboxXEnd = controlRightX
                        val hitboxYStart = yPos
                        val hitboxYEnd = yPos + 14
                        if (mouseX in hitboxXStart..hitboxXEnd && mouseY in hitboxYStart..hitboxYEnd) {
                            if (mouseButton == 0) {
                                colorPickerOpen = if (colorPickerOpen == setting) null else setting
                                dropdownOpen = null
                                inputField?.setFocused(false)
                                focusedSetting = null
                            }
                            return
                        }
                    }

                    is SliderSetting -> {
                        val sliderHeight = 6
                        val sliderYStart = yPos - 1
                        val sliderYEnd = sliderYStart + sliderHeight

                        val (sliderX, sliderWidth) = getSliderXAndWidth()

                        if (mouseX in sliderX..(sliderX + sliderWidth) && mouseY in sliderYStart..sliderYEnd) {
                            if (mouseButton == 0) {
                                sliderDragging = setting
                                dropdownOpen = null
                                colorPickerOpen = null
                                inputField?.setFocused(false)
                                focusedSetting = null
                            }
                            return
                        }
                    }



                }

                yPos += lineHeight + 15
            }
        }

        // 5. SearchBox
        searchBox.mouseClicked(mouseX, mouseY, mouseButton)

        // empty
        focusedSetting = null
        inputField?.setFocused(false)
        dropdownOpen = null
        colorPickerOpen = null
        keybindListening = false
        sliderDragging = null

        super.mouseClicked(mouseX, mouseY, mouseButton)
    }

    override fun mouseClickMove(mouseX: Int, mouseY: Int, clickedMouseButton: Int, timeSinceLastClick: Long) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick)

        sliderDragging?.let { slider ->
            val (sliderX, sliderWidth) = getSliderXAndWidth()
            val relativeX = (mouseX - sliderX).coerceIn(0, sliderWidth)
            val newValue = slider.min + (slider.max - slider.min) * (relativeX.toFloat() / sliderWidth)
            slider.value = newValue.coerceIn(slider.min.toFloat(), slider.max.toFloat())
        }


        colorPickerOpen?.let { cp ->
            if (mouseX in pickerX until (pickerX + pickerSize) && mouseY in pickerY until (pickerY + pickerSize)) {
                val relativeX = mouseX - pickerX
                val relativeY = mouseY - pickerY

                hue = relativeX.toFloat() / (pickerSize - 1)

                val yRatio = relativeY.toFloat() / (pickerSize - 1)
                saturation = if (yRatio < 0.5f) {
                    yRatio / 0.5f
                } else {
                    1f
                }

                brightness = if (yRatio < 0.5f) {
                    1f
                } else {
                    1f - ((yRatio - 0.5f) / 0.5f)
                }

                val newColor = hsvToRgb(hue, saturation, brightness)
                cp.value = newColor
            }
        }
    }


    private fun getSliderXAndWidth(): Pair<Int, Int> {
        val sliderWidth = 100
        val paddingRight = 20

        val rightEdge = guiLeft + categoryWidth + moduleWidth + 10 + settingsWidth - paddingRight
        val sliderX = rightEdge - sliderWidth - 10

        return sliderX to sliderWidth
    }


    override fun mouseReleased(mouseX: Int, mouseY: Int, state: Int) {
        super.mouseReleased(mouseX, mouseY, state)
        sliderDragging = null
    }


    override fun keyTyped(typedChar: Char, keyCode: Int) {
        if (inputField != null && inputField!!.isFocused) {
            inputField!!.textboxKeyTyped(typedChar, keyCode)
            focusedSetting?.let { fs ->
                if (fs is InputSetting) {
                    fs.value = inputField!!.text
                }
            }
            return
        }
        if (searchBox.isFocused) {
            searchBox.textboxKeyTyped(typedChar, keyCode)
            searchText = searchBox.text
        } else if (keybindListening && focusedSetting is KeybindSetting) {
            val kbSetting = focusedSetting as KeybindSetting
            kbSetting.value = if (keyCode == Keyboard.KEY_ESCAPE) -1 else keyCode
            keybindListening = false
            focusedSetting = null
        } else if (focusedSetting is InputSetting) {
            val input = focusedSetting as InputSetting
            if (keyCode == Keyboard.KEY_BACK) {
                if (input.value.isNotEmpty()) {
                    input.value = input.value.dropLast(1)
                }
            } else if (typedChar.isLetterOrDigit() || typedChar.isWhitespace() || typedChar in setOf('.', ',', '-', '_')) {
                input.value += typedChar
            }
        } else if (keyCode == Keyboard.KEY_ESCAPE) {
            mc.displayGuiScreen(null)
        }
        super.keyTyped(typedChar, keyCode)
    }

    override fun updateScreen() {
        searchBox.updateCursorCounter()
        super.updateScreen()

        focusedSetting?.let { fs ->
            if (fs is InputSetting && inputField != null && inputField!!.isFocused) {
                if (fs.value != inputField!!.text) {
                    fs.value = inputField!!.text
                }
            }
        }

        inputField?.updateCursorCounter()
    }
}