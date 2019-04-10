package io.elderscrollslegends

import com.fasterxml.jackson.annotation.JsonProperty

class Attribute {
    companion object {
        private val queryBuilder = QueryBuilder()
        private const val RESOURCE_NAME = "attributes"

        fun all(): List<String> {
            return where(emptyMap())
        }

        private fun where(predicates: Map<String, String>): List<String> {
            return queryBuilder.where(resource = RESOURCE_NAME, cls = Attributes::class.java, predicates = predicates) { attributes, attributeList ->
                attributeList.addAll(attributes?.attributes ?: emptyList())
            }
        }
    }

}

data class Attributes(
    val attributes: List<String>,

    @JsonProperty("_pageSize")
    override val pageSize: Int,
    @JsonProperty("_totalCount")
    override val totalCount: Int
) : ResultCounters(pageSize, totalCount)