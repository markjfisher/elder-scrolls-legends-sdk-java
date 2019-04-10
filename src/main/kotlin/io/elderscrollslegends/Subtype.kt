package io.elderscrollslegends

import com.fasterxml.jackson.annotation.JsonProperty

class Subtype {
    companion object {
        private val queryBuilder = QueryBuilder()
        private const val RESOURCE_NAME = "types"

        fun all(): List<String> {
            return where(emptyMap())
        }

        private fun where(predicates: Map<String, String>): List<String> {
            return queryBuilder.where(resource = RESOURCE_NAME, cls = Subtypes::class.java, predicates = predicates) { types, typeList ->
                typeList.addAll(types?.subtypes ?: emptyList())
            }
        }
    }
}

data class Subtypes(
    val subtypes: List<String>,

    @JsonProperty("_pageSize")
    override val pageSize: Int,
    @JsonProperty("_totalCount")
    override val totalCount: Int
) : ResultCounters(pageSize, totalCount)