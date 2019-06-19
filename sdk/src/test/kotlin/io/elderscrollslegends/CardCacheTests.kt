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

class CardCacheTests {
    private lateinit var client: Client
    private val cardId = "06a4d55a15a48361f1d5d7c2fe50563f1fa82408"
    private var cardJson = String(this::class.java.getResource("/card-$cardId.json").readBytes())

    @BeforeEach
    fun before() {
        client = mockk()
        UnirestInitializer.setClient(client)
        every { client.request(any(), any<Function<RawResponse, HttpResponse<JsonNode>>>()) } returns response
        every { response.isSuccess } returns true
    }

    private val response = mockk<HttpResponse<JsonNode>>()

    @Test
    fun `find Card via cache by id`() {
        every { response.body } returns JsonNode(cardJson)

        // When
        val foundCardById = CardCache.findById(cardId)

        // Then
        assertThat(foundCardById).isNotNull
        assertThat(foundCardById!!.name).isEqualTo("Allena Benoch")

        // And it is in the cache
        assertThat(CardCache.hasCard(cardId)).isTrue()
    }

    @Test
    fun `find Card via cache by code`() {
        every { response.body } returns JsonNode(cardJson)

        // When
        val foundCardByCode = CardCache.findByCode("as")

        // Then
        assertThat(foundCardByCode).isNotNull
        assertThat(foundCardByCode!!.name).isEqualTo("Allena Benoch")

        // And it is in the cache
        assertThat(CardCache.hasCard(cardId)).isTrue()
    }

    @Test
    fun `can load all cards into cache`() {
        val cards1 = String(this::class.java.getResource("/cards-page1.json").readBytes())
        val cards2 = String(this::class.java.getResource("/cards-page2.json").readBytes())
        val cards3 = String(this::class.java.getResource("/cards-page3.json").readBytes())
        every { response.body } returnsMany listOf(JsonNode(cards1), JsonNode(cards2), JsonNode(cards3))

        // when
        CardCache.load()

        // Then
        assertThat(CardCache.hasCard("7f62d718099821fc9945af326ef29f406f039f71")).isTrue()
        assertThat(CardCache.hasCard("54798ac9d0703e215316a302b98f5a35e349d553")).isTrue()
        assertThat(CardCache.hasCard("3617a6f2914cb54376fef96fa256f2f4f8434707")).isTrue()
        assertThat(CardCache.hasCard("74e2126b539b088dce67e98d0948aa7bf55e74f8")).isTrue()
        assertThat(CardCache.hasCard("7b42596f980d3cb5bda27f8b6de58f91d10c7f0b")).isTrue()
        assertThat(CardCache.hasCard("06a4d55a15a48361f1d5d7c2fe50563f1fa82408")).isTrue()

    }
}