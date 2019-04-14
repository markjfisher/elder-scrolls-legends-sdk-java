package io.elderscrollslegends

import io.mockk.every
import io.mockk.mockk
import kong.unirest.Client
import kong.unirest.HttpResponse
import kong.unirest.JsonNode
import kong.unirest.RawResponse
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.function.Function

class DecoderTests {
    private val client = mockk<Client>()
    private val response = mockk<HttpResponse<JsonNode>>()
    init {
        UnirestClient(client = client)
    }

    @Test
    fun `should decode a string to the card name`() {
        val deckImportData = String(this::class.java.getResource("/deck-import.json").readBytes())
        every { client.request(any(), any<Function<RawResponse, HttpResponse<JsonNode>>>()) } returns response
        every { response.isSuccess } returns true
        every { response.body } returnsMany listOf(
            JsonNode("{'url':'testHash','title':''}"),
            JsonNode(deckImportData)
        )

        val decoder = Decoder()
        assertThat(decoder.getNameFromCode("xx")).isEqualTo("Test Card Name")

    }

}
