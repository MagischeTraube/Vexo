package io.github.vexo.utils.skyblock

import net.minecraft.item.ItemStack
import net.minecraft.util.EnumChatFormatting

fun ItemStack.getItemLore(): List<String> {
    val tooltip = mutableListOf<String>()

    if (this.hasDisplayName()) {
        tooltip.add(this.displayName)
    }
    tooltip.addAll(
        this.getTooltip(net.minecraft.client.Minecraft.getMinecraft().thePlayer, false)
            .map { EnumChatFormatting.getTextWithoutFormattingCodes(it) }
    )
    return tooltip
}


private val SHINY_ITEMS = listOf(
    "NECRON_HANDLE",
    "WITHER_HELMET",
    "WITHER_CHESTPLATE",
    "WITHER_LEGGINGS",
    "WITHER_BOOTS",
)

private val RARITY_MAP = mapOf(
    "COMMON" to 0,
    "UNCOMMON" to 1,
    "RARE" to 2,
    "EPIC" to 3,
    "LEGENDARY" to 4,
    "MYTHIC" to 5
)

fun getSkyblockItemID(stack: ItemStack?): String? {
    if (stack == null || !stack.hasTagCompound()) return null

    val tag = stack.tagCompound ?: return null
    val extraAttributes = tag.getCompoundTag("ExtraAttributes")

    var itemID = extraAttributes.getString("id")
    if (itemID.isEmpty()) return null


    // Shiny Items
    val isShiny = extraAttributes.getBoolean("is_shiny")
    if (isShiny && SHINY_ITEMS.contains(itemID)) {
        itemID = "SHINY_$itemID"
    }

    // Pets
    val petInfo = extraAttributes.getString("petInfo")
    if (petInfo.isNotEmpty()) {
        val tierRegex = """"tier"\s*:\s*"(\w+)"""".toRegex()
        val typeRegex = """"type"\s*:\s*"(\w+)"""".toRegex()
        val tierMatch = tierRegex.find(petInfo)?.groups?.get(1)?.value ?: "COMMON"
        val typeMatch = typeRegex.find(petInfo)?.groups?.get(1)?.value ?: "PET"
        val rarity = RARITY_MAP[tierMatch.uppercase()] ?: 0
        return "$typeMatch;$rarity"
    }

    // Enchanted Books
    if (itemID == "ENCHANTED_BOOK") {
        val enchantments = extraAttributes.getCompoundTag("enchantments")
        val keys = enchantments.keySet
        if (keys.isEmpty()) return null

        val enchantment = keys.first()
        val level = enchantments.getInteger(enchantment)
        return "ENCHANTMENT_${enchantment.uppercase()}_${level}"
    }

    if (itemID == "ATTRIBUTE_SHARD") {
        itemID = getShardTyp(stack.displayName)
    }

    return itemID
}

fun getShardTyp(name: String) = shardToIDMap[name] ?: name

private val shardToIDMap = mapOf(
    "Power Dragon Shard" to "SHARD_POWER_DRAGON",
    "Apex Dragon Shard" to "SHARD_APEX_DRAGON",
    "Wither Shard" to "SHARD_WITHER",
    "Thorn Shard" to "SHARD_THORN",
)
