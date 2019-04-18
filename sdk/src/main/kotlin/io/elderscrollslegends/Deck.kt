package io.elderscrollslegends

class Deck(
    cards: List<Card> = emptyList()
): CardGrouping(cards) {

    fun exportCode(idMapper: (cardId: String) -> String = { Decoder.idToCodeMap.getOrDefault(it, "__") }): String {
        return Decoder(DecoderType.DECK, idMapper).createExportCode(this)
    }

    companion object {
        @JvmStatic
        fun importCode(code: String)= Deck(Decoder(DecoderType.DECK).createListFromCode(code))
    }
}
