package io.github.vexo.features.dungeons

import io.github.vexo.Vexo.Companion.mc
import io.github.vexo.config.BooleanSetting
import io.github.vexo.config.ColorSetting
import io.github.vexo.config.Module
import io.github.vexo.events.PriceDataUpdateEvent
import io.github.vexo.utils.skyblock.*
import io.github.vexo.utils.skyblock.PriceUtils.getPrice
import io.github.vexo.utils.skyblock.PriceUtils.setForceFetch
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumChatFormatting
import net.minecraftforge.client.event.GuiScreenEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.awt.Color
import java.text.NumberFormat
import java.util.Locale

object ProfitTracker : Module(
    name = "Profit Tracker",
    description = "Shows Dungeon Chest Profit",
    category = "Dungeons"
) {
    private val includeTaxes = registerSetting(BooleanSetting("Include Taxes", true))
    private val sellOffer = registerSetting(BooleanSetting("Sell Offer", false))
    private val includeKeyPrice = registerSetting(BooleanSetting("Include Key Price", true))

    private val mostProfitColor = registerSetting(ColorSetting("Most Profit", Color(18, 250, 0)))
    private val secondMostProfitColor = registerSetting(ColorSetting("2nd Most Profit", Color(5, 163, 255)))

    private val unopenedChestColor = registerSetting(ColorSetting("No Chest Opened", Color(18, 250, 0)))
    private val openedChestColor = registerSetting(ColorSetting("Opened Chest", Color(160, 162, 0)))
    private val noMoreChestColor = registerSetting(ColorSetting("No More Chests", Color(78, 0, 2)))

    private val customGui = registerSetting(BooleanSetting("Change Gui Color", true))
    private val customGuiColor = registerSetting(ColorSetting("Gui Color", Color(79, 79, 79)))

    private val showNegativeProfit = registerSetting(BooleanSetting("Show negative Profit", true))

    private var lastContentHash: Int? = null
    private var cachedCroesusChests: List<Pair<Int, Int>> = emptyList()
    private var cachedChestProfit: Pair<Int, Int>? = null

    private var forceRecalc = false

    private var lastGuiTitle: String? = null

    @SubscribeEvent
    fun onGuiRender(event: GuiScreenEvent.DrawScreenEvent.Pre) {
        val gui = mc.currentScreen as? GuiContainer ?: return
        val guiContentHash = getGuiContentHash(gui)

        when (val title = gui.getGuiTitle() ?: return) {
            CroesusMenuGuiTitle -> {
                CroesusMenuHighlight(gui)

                if (lastGuiTitle != CroesusMenuGuiTitle) {
                    setForceFetch()
                }
            }

            else -> when {
                CroesusChestsGuiTitle.any { title.contains(it) } -> {
                    if (forceRecalc || lastContentHash != guiContentHash || cachedCroesusChests.isEmpty()) {
                        cachedCroesusChests = calculateCroesusChestsProfits(gui)
                        lastContentHash = guiContentHash
                        forceRecalc = false
                    }
                    drawCroesusChests(gui, cachedCroesusChests)
                }

                DungeonChestsGuiTitle.any { title.contains(it) } -> {
                    if (forceRecalc || lastContentHash != guiContentHash || cachedChestProfit == null) {
                        cachedChestProfit = calculateChestProfitForGui(gui)
                        lastContentHash = guiContentHash
                        forceRecalc = false
                    }
                    cachedChestProfit?.let { (slot, profit) ->
                        drawChestProfit(gui, slot, profit)
                    }
                }
            }
        }
    }

    @SubscribeEvent
    fun onGuiClosed(event: GuiScreenEvent.InitGuiEvent.Post) {
        if (event.gui == null) {
            resetCache()
        }
    }

    @SubscribeEvent
    fun onPriceUpdate(event: PriceDataUpdateEvent) {
        resetCache()
        forceRecalc = true
    }

    private fun resetCache() {
        lastContentHash = null
        cachedCroesusChests = emptyList()
        cachedChestProfit = null
    }

    private fun getGuiContentHash(gui: GuiContainer): Int {
        return gui.inventorySlots.inventorySlots
            .joinToString("|") { it.stack?.displayName ?: "empty" }
            .hashCode()
    }

    private fun CroesusMenuHighlight(gui: GuiContainer) {
        drawCustomOverlay(gui)
        forEachValidSlot(gui) { slot, stack ->
            val lore = stack.getItemLore()
            when {
                lore.contains("No Chests Opened!") -> highlightSlot(gui, slot.slotNumber, unopenedChestColor.value, false)
                lore.any { "Opened Chest" in it } -> highlightSlot(gui, slot.slotNumber, openedChestColor.value, false)
                lore.contains("No more Chests to open!") -> highlightSlot(gui, slot.slotNumber, noMoreChestColor.value, false)
            }
        }
    }

    private fun calculateCroesusChestsProfits(gui: GuiContainer): List<Pair<Int, Int>> {
        val chestProfits = mutableListOf<Pair<Int, Int>>()
        forEachValidSlot(gui) { slot, stack ->
            val name = stack.cleanName()
            if (DungeonChestsGuiTitle.contains(name) && stack.getItemLore().none { "Already opened!" in it }) {
                val (loot, costs) = stack.getChestContentsAndCost()
                chestProfits += slot.slotNumber to calculateChestProfit(loot, costs)
            }
        }
        return chestProfits.sortedByDescending { it.second }
    }

    private fun drawCroesusChests(gui: GuiContainer, chestProfits: List<Pair<Int, Int>>) {
        drawCustomOverlay(gui)
        chestProfits.getOrNull(0)?.let { (slot, profit) ->
            highlightSlot(gui, slot, mostProfitColor.value, false)
            writeAboveSlot(gui, slot, profit.toCoins(), mostProfitColor.value)
        }
        chestProfits.getOrNull(1)?.let { (slot, profit) ->
            if (showNegativeProfit.value || profit < 0) {
                if (profit < 0) {
                    recolorSlot(gui, slot, Color(255, 0, 0 ))
                }
                highlightSlot(gui, slot, secondMostProfitColor.value, false)
                writeBelowSlot(gui, slot, profit.toCoins(), secondMostProfitColor.value)
            }
        }
    }

    private fun calculateChestProfitForGui(gui: GuiContainer): Pair<Int, Int>? {
        var openChestSlot = -1
        val cost = mutableListOf<String>()
        val loot = mutableListOf<String>()

        forEachValidSlot(gui) { slot, stack ->
            val name = stack.cleanName()
            when {
                name == "Open Reward Chest" -> {
                    openChestSlot = slot.slotNumber
                    cost += stack.getChestContentsAndCost().second
                }
                name.contains("Essence") -> loot += name
                else -> {
                    var id = getSkyblockItemID(stack)
                    if (id.isNullOrBlank()) id = stack.displayName.removeFormatting()
                    loot += id
                }
            }
        }
        return if (openChestSlot >= 0) openChestSlot to calculateChestProfit(loot, cost) else null
    }

    private fun drawChestProfit(gui: GuiContainer, slot: Int, profit: Int) {
        drawCustomOverlay(gui)
        writeBelowSlot(gui, slot, profit.toCoins(), mostProfitColor.value)
    }

    private fun drawCustomOverlay(gui: GuiContainer) {
        if (!customGui.value) return
        forEachSlot(gui, true) { slot, stack ->
            recolorSlot(gui, slot.slotNumber, customGuiColor.value, stack?.displayName == " ")
        }
    }

    private fun calculateChestProfit(loot: List<String>, costs: List<String>): Int {
        val essencePattern = """(Wither|Undead) Essence x(\d+)""".toRegex()
        val totalLootValue = loot.sumOf { raw ->
            val m = essencePattern.find(raw)
            val (baseName, qty) = if (m != null) {
                "${m.groupValues[1]} Essence" to m.groupValues[2].toInt()
            } else {
                raw.substringBefore(" x") to 1
            }
            val sbId = getSkyblockIdFromName(baseName)
            if (!uselessDungeonLoot.contains(sbId) && !sbId.isBlank()) {
                qty * getPrice(sbId, sellOffer.value, includeTaxes.value)
            } else 0
        }
        val totalCost = costs.sumOf { cost ->
            when {
                cost == "Dungeon Chest Key" && includeKeyPrice.value ->
                    getPrice(getSkyblockIdFromName(cost), sellOffer.value, includeTaxes.value)
                cost != "FREE" -> cost.replace(",", "").removeSuffix(" Coins").toInt()
                else -> 0
            }
        }
        return totalLootValue - totalCost
    }

    private fun ItemStack.getChestContentsAndCost(): Pair<List<String>, List<String>> {
        val tooltip = getTooltip(mc.thePlayer, false)
            .map { EnumChatFormatting.getTextWithoutFormattingCodes(it).replaceLvlTag(it) }
        return tooltip.sectionBetween("Contents", "Cost") to tooltip.sectionAfter("Cost")
    }

    private fun String.replaceLvlTag(raw: String): String =
        if (contains("[Lvl 1]")) when {
            raw.contains("§6") -> replace("[Lvl 1]", "[Leg]")
            raw.contains("§5") -> replace("[Lvl 1]", "[Epic]")
            else -> this
        } else this

    private fun List<String>.sectionBetween(start: String, end: String) =
        dropWhile { it != start }.drop(1).takeWhile { it != end && it.isNotBlank() }

    private fun List<String>.sectionAfter(start: String) =
        dropWhile { it != start }.drop(1).takeWhile { it.isNotBlank() }

    private fun ItemStack.cleanName() = displayName.removeFormatting()

    private fun Int.toCoins(): String = "${numberFormatter.format(this)} Coins"

    private inline fun forEachValidSlot(
        gui: GuiContainer,
        action: (slot: net.minecraft.inventory.Slot, stack: ItemStack) -> Unit
    ) {
        forEachSlot(gui, includeUseless = false) { slot, stack ->
            if (stack != null) action(slot, stack)
        }
    }

    private inline fun forEachSlot(
        gui: GuiContainer,
        includeUseless: Boolean = false,
        action: (slot: net.minecraft.inventory.Slot, stack: ItemStack?) -> Unit
    ) {
        val maxSlot = gui.inventorySlots.inventory.size - 37
        for (slot in gui.inventorySlots.inventorySlots) {
            if (slot.slotNumber <= maxSlot) {
                val stack = slot.stack
                if (!includeUseless && stack == null) continue
                if (!includeUseless && stack?.cleanName() in GuiUselessItems) continue
                action(slot, stack)
            }
        }
    }


    private fun getSkyblockIdFromName(name: String) = nameToIdMap[name] ?: name

    private val numberFormatter = NumberFormat.getNumberInstance(Locale.US)

    private val CroesusMenuGuiTitle = "Croesus"

    private val CroesusChestsGuiTitle = listOf(
        "Master Mode The Catacombs - Flo",
        "The Catacombs - Floor I",
        "The Catacombs - Floor II",
        "The Catacombs - Floor III",
        "The Catacombs - Floor IV",
        "The Catacombs - Floor V",
        "The Catacombs - Floor VI",
        "The Catacombs - Floor VII",
    )

    private val DungeonChestsGuiTitle = listOf(
        "Wood Chest",
        "Gold Chest",
        "Diamond Chest",
        "Emerald Chest",
        "Obsidian Chest",
        "Bedrock Chest",
    )

    private val GuiUselessItems = listOf(
        " ",
        "Close",
        "Next Page",
        "Previous Page",
        "Go Back",
        "Reroll Chest",
    )

    private val uselessDungeonLoot = listOf(
        "Enchanted Book (Feather Falling VI)",
        "ENCHANTMENT_FEATHER_FALLING_6",
        "Enchanted Book (Feather Falling VII)",
        "ENCHANTMENT_FEATHER_FALLING_7",
        "Enchanted Book (Infinite Quiver VI)",
        "ENCHANTMENT_INFINITE_QUIVER_6",
        "Enchanted Book (Infinite Quiver VII)",
        "ENCHANTMENT_INFINITE_QUIVER_7",
        "Enchanted Book (Bank I)",
        "ENCHANTMENT_ULTIMATE_BANK_1",
        "Enchanted Book (Bank II)",
        "ENCHANTMENT_ULTIMATE_BANK_2",
        "Enchanted Book (Bank III)",
        "ENCHANTMENT_ULTIMATE_BANK_3",
        "Enchanted Book (Ultimate Jerry I)",
        "ENCHANTMENT_ULTIMATE_JERRY_1",
        "Enchanted Book (Ultimate Jerry II)",
        "ENCHANTMENT_ULTIMATE_JERRY_2",
        "Enchanted Book (Ultimate Jerry III)",
        "ENCHANTMENT_ULTIMATE_JERRY_3",
        "Enchanted Book (No Pain No Gain I)",
        "ENCHANTMENT_ULTIMATE_NO_PAIN_NO_GAIN_1",
        "Enchanted Book (No Pain No Gain II)",
        "ENCHANTMENT_ULTIMATE_NO_PAIN_NO_GAIN_2",
        "Enchanted Book (No Pain No Gain III)",
        "ENCHANTMENT_ULTIMATE_NO_PAIN_NO_GAIN_3",
        "Enchanted Book (Combo I)",
        "ENCHANTMENT_ULTIMATE_COMBO_1",
        "Enchanted Book (Combo II)",
        "ENCHANTMENT_ULTIMATE_COMBO_2",
        "Enchanted Book (Combo III)",
        "ENCHANTMENT_ULTIMATE_COMBO_3",
        "Dungeon Disc",
        "DUNGEON_DISC_1",
        "Clown Disc",
        "DUNGEON_DISC_2",
        "Necron Disc",
        "DUNGEON_DISC_5",
        "Watcher Disc",
        "DUNGEON_DISC_4",
        "Old Disc",
        "DUNGEON_DISC_3",
        "Goldor the Fish",
        "GOLDOR_THE_FISH",
        "Maxor the Fish",
        "MAXOR_THE_FISH",
        "Storm the Fish",
        "STORM_THE_FISH",
    )

    private val nameToIdMap = mapOf(
        "Dungeon Chest Key" to "DUNGEON_CHEST_KEY",

        "Wither Essence" to "ESSENCE_WITHER",
        "Undead Essence" to "ESSENCE_UNDEAD",

        "Enchanted Book (Rejuvenate I)" to "ENCHANTMENT_REJUVENATE_1",
        "Enchanted Book (Rejuvenate II)" to "ENCHANTMENT_REJUVENATE_2",
        "Enchanted Book (Rejuvenate III)" to "ENCHANTMENT_REJUVENATE_3",
        "Enchanted Book (Ultimate Wise I)" to "ENCHANTMENT_ULTIMATE_WISE_1",
        "Enchanted Book (Ultimate Wise II)" to "ENCHANTMENT_ULTIMATE_WISE_2",
        "Enchanted Book (Ultimate Wise III)" to "ENCHANTMENT_ULTIMATE_WISE_3",
        "Enchanted Book (Wisdom I)" to "ENCHANTMENT_ULTIMATE_WISDOM_1",
        "Enchanted Book (Wisdom II)" to "ENCHANTMENT_ULTIMATE_WISDOM_2",
        "Enchanted Book (Wisdom III)" to "ENCHANTMENT_ULTIMATE_WISDOM_3",
        "Enchanted Book (Last Stand I)" to "ENCHANTMENT_ULTIMATE_LAST_STAND_1",
        "Enchanted Book (Last Stand II)" to "ENCHANTMENT_ULTIMATE_LAST_STAND_2",
        "Enchanted Book (Rend I)" to "ENCHANTMENT_ULTIMATE_REND_1",
        "Enchanted Book (Rend II)" to "ENCHANTMENT_ULTIMATE_REND_2",
        "Enchanted Book (Legion I)" to "ENCHANTMENT_ULTIMATE_LEGION_1",
        "Enchanted Book (Lethality VI)" to "ENCHANTMENT_LETHALITY_6",
        "Enchanted Book (Overload I)" to "ENCHANTMENT_OVERLOAD_1",
        "Enchanted Book (Swarm I)" to "ENCHANTMENT_ULTIMATE_SWARM_1",
        "Enchanted Book (Soul Eater I)" to "ENCHANTMENT_ULTIMATE_SOUL_EATER_1",
        "Enchanted Book (One For All I)" to "ENCHANTMENT_ULTIMATE_ONE_FOR_ALL_1",
        "Enchanted Book (Thunderlord VII)" to "ENCHANTMENT_THUNDERLORD_7",

        "Hot Potato Book" to "HOT_POTATO_BOOK",
        "Fuming Potato Book" to "FUMING_POTATO_BOOK",
        "Recombobulator 3000" to "RECOMBOBULATOR_3000",

        "First Master Star" to "FIRST_MASTER_STAR",
        "Second Master Star" to "SECOND_MASTER_STAR",
        "Third Master Star" to "THIRD_MASTER_STAR",
        "Fourth Master Star" to "FOURTH_MASTER_STAR",
        "Fifth Master Star" to "FIFTH_MASTER_STAR",

        "Power Dragon" to "SHARD_POWER_DRAGON",
        "Apex Dragon" to "SHARD_APEX_DRAGON",
        "Wither" to "SHARD_WITHER",
        "Thorn" to "SHARD_THORN",
        "Power Dragon Shard" to "SHARD_POWER_DRAGON",
        "Apex Dragon Shard" to "SHARD_APEX_DRAGON",
        "Wither Shard" to "SHARD_WITHER",
        "Thorn Shard" to "SHARD_THORN",

        "Master Skull - Tier 1" to "MASTER_SKULL_TIER_1",
        "Master Skull - Tier 2" to "MASTER_SKULL_TIER_2",
        "Master Skull - Tier 3" to "MASTER_SKULL_TIER_3",
        "Master Skull - Tier 4" to "MASTER_SKULL_TIER_4",
        "Master Skull - Tier 5" to "MASTER_SKULL_TIER_5",

        "Necromancer's Brooch" to "NECROMANCER_BROOCH",
        "Bonzo's Mask" to "BONZO_MASK",
        "Red Nose" to "RED_NOSE",
        "Bonzo's Staff" to "BONZO_STAFF",
        "Balloon Snake" to "BALLOON_SNAKE",

        "Scarf's Studies" to "SCARF_STUDIES",
        "Adaptive Blade" to "STONE_BLADE",
        "Red Scarf" to "RED_SCARF",
        "Adaptive Helmet" to "ADAPTIVE_HELMET",
        "Adaptive Chestplate" to "ADAPTIVE_CHESTPLATE",
        "Adaptive Leggings" to "ADAPTIVE_LEGGINGS",
        "Adaptive Boots" to "ADAPTIVE_BOOTS",
        "Adaptive Belt" to "ADAPTIVE_BELT",
        "Suspicious Vial" to "SUSPICIOUS_VIAL",

        "Spirit Bone" to "SPIRIT_BONE",
        "Spirit Boots" to "THORNS_BOOTS",
        "Spirit Stone" to "SPIRIT_DECOY ",
        "Spirit Bow" to "ITEM_SPIRIT_BOW",
        "Spirit Sword" to "SPIRIT_SWORD",
        "Spirit Wing" to "SPIRIT_WING",
        "[Leg] Spirit" to "SPIRIT;4",
        "[Epic] Spirit" to "SPIRIT;3",

        "Dark Orb" to "DARK_ORB",
        "Shadow Assassin Helmet" to "SHADOW_ASSASSIN_HELMET",
        "Shadow Assassin Chestplate" to "SHADOW_ASSASSIN_CHESTPLATE",
        "Shadow Assassin Leggings" to "SHADOW_ASSASSIN_LEGGINGS",
        "Shadow Assassin Boots" to "SHADOW_ASSASSIN_BOOTS",
        "Warped Stone" to "AOTE_STONE",
        "Last Breath" to "LAST_BREATH",
        "Livid Dagger" to "LIVID_DAGGER",
        "Shadow Fury" to "SHADOW_FURY",
        "Shadow Assassin Cloak" to "SHADOW_ASSASSIN_CLOAK",
        "Livid Dye" to "DYE_LIVID",

        "Giant Tooth" to "GIANT_TOOTH",
        "Ancient Rose" to "GOLEM_POPPY",
        "Necromancer Lord Helmet" to "NECROMANCER_LORD_HELMET",
        "Necromancer Lord Chestplate" to "NECROMANCER_LORD_CHESTPLATE",
        "Necromancer Lord Leggings" to "NECROMANCER_LORD_LEGGINGS",
        "Necromancer Lord Boots" to "NECROMANCER_LORD_BOOTS",
        "Sadan's Brooch" to "SADAN_BROOCH",
        "Necromancer Sword" to "NECROMANCER_SWORD",
        "Summoning Ring" to "SUMMONING_RING",
        "Giant's Sword" to "GIANTS_SWORD",
        "Precursor Eye" to "PRECURSOR_EYE",
        "Fel Skull" to "FEL_SKULL",
        "Soulweaver Gloves" to "SOULWEAVER_GLOVES",

        "Precursor Gear" to "PRECURSOR_GEAR",
        "Wither Catalyst" to "WITHER_CATALYST",
        "Wither Helmet" to "WITHER_HELMET",
        "Wither Chestplate" to "WITHER_CHESTPLATE",
        "Wither Leggings" to "WITHER_LEGGINGS",
        "Wither Boots" to "WITHER_BOOTS",
        "Wither Blood" to "WITHER_BLOOD",
        "Auto Recombobulator" to "AUTO_RECOMBOBULATOR",
        "Necron's Handle" to "NECRON_HANDLE",
        "Implosion" to "IMPLOSION_SCROLL",
        "Shadow Warp" to "SHADOW_WARP_SCROLL",
        "Wither Shield" to "WITHER_SHIELD_SCROLL",
        "Wither Cloak Sword" to "WITHER_CLOAK",
        "Dark Claymore" to "DARK_CLAYMORE",
        "Necron Dye" to "DYE_NECRON",

        "Shiny Necron's Handle" to "SHINY_NECRON_HANDLE",
        "Shiny Wither Helmet" to "SHINY_WITHER_HELMET",
        "Shiny Wither Chestplate" to "SHINY_WITHER_CHESTPLATE",
        "Shiny Wither Leggings" to "SHINY_WITHER_LEGGINGS",
        "Shiny Wither Boots" to "SHINY_WITHER_BOOTS",
    )
}