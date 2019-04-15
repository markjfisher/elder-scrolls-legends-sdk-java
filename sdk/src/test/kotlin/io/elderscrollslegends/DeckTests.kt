package io.elderscrollslegends

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class DeckTests {
    @Test
    fun `can add and retrieve cards from a deck`() {
        val card1 = Card(name = "card1", id = "1")
        val card2 = Card(name = "card2", id = "2")
        val card3 = Card(name = "card3", id = "3")
        val card4 = Card(name = "card4", id = "4")
        val deck = Deck(listOf(card4, card1, card3, card4, card2, card3, card1, card3, card4))

        assertThat(deck.of(0)).isEmpty()
        assertThat(deck.of(1)).containsExactly(card2)
        assertThat(deck.of(2)).containsExactly(card1)
        assertThat(deck.of(3)).containsExactly(card3, card4) // the returned list is sorted by name
        assertThat(deck.of(4)).isEmpty()

    }
}