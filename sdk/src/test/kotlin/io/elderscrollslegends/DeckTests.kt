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

class DeckTests {
    private lateinit var client: Client

    @BeforeEach
    fun before() {
        client = mockk()
        UnirestInitializer.setClient(client)
    }

    private fun mapper(cardId: String): String = cardId.reversed() // simple test mapper

    private val card1 = Card(name = "card1", id = "c1")
    private val card2 = Card(name = "card2", id = "c2")
    private val card3 = Card(name = "card3", id = "c3")
    private val card4 = Card(name = "card4", id = "c4")
    private val deck = Deck(cards = listOf(card4, card1, card3, card4, card2, card3, card1, card3, card4))

    @Test
    fun `can retrieve cards from a deck by count`() {
        assertThat(deck.of(0)).isEmpty()
        assertThat(deck.of(1)).containsExactly(card2)
        assertThat(deck.of(2)).containsExactly(card1)
        assertThat(deck.of(3)).containsExactly(card3, card4) // the returned list is sorted by name
        assertThat(deck.of(4)).isEmpty()
    }

    @Test
    fun `can create a code for a deck of cards`() {
        assertThat(deck.exportCode {mapper(it)}).isEqualTo("SPAB2cAB1cAC3c4c")
    }

    @Test
    fun `real map data returns expected values`() {
        val card1 = Card(name = "A Land Divided", id = "410c046c044f0d6307eb51fc47136aa8378e9ac1") // aa
        val card2 = Card(name = "Chodala's Treachery", id = "0d7c1461dff962a09b32ce0f612b8995afca70ea") // dc
        assertThat(Deck(listOf(card1)).exportCode()).isEqualTo("SPABaaAAAA")
        assertThat(Deck(listOf(card1, card2, card2)).exportCode()).isEqualTo("SPABaaABdcAA")
    }

    @Test
    fun `finding cards in deck by id`() {
        assertThat(deck.byId("c1")).isEqualTo(CardCount(card = card1, count = 2))
        assertThat(deck.byId("c2")).isEqualTo(CardCount(card = card2, count = 1))
        assertThat(deck.byId("c3")).isEqualTo(CardCount(card = card3, count = 3))
        assertThat(deck.byId("c4")).isEqualTo(CardCount(card = card4, count = 3))
    }

    @Test
    fun `importing code returns a deck with correct cards`() {
        // Given
        val response = mockk<HttpResponse<JsonNode>>()
        val card1s = "{'card': {'name': 'card1', 'id': 'c1' }}"
        val card2s = "{'card': {'name': 'card2', 'id': 'c2' }}"
        val card3s = "{'card': {'name': 'card3', 'id': 'c3' }}"
        val card4s = "{'card': {'name': 'card4', 'id': 'c4' }}"
        every { client.request(any(), any<Function<RawResponse, HttpResponse<JsonNode>>>()) } returns response
        every { response.isSuccess } returns true
        // We respond with 4 cards for the 4 import codes "aa", "bb", "cc", "cc"
        every { response.body } returnsMany listOf(JsonNode(card1s), JsonNode(card2s), JsonNode(card3s), JsonNode(card4s))

        // When
        val deck2 = Deck.importCode("SPABaaABbbACccdd")

        // Then
        assertThat(deck2.byId("c1")).isEqualTo(CardCount(card = card1, count = 1))
        assertThat(deck2.byId("c2")).isEqualTo(CardCount(card = card2, count = 2))
        assertThat(deck2.byId("c3")).isEqualTo(CardCount(card = card3, count = 3))
        assertThat(deck2.byId("c4")).isEqualTo(CardCount(card = card4, count = 3))
        assertThat(deck2.byId("xx")).isEqualTo(CardCount())
    }

    @Test
    fun `importing invalid code returns empty Deck`() {
        assertThat(Deck.importCode("SPABABAB").cards.size).isEqualTo(0)
    }
}