package io.elderscrollslegends

import io.mockk.every
import io.mockk.mockk
import kong.unirest.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.function.Function

class SubtypeTests {
    private lateinit var client: Client

    @BeforeEach
    fun before() {
        client = mockk()
        UnirestInitializer.setClient(client)
    }

    private val response = mockk<HttpResponse<JsonNode>>()

    @Test
    fun `all() returns all subtypes`() {
        val types = String(this::class.java.getResource("/subtypes.json").readBytes())
        every { client.request(any(), any<Function<RawResponse, HttpResponse<JsonNode>>>()) } returns response
        every { response.isSuccess } returns true
        every { response.body } returns JsonNode(types)

        // when
        val allSubtypes = Subtype.all()

        // then
        assertThat(allSubtypes).isEqualTo(
            listOf(
                "Animal",
                "Argonian",
                "Ash Creature",
                "Beast",
                "Breton",
                "Centaur",
                "Chaurus",
                "Daedra",
                "Dark Elf",
                "Defense",
                "Dragon",
                "Dreugh",
                "Dwemer",
                "Fabricant",
                "Factotum",
                "Falmer",
                "Fish",
                "Gargoyle",
                "Giant",
                "Goblin",
                "God",
                "Harpy",
                "High Elf",
                "Imp",
                "Imperfect",
                "Imperial",
                "Insect",
                "Khajiit",
                "Kwama",
                "Lurcher",
                "Mammoth",
                "Mantikora",
                "Minotaur",
                "Mudcrab",
                "Mummy",
                "Nereid",
                "Netch",
                "Nord",
                "Ogre",
                "Orc",
                "Pastry",
                "Reachman",
                "Redguard",
                "Reptile",
                "Skeever",
                "Skeleton",
                "Spider",
                "Spirit",
                "Spriggan",
                "Troll",
                "Vampire",
                "Wamasu",
                "Werewolf",
                "Wolf",
                "Wood Elf",
                "Wraith"
            )
        )
    }

}
