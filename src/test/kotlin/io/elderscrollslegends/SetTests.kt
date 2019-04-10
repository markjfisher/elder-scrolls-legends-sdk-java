package io.elderscrollslegends

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kong.unirest.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.function.Function

class SetTests {
    private val client = mockk<Client>()
    private val response = mockk<HttpResponse<JsonNode>>()
    init {
        UnirestClient(client)
    }

    @Test
    fun `find Set by id`() {
        // Given
        val setId = "fsc"
        val set = String(this::class.java.getResource("/set-fsc.json").readBytes())
        every { client.request(any(), any<Function<RawResponse, HttpResponse<JsonNode>>>()) } returns response
        every { response.isSuccess } returns true
        every { response.body } returns JsonNode(set)

        // when
        val result = Set.find(setId)

        // then
        assertThat(result).isNotNull
        assertThat(result!!).satisfies {
            assertThat(it.id).isEqualTo("fsc")
            assertThat(it.name).isEqualTo("FrostSpark Collection")
            assertThat(it.releaseDate).isEqualTo("2018-10-19")
            assertThat(it.totalCards).isEqualTo(11)
        }

    }

    @Test
    fun `all() returns all sets`() {
        val sets = String(this::class.java.getResource("/sets.json").readBytes())
        every { client.request(any(), any<Function<RawResponse, HttpResponse<JsonNode>>>()) } returns response
        every { response.isSuccess } returns true
        every { response.body } returnsMany listOf(JsonNode(sets), JsonNode(sets), JsonNode(sets)) // should only need 1

        // when
        val allSets = Set.all()

        // then
        assertThat(allSets.size).isEqualTo(9)

        assertThat(allSets.first()).satisfies {
            assertThat(it.id).isEqualTo("cs")
            assertThat(it.name).isEqualTo("Core Set")
            assertThat(it.releaseDate).isEqualTo("2016-04-01")
            assertThat(it.totalCards).isEqualTo(454)
        }

        assertThat(allSets.map{it.id}).isEqualTo(
            listOf("cs", "mc", "mr", "fodb", "hos", "rcc", "fhc", "hom", "fsc")
        )

    }

    @Test
    fun `where clause invokes correct calls over multiple pages when no page is specified`() {
        val sets1 = String(this::class.java.getResource("/sets-page1.json").readBytes())
        val sets2 = String(this::class.java.getResource("/sets-page2.json").readBytes())
        val sets3 = String(this::class.java.getResource("/sets-page3.json").readBytes())

        val capturedHttpRequest = slot<HttpRequest<*>>()
        val requests = mutableListOf<HttpRequest<*>>()
        every { client.request(capture(capturedHttpRequest), any<Function<RawResponse, HttpResponse<JsonNode>>>()) } answers {
            requests.add(capturedHttpRequest.captured)
            response
        }
        every { response.isSuccess } returns true
        every { response.body } returnsMany listOf(JsonNode(sets1), JsonNode(sets2), JsonNode(sets3))

        // when
        Set.where(mapOf("totalCards" to "11,22"))

        assertThat(requests.map { it.url }).isEqualTo(
            listOf(
                "https://api.elderscrollslegends.io/v1/sets?totalCards=11%2C22&page=1",
                "https://api.elderscrollslegends.io/v1/sets?totalCards=11%2C22&page=2",
                "https://api.elderscrollslegends.io/v1/sets?totalCards=11%2C22&page=3"
            )
        )
    }

    @Test
    fun `where clause invokes correct call to single page when page is specified`() {
        val sets = String(this::class.java.getResource("/sets-page2.json").readBytes())

        val capturedHttpRequest = slot<HttpRequest<*>>()
        val requests = mutableListOf<HttpRequest<*>>()
        every { client.request(capture(capturedHttpRequest), any<Function<RawResponse, HttpResponse<JsonNode>>>()) } answers {
            requests.add(capturedHttpRequest.captured)
            response
        }
        every { response.isSuccess } returns true
        every { response.body } returns JsonNode(sets)

        // when
        Set.where(mapOf("page" to "2"))

        assertThat(requests.map { it.url }).isEqualTo(
            listOf(
                "https://api.elderscrollslegends.io/v1/sets?page=2"
            )
        )
    }
}
