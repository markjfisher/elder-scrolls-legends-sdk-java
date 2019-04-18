package io.elderscrollslegends

class Collection(
    cards: List<Card> = emptyList()
): CardGrouping(cards) {

    fun exportCode(idMapper: (cardId: String) -> String = { Decoder.idToCodeMap.getOrDefault(it, "__") }): String {
        return Decoder(DecoderType.COLLECTION, idMapper).createExportCode(this)
    }

    companion object {
        @JvmStatic
        fun importCode(code: String) = Collection(Decoder(DecoderType.COLLECTION).createListFromCode(code))
    }
}
