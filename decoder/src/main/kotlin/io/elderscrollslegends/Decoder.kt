package io.elderscrollslegends

import com.natpryce.konfig.*
import org.jsoup.Jsoup

class Decoder {
    private val uri = Key("legends-decks.uri", stringType)

    private val config = ConfigurationProperties.systemProperties() overriding
            EnvironmentVariables() overriding
            ConfigurationProperties.fromResource("defaults.properties")

    private val unirestClient = UnirestClient(uriPath = config[uri])

    private val defaultHeaders = mapOf(
        "origin" to "https://www.legends-decks.com",
        "accept-encoding" to "gzip, deflate, br",
        "accept-language" to "en-GB,en;q=0.9,en-US;q=0.8,fr;q=0.7",
        "x-requested-with" to "XMLHttpRequest",
        "pragma" to "no-cache",
        "content-type" to "application/x-www-form-urlencoded; charset=UTF-8",
        "accept" to "application/json, text/javascript, */*; q=0.01",
        "cache-control" to "no-cache",
        "authority" to "www.legends-decks.com",
        "referer" to "https://www.legends-decks.com/deck-builder"
    )

    private fun createImportFields(decodeString: String): Map<String, String> {
        return mapOf("import_desc" to decodeString)
    }

    private fun createDeckFields(hash: String): Map<String, String> {
        return mapOf("hash" to "#$hash")
    }

    fun getNameFromCode(code: String): String {
        val decodeString = "SPAB${code}AAAA" // single card of given code
        val data1 = unirestClient.post(
            resource = "deck_import.php",
            fields = createImportFields(decodeString),
            headers = defaultHeaders,
            cls = ImportData::class.java)

        if (data1 == null || data1.url.isBlank()) return ""

        val data2 = unirestClient.post(
            resource = "deck_builder_first_load.php",
            fields = createDeckFields(data1.url),
            headers = defaultHeaders,
            cls = DeckData::class.java)

        if (data2 == null || data2.deck.isBlank()) return ""

        return Jsoup.parse(data2.deck).select("span.name").text()
    }
}

data class ImportData(
    val url: String = "",
    val title: String = ""
)

data class DeckData(
    val deck: String
)