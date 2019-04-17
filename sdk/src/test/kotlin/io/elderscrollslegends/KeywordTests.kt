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

class KeywordTests {
    private lateinit var client: Client

    @BeforeEach
    fun before() {
        client = mockk()
        UnirestInitializer.setClient(client)
    }

    private val response = mockk<HttpResponse<JsonNode>>()

    @Test
    fun `all() returns all keywords`() {
        val keywords = String(this::class.java.getResource("/keywords.json").readBytes())
        every { client.request(any(), any<Function<RawResponse, HttpResponse<JsonNode>>>()) } returns response
        every { response.isSuccess } returns true
        every { response.body } returns JsonNode(keywords)

        // when
        val allKeywords = Keyword.all()

        // then
        assertThat(allKeywords).isEqualTo(
            listOf(
                "Assemble",
                "Betray",
                "Breakthrough",
                "Charge",
                "Drain",
                "Exalt",
                "Guard",
                "Last Gasp",
                "Lethal",
                "Pilfer",
                "Plot",
                "Prophecy",
                "Rally",
                "Regenerate",
                "Shackle",
                "Silence",
                "Slay",
                "Treasure Hunt",
                "Ward"
            )
        )
    }

}
