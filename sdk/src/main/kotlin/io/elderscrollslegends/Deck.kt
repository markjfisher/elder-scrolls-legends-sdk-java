package io.elderscrollslegends

import org.json.JSONArray

class Deck(
    cards: List<Card> = emptyList(),
    val idMapper: (cardId: String) -> String = { idToCodeMap.getOrDefault(it, "__") }
) {

    // A ID of a card is the SDK id given to the card, e.g. "63fe4012f15619e80b6f77c5724bc751f50730de"
    // A code of a card is the game attributed export code for the card, e.g. "cC" used for import/export

    // A map of count to list of cards with that count in the deck, e.g. {1: [Adoring Fan, Aela the Huntress], 2: [Aldmeri Patriot]}
    private val countToCardListMap = cards
        .groupBy { it.id }                                     // Map<cardId, List<Card>>
        .map { it.value.size to it.value.first() }             // List<Pair<count, Card>>
        .groupBy { it.first }                                  // Map<count, List<Pair<count, Card>>>
        .map { it.key to it.value.map { p -> p.second }.sortedBy { card -> card.name } }      // List<Pair<count, List<Card (sorted by name)>>
        .toMap()                                               // Map<count, List<Card>>

    // A map of cardId to DeckCard
    private val deckCardMap = cards
        .groupBy { it.id }
        .map { it.value.first().id to DeckCard(card = it.value.first(), count = it.value.size) }
        .toMap()

    fun of(count: Int) = countToCardListMap.getOrDefault(count, emptyList())

    fun byId(cardId: String): DeckCard {
        return deckCardMap.getOrDefault(cardId, DeckCard())
    }

    fun exportCode(): String {
        return "SP" +
                (1..3).joinToString("") { i ->
                    createCountMaker(of(i).size) + of(i).joinToString("") { idMapper(it.id) }
                }
    }

    // Converts a number to base26 rooted at A for 0, ie. AA=0, AB=1, ... AZ=25, BA=26, ...
    private fun createCountMaker(length: Int): String {
        val low = (length % 26 + 65).toChar()
        val high = (length / 26 + 65).toChar()
        return "$high$low"
    }

    companion object {
        private val mapData = String(this::class.java.getResource("/decoder-map-all.json").readBytes())

        private val idToCodeMap = JSONArray(mapData)
            .let { 0.until(it.length()).map { i -> it.optJSONObject(i) } }
            .map { it.optString("id") to it.optString("code") }
            .toMap()

        private val codeToIdMap = JSONArray(mapData)
            .let { 0.until(it.length()).map { i -> it.optJSONObject(i) } }
            .map { it.optString("code") to it.optString("id") }
            .toMap()

        @JvmStatic
        fun importCode(code: String): Deck {
            // Minimum decodable string: SPAAAAAA, which is an empty deck.
            // Deck length must be even as everything is split into pairs of chars
            if (code.length < 8 || (code.length % 2 != 0)) return Deck()
            val cards = mutableListOf<Card>()

            val codeSeq = SeqOfString(code)

            val header = codeSeq.take(2)
            if (header != "SP") return Deck()

            cards.addAll(convertSeqToListOfCards(1, codeSeq))
            cards.addAll(convertSeqToListOfCards(2, codeSeq))
            cards.addAll(convertSeqToListOfCards(3, codeSeq))

            return Deck(cards = cards)
        }

        private fun convertSeqToListOfCards(of: Int, seq: SeqOfString): List<Card> {
            val count = decodeCountMarker(seq.take(2))
            val items = mutableListOf<Card>()
            repeat(0.until(count).count()) {
                val code = seq.take(2)
                val id = codeToIdMap.getOrDefault(code, "NOT_FOUND")
                val card = Card.find(id)
                val x = MutableList(of) { card }.mapNotNull { it }
                items.addAll(x)
            }
            return items
        }

        fun decodeCountMarker(code: String): Int {
            val low = code[1].toInt() - 65
            val high = (code[0].toInt() - 65) * 26
            return low + high
        }

    }

    private class SeqOfString(s: String) {
        private var seq = s.asSequence()

        fun take(n: Int): String {
            val s = seq.take(n).joinToString("")
            seq = seq.drop(2)
            return s
        }
    }

}

data class DeckCard(
    val card: Card? = null,
    val count: Int = 0
)