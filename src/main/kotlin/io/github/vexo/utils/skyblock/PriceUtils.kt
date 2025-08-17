package io.github.vexo.utils.skyblock

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import io.github.vexo.events.ServerTickEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.io.File
import java.net.URL
import java.util.concurrent.ConcurrentHashMap

object PriceUtils {

    private val ignoreList = listOf(
        "NEW_YEAR_CAKE",
        "ATTRIBUTE",
    )

    private val gson = Gson()
    private const val BAZAAR_URL = "https://api.hypixel.net/skyblock/bazaar"
    private const val LOWESTBIN_URL = "https://moulberry.codes/lowestbin.json"
    private val PRICE_DATA_FILE = File("config/Vexo/PRICE_DATA.json")
    private var lastFetchTime: Long = 0
    private const val FETCH_INTERVAL_MS = 20 * 60 * 1000L // 20 mins

    private val SHINY_ITEMS = listOf(
        "NECRON_HANDLE",
        "WITHER_HELMET",
        "WITHER_CHESTPLATE",
        "WITHER_LEGGINGS",
        "WITHER_BOOTS",
    )

    private val cachedPriceData = ConcurrentHashMap<String, PriceData>()

    private data class PriceBackup(
        val lastFetchTime: Long,
        val prices: Map<String, PriceData>
    )

    data class PriceData(
        val sellLocation: String,
        val buyPrice: Int? = null,
        val sellPrice: Int? = null,
        val lowestBin: Int? = null
    )

    init {
        loadCachedPriceData()
    }

    @SubscribeEvent
    fun onServerTick(event: ServerTickEvent) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastFetchTime >= FETCH_INTERVAL_MS) {
            fetchPrices()
            lastFetchTime = currentTime
        }
    }

    private fun fetchPrices() {
        Thread {
            try {
                safeApiCall("LowestBIN") {
                    parseLowestBin(cachedPriceData)
                }

                safeApiCall("Shiny LowestBIN") {
                    parseShinyLowestBin(cachedPriceData)
                }

                safeApiCall("Bazaar") {
                    parseBazaar(cachedPriceData)
                }

                saveCachedPriceData()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }


    private fun safeApiCall(name: String, block: () -> Unit) {
        val retries = 3
        val delayMs = 3000L

        repeat(retries) { attempt ->
            try {
                block()
                return
            } catch (e: Exception) {
                println("Error $name (try ${attempt + 1}/$retries): ${e.message}")
                if (attempt < retries - 1) Thread.sleep(delayMs)
            }
        }
        println("$name no API respond... use old data")
    }

    private fun parseShinyLowestBin(newPriceData: ConcurrentHashMap<String, PriceData>) {
        for (item in SHINY_ITEMS) {
            try {
                Thread.sleep(500)

                val itemJson = fetchJson(
                    "https://sky.coflnet.com/api/item/price/$item/bin?query%5BIsShiny%5D=true"
                )

                if (itemJson.has("lowest") && !itemJson.get("lowest").isJsonNull) {
                    val lowestPrice = itemJson.get("lowest").asInt
                    newPriceData["SHINY_$item"] = PriceData(
                        sellLocation = "auction_house",
                        lowestBin = lowestPrice
                    )
                }
            } catch (e: Exception) {
                println("Error fetch Price of $item: ${e.message}")
            }
        }
    }

    fun calculateBinAfterTaxes(price: Double): Double {
        if (price <= 0) return 0.0

        val startFee = when {
            price > 100_000_000 -> price * 0.025
            price >= 10_000_000 -> price * 0.02
            else -> price * 0.01
        }

        var afterStartFee = price - startFee

        if (price > 1_000_000) {
            val collectTax = afterStartFee * 0.01
            afterStartFee -= collectTax

            if (afterStartFee < 1_000_000) {
                afterStartFee = 1_000_000.0
            }
        }

        return afterStartFee
    }

    private fun parseLowestBin(priceData: ConcurrentHashMap<String, PriceData>) {
        val lowestbinJson = fetchJson(LOWESTBIN_URL)
        for ((key, value) in lowestbinJson.entrySet()) {
            if (ignoreList.none { key.contains(it, ignoreCase = true) }) {
                priceData[key] = PriceData(
                    sellLocation = "auction_house",
                    lowestBin = value.asInt
                )
            }
        }
    }

    private fun parseBazaar(priceData: ConcurrentHashMap<String, PriceData>) {
        val bazaarJson = fetchJson(BAZAAR_URL)
        val products = bazaarJson.getAsJsonObject("products")
        for ((productId, productDataElement) in products.entrySet()) {
            if (ignoreList.none { productId.contains(it, ignoreCase = true) }) {
                val quickStatus = productDataElement.asJsonObject.getAsJsonObject("quick_status")
                priceData[productId] = PriceData(
                    sellLocation = "bazaar",
                    buyPrice = quickStatus.get("buyPrice").asInt,
                    sellPrice = quickStatus.get("sellPrice").asInt
                )
            }
        }
    }

    private fun fetchJson(urlString: String): JsonObject {
        val connection = URL(urlString).openConnection() as java.net.HttpURLConnection
        connection.setRequestProperty("User-Agent", "Mozilla/5.0")
        connection.connectTimeout = 5000
        connection.readTimeout = 5000

        connection.inputStream.use { stream ->
            return gson.fromJson(stream.reader(), JsonObject::class.java)
        }
    }

    fun getPrice(skyblockID: String, buyPrice: Boolean, includeTaxes: Boolean): Int {
        val itemData = cachedPriceData[skyblockID] ?: return 0
        val rawPrice = when (itemData.sellLocation) {
            "auction_house" -> itemData.lowestBin
            "bazaar" -> if (buyPrice) itemData.buyPrice else itemData.sellPrice
            else -> null
        } ?: return 0

        return if (itemData.sellLocation == "auction_house" && includeTaxes) {
            calculateBinAfterTaxes(rawPrice.toDouble()).toInt()
        } else {
            rawPrice
        }
    }

    fun saveCachedPriceData() {
        try {
            PRICE_DATA_FILE.parentFile?.mkdirs()
            val gson = GsonBuilder().setPrettyPrinting().create()
            val backup = PriceBackup(
                lastFetchTime = lastFetchTime,
                prices = cachedPriceData.toMap()
            )
            PRICE_DATA_FILE.writeText(gson.toJson(backup))
            println("${PRICE_DATA_FILE.path} saved!")
        } catch (e: Exception) {
            println("PriceUtils: ${e.message}")
            e.printStackTrace()
        }
    }


    private fun loadCachedPriceData() {
        if (PRICE_DATA_FILE.exists()) {
            try {
                val type = object : com.google.gson.reflect.TypeToken<PriceBackup>() {}.type
                val backup: PriceBackup = gson.fromJson(PRICE_DATA_FILE.readText(), type)
                cachedPriceData.putAll(backup.prices)
                lastFetchTime = backup.lastFetchTime
                println("Price data loaded from backup (${cachedPriceData.size} items). Last fetch: ${java.util.Date(lastFetchTime)}")
            } catch (e: Exception) {
                println("Could not load price_data file: ${e.message}")
            }
        }
    }

}
