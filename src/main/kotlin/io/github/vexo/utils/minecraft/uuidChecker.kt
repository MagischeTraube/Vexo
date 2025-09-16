package io.github.vexo.utils.minecraft

import com.google.gson.JsonParser
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

object MinecraftUUIDUtils {

    /**
     * Gets the UUID for a given Minecraft username
     * @param username The Minecraft username to look up
     * @return Formatted UUID string (e.g., "e603a079-85b7-479e-88aa-2a7a3584530b")
     * @throws Exception if the API request fails or username is invalid
     */
    fun getUUID(username: String): String {
        val url = URL("https://api.mojang.com/users/profiles/minecraft/$username")
        val connection = url.openConnection() as HttpURLConnection

        connection.requestMethod = "GET"
        connection.setRequestProperty("User-Agent", "MinecraftMod/1.0")

        when (connection.responseCode) {
            404 -> throw IllegalArgumentException("Username \"$username\" not found")
            200 -> {
                // Read response
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = reader.readText()
                reader.close()

                // Parse JSON response
                val jsonParser = JsonParser()
                val jsonObject = jsonParser.parse(response).asJsonObject
                val rawUuid = jsonObject.get("id").asString

                // Format UUID with dashes
                return formatUUID(rawUuid)
            }
            else -> throw Exception("API request failed with response code: ${connection.responseCode}")
        }
    }

    /**
     * Formats a raw UUID string by adding dashes
     * @param rawUuid Raw 32-character UUID string
     * @return Formatted UUID string with dashes
     */
    private fun formatUUID(rawUuid: String): String {
        if (rawUuid.length != 32) {
            return rawUuid
        }

        return rawUuid.replace(Regex("(.{8})(.{4})(.{4})(.{4})(.{12})"), "$1-$2-$3-$4-$5")
    }
}