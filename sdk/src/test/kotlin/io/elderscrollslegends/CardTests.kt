package io.elderscrollslegends

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kong.unirest.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.function.Function

class CardTests {
    private val client = mockk<Client>()
    private val response = mockk<HttpResponse<JsonNode>>()
    init {
        UnirestClient(client = client)
    }

    @Test
    fun `find Card by id`() {
        // Given
        val cardId = "06a4d55a15a48361f1d5d7c2fe50563f1fa82408"
        val card = String(this::class.java.getResource("/card-06a4d55a15a48361f1d5d7c2fe50563f1fa82408.json").readBytes())
        every { client.request(any(), any<Function<RawResponse, HttpResponse<JsonNode>>>()) } returns response
        every { response.isSuccess } returns true
        every { response.body } returns JsonNode(card)

        // when
        val result = Card.find(cardId)

        // then
        assertThat(result).isNotNull
        assertThat(result!!).satisfies {
            assertThat(it.name).isEqualTo("Allena Benoch")
            assertThat(it.rarity).isEqualTo("Legendary")
            assertThat(it.type).isEqualTo("Creature")
            assertThat(it.subtypes).isEqualTo(listOf("Wood Elf"))
            assertThat(it.cost).isEqualTo(6)
            assertThat(it.power).isEqualTo(1)
            assertThat(it.health).isEqualTo(1)
            assertThat(it.set.id).isEqualTo("cs")
            assertThat(it.set.name).isEqualTo("Core Set")
            assertThat(it.set.self).isEqualTo("https://api.elderscrollslegends.io/v1/sets/cs")
            assertThat(it.collectible).isEqualTo(true)
            assertThat(it.soulSummon).isEqualTo("1200")
            assertThat(it.soulTrap).isEqualTo("400")
            assertThat(it.text).isEqualTo("Lethal. Summon: Deal 1 damage.")
            assertThat(it.attributes).isEqualTo(listOf("Strength", "Agility"))
            assertThat(it.keywords).isEqualTo(listOf("Lethal"))
            assertThat(it.unique).isEqualTo(true)
            assertThat(it.imageUrl).isEqualTo("https://images.elderscrollslegends.io/cs/allena_benoch.png")
            assertThat(it.id).isEqualTo("06a4d55a15a48361f1d5d7c2fe50563f1fa82408")
        }

    }

    @Test
    fun `all() returns all cards`() {
        val cards1 = String(this::class.java.getResource("/cards-page1.json").readBytes())
        val cards2 = String(this::class.java.getResource("/cards-page2.json").readBytes())
        val cards3 = String(this::class.java.getResource("/cards-page3.json").readBytes())
        every { client.request(any(), any<Function<RawResponse, HttpResponse<JsonNode>>>()) } returns response
        every { response.isSuccess } returns true
        every { response.body } returnsMany listOf(JsonNode(cards1), JsonNode(cards2), JsonNode(cards3))

        // when
        // val x = unirestClient.get("cards", Cards::class.java)
        val allCards = Card.all()

        // then
        assertThat(allCards.size).isEqualTo(6)

        assertThat(allCards.first()).satisfies {
            assertThat(it.name).isEqualTo("Blood Dragon")
            assertThat(it.rarity).isEqualTo("Legendary")
            assertThat(it.type).isEqualTo("Creature")
            assertThat(it.subtypes).isEqualTo(listOf("Dragon"))
            assertThat(it.cost).isEqualTo(5)
            assertThat(it.power).isEqualTo(5)
            assertThat(it.health).isEqualTo(7)
            assertThat(it.set.id).isEqualTo("cs")
            assertThat(it.set.name).isEqualTo("Core Set")
            assertThat(it.set.self).isEqualTo("https://api.elderscrollslegends.io/v1/sets/cs")
            assertThat(it.collectible).isEqualTo(true)
            assertThat(it.soulSummon).isEqualTo("1200")
            assertThat(it.soulTrap).isEqualTo("400")
            assertThat(it.text).isEqualTo("Blood Dragon ignores Guards and can attack creatures in any lane.")
            assertThat(it.attributes).isEqualTo(listOf("Strength"))
            assertThat(it.keywords).isEqualTo(listOf("Guard"))
            assertThat(it.unique).isEqualTo(false)
            assertThat(it.imageUrl).isEqualTo("https://images.elderscrollslegends.io/cs/blood_dragon.png")
            assertThat(it.id).isEqualTo("7f62d718099821fc9945af326ef29f406f039f71")
        }

        assertThat(allCards[1].name).isEqualTo("Burn and Pillage")
        assertThat(allCards[2].name).isEqualTo("Camoran Scout Leader")
        assertThat(allCards[3].name).isEqualTo("Cliff Racer")
        assertThat(allCards[4].name).isEqualTo("Wabbajack")
        assertThat(allCards[5].name).isEqualTo("Allena Benoch")

    }

    @Test
    fun `where clause invokes correct calls over multiple pages when no page is specified`() {
        val cards1 = String(this::class.java.getResource("/cards-where-page1.json").readBytes())
        val cards2 = String(this::class.java.getResource("/cards-where-page2.json").readBytes())
        val cards3 = String(this::class.java.getResource("/cards-where-page3.json").readBytes())

        val capturedHttpRequest = slot<HttpRequest<*>>()
        val requests = mutableListOf<HttpRequest<*>>()
        every { client.request(capture(capturedHttpRequest), any<Function<RawResponse, HttpResponse<JsonNode>>>()) } answers {
            requests.add(capturedHttpRequest.captured)
            response
        }
        every { response.isSuccess } returns true
        every { response.body } returnsMany listOf(JsonNode(cards1), JsonNode(cards2), JsonNode(cards3))

        // when
        Card.where(mapOf("keywords" to "guard|prophecy"))

        assertThat(requests.map { it.url }).isEqualTo(
            listOf(
                "https://api.elderscrollslegends.io/v1/cards?keywords=guard%7Cprophecy&page=1",
                "https://api.elderscrollslegends.io/v1/cards?keywords=guard%7Cprophecy&page=2",
                "https://api.elderscrollslegends.io/v1/cards?keywords=guard%7Cprophecy&page=3"
            )
        )
    }

    @Test
    fun `where clause invokes correct call to single page when page is specified`() {
        val cards = String(this::class.java.getResource("/cards-page2.json").readBytes())

        val capturedHttpRequest = slot<HttpRequest<*>>()
        val requests = mutableListOf<HttpRequest<*>>()
        every { client.request(capture(capturedHttpRequest), any<Function<RawResponse, HttpResponse<JsonNode>>>()) } answers {
            requests.add(capturedHttpRequest.captured)
            response
        }
        every { response.isSuccess } returns true
        every { response.body } returns JsonNode(cards)

        // when
        Card.where(mapOf("keywords" to "guard|prophecy", "page" to "2"))

        assertThat(requests.map { it.url }).isEqualTo(
            listOf(
                "https://api.elderscrollslegends.io/v1/cards?keywords=guard%7Cprophecy&page=2"
            )
        )
    }

    @Test
    fun `can create card with missing fields`() {
        val cardData = String(this::class.java.getResource("/card-missing-fields.json").readBytes())

        val capturedHttpRequest = slot<HttpRequest<*>>()
        val requests = mutableListOf<HttpRequest<*>>()
        every { client.request(capture(capturedHttpRequest), any<Function<RawResponse, HttpResponse<JsonNode>>>()) } answers {
            requests.add(capturedHttpRequest.captured)
            response
        }
        every { response.isSuccess } returns true
        every { response.body } returns JsonNode(cardData)

        // when
        val card = Card.find("fake-id")!!

        assertThat(requests.map { it.url }).isEqualTo(
            listOf(
                "https://api.elderscrollslegends.io/v1/cards/fake-id"
            )
        )

        assertThat(card).satisfies {
            assertThat(it.id).isEqualTo("fake-id")
            assertThat(it.name).isEqualTo("Fake Card")
        }
    }

    @Test
    fun `can create card with non integer soul values`() {
        val cardData = String(this::class.java.getResource("/card-non-int-summon-costs.json").readBytes())

        val capturedHttpRequest = slot<HttpRequest<*>>()
        val requests = mutableListOf<HttpRequest<*>>()
        every { client.request(capture(capturedHttpRequest), any<Function<RawResponse, HttpResponse<JsonNode>>>()) } answers {
            requests.add(capturedHttpRequest.captured)
            response
        }
        every { response.isSuccess } returns true
        every { response.body } returns JsonNode(cardData)

        // when
        val card = Card.find("test-id")!!

        assertThat(card).satisfies {
            assertThat(it.id).isEqualTo("test-id")
            assertThat(it.name).isEqualTo("test card")
        }
    }
}
