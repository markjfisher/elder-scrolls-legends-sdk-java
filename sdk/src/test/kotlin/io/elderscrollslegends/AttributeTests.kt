package io.elderscrollslegends

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kong.unirest.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.function.Function

class AttributeTests {
    private val client = mockk<Client>()
    private val response = mockk<HttpResponse<JsonNode>>()

    init {
        UnirestClient(client)
    }

    @Test
    fun `all() returns all attributes`() {
        val attributes = String(this::class.java.getResource("/attributes.json").readBytes())
        every { client.request(any(), any<Function<RawResponse, HttpResponse<JsonNode>>>()) } returns response
        every { response.isSuccess } returns true
        every { response.body } returns JsonNode(attributes)

        // when
        val allAttributes = Attribute.all()

        // then
        assertThat(allAttributes).isEqualTo(
            listOf(
                "Agility",
                "Endurance",
                "Intelligence",
                "Neutral",
                "Strength",
                "Willpower"
            )
        )
    }

    @Test
    fun `all invokes correct api calls over multiple pages when no page is specified and assembles them into single list`() {
        val attr1 = String(this::class.java.getResource("/attributes-page1.json").readBytes())
        val attr2 = String(this::class.java.getResource("/attributes-page2.json").readBytes())
        val attr3 = String(this::class.java.getResource("/attributes-page3.json").readBytes())

        val capturedHttpRequest = slot<HttpRequest<*>>()
        val requests = mutableListOf<HttpRequest<*>>()
        every {
            client.request(
                capture(capturedHttpRequest),
                any<Function<RawResponse, HttpResponse<JsonNode>>>()
            )
        } answers {
            requests.add(capturedHttpRequest.captured)
            response
        }
        every { response.isSuccess } returns true
        every { response.body } returnsMany listOf(JsonNode(attr1), JsonNode(attr2), JsonNode(attr3))

        // when
        val allAttributes = Attribute.all()

        assertThat(requests.map { it.url }).isEqualTo(
            listOf(
                "https://api.elderscrollslegends.io/v1/attributes?page=1",
                "https://api.elderscrollslegends.io/v1/attributes?page=2",
                "https://api.elderscrollslegends.io/v1/attributes?page=3"
            )
        )

        assertThat(allAttributes).isEqualTo(
            listOf(
                "Agility",
                "Endurance",
                "Intelligence",
                "Neutral",
                "Strength",
                "Willpower"
            )
        )

    }

}
