package io.elderscrollslegends

import org.json.JSONObject
import java.io.File

class CodeMapper {
    companion object {
        @JvmStatic
        fun main(args : Array<String>) {
            val cards = Card.all()
            val decoder = Decoder()
            val m = CardCodeGenerator().generateCardCodeCombinations()
                .map { code ->
                    print(code)
                    val name = decoder.getNameFromCode(code)
                    if (code.endsWith("Z")) println("")
                    val card = cards.filter { it.name == name }
                    val id = if (card.isNotEmpty()) card.first().id else "UNKNOWN"
                    val jo = JSONObject()
                        .put("name", name)
                        .put("code", code)
                        .put("id", id)
                    jo
                }
                .filter { jo ->
                    jo.get("name") != ""
                }
            println("")

            File("map.json").writeText(m.toString())
        }
    }
}