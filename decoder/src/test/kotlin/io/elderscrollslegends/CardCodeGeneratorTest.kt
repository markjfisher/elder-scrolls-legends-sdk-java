package io.elderscrollslegends

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class CardCodeGeneratorTest {
    @Test
    fun `combining letters`() {
        val codes = CardCodeGenerator().generateCardCodeCombinations()
        Assertions.assertThat(codes.size).isEqualTo(2704)
        Assertions.assertThat(codes.first()).isEqualTo("aa")
        Assertions.assertThat(codes.last()).isEqualTo("ZZ")
    }
}