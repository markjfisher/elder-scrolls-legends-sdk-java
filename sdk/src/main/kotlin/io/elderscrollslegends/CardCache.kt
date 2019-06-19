package io.elderscrollslegends

class CardCache {
    companion object {
        private val cache = mutableMapOf<String, Card>()

        @JvmStatic
        fun load() {
            cache.clear()
            Card.all().forEach { card ->
                cache[card.id] = card
            }
        }

        @JvmStatic
        fun findById(id: String): Card? {
            return if (!hasCard(id)) {
                val card = Card.find(id)
                if (card != null) {
                    cache[id] = card
                }
                card
            } else {
                cache[id]
            }
        }

        @JvmStatic
        fun findByCode(code: String): Card? {
            val id = Decoder.codeToIdMap[code] ?: return null
            return findById(id)
        }

        fun hasCard(id: String): Boolean = cache.containsKey(id)
    }
}