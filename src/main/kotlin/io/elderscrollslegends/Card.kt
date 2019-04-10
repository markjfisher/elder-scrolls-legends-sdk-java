package io.elderscrollslegends

import com.fasterxml.jackson.annotation.JsonProperty

data class Card (
    val name: String,
    val rarity: String,
    val type: String,
    val subtypes: List<String> = emptyList(),
    val cost: Int,
    val power: Int,
    val health: Int,
    val set: CardSet,
    val collectible: Boolean,
    val soulSummon: Int,
    val soulTrap: Int,
    val text: String,
    val attributes: List<String> = emptyList(),
    val keywords: List<String> = emptyList(),
    val unique: Boolean,
    val imageUrl: String,
    val id: String
) {
    // We should always honour the totals even in a where clause.
    // all() is a where with no no predicates
    companion object {
        private val unirestClient = UnirestClient()
        fun all(): List<Card> {
            return where(emptyMap())
        }

        fun find(id: String) : Card? {
            return unirestClient.find(resource = "cards", id = id, cls = CardSingle::class.java)?.card
        }

        fun where(predicates: Map<String, String>): List<Card> {
            val adjustedPredicates = predicates.toMutableMap()

            val singlePageOnly = predicates.containsKey("page")

            val page = adjustedPredicates.getOrDefault("page", "1")
            adjustedPredicates["page"] = page

            val cards = unirestClient.get(resource = "cards", cls = Cards::class.java, queryParams = adjustedPredicates)
                    ?: return emptyList()

            val results = mutableListOf<Card>()
            results.addAll(cards.cards)

            if (singlePageOnly) return results

            // now we have cards.pageSize and cards.totalCount and predicates for page number we can gather all the cards
            val totalPageCount = cards.totalCount / cards.pageSize + if (cards.totalCount % cards.pageSize == 0) 0 else 1

            for (nextPage in (page.toInt() + 1)..totalPageCount) {
                adjustedPredicates["page"] = nextPage.toString()
                val nextCards = unirestClient.get(resource = "cards", cls = Cards::class.java, queryParams = adjustedPredicates)?.cards ?: emptyList()
                results.addAll(nextCards)
            }

            return results
        }
    }
}

data class Cards (
    val cards: List<Card>,
    @JsonProperty("_pageSize")
    val pageSize: Int,
    @JsonProperty("_totalCount")
    val totalCount: Int
)

data class CardSingle (
    val card: Card
)

data class CardSet (
    val name: String,
    val id: String,
    @JsonProperty("_self")
    val self: String
)