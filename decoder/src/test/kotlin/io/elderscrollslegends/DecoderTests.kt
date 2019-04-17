package io.elderscrollslegends

import io.mockk.every
import io.mockk.mockk
import kong.unirest.Client
import kong.unirest.HttpResponse
import kong.unirest.JsonNode
import kong.unirest.RawResponse
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.function.Function

class DecoderTests {
    private lateinit var client: Client

    @BeforeEach
    fun before() {
        client = mockk()
        UnirestInitializer.setClient(client)
    }

    private val response = mockk<HttpResponse<JsonNode>>()

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
