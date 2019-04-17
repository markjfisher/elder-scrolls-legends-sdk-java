package io.elderscrollslegends

import io.mockk.every
import io.mockk.mockk
import kong.unirest.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.function.Function

class TypeTests {
    private lateinit var client: Client

    @BeforeEach
    fun before() {
        client = mockk()
        UnirestInitializer.setClient(client)
    }

    private val response = mockk<HttpResponse<JsonNode>>()

    @Test
    fun `all() returns all types`() {
        val types = String(this::class.java.getResource("/types.json").readBytes())
        every { client.request(any(), any<Function<RawResponse, HttpResponse<JsonNode>>>()) } returns response
        every { response.isSuccess } returns true
        every { response.body } returns JsonNode(types)

        // when
        val allTypes = Type.all()

        // then
        assertThat(allTypes).isEqualTo(
            listOf(
                "Action",
                "Creature",
                "Item",
                "Support"
            )
        )
    }

}
