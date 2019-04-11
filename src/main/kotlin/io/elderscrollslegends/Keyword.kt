package io.elderscrollslegends

import com.fasterxml.jackson.annotation.JsonProperty

class Keyword {
    companion object {
        private val queryBuilder = QueryBuilder()
        private const val RESOURCE_NAME = "keywords"

        @JvmStatic
        fun all(): List<String> {
            return where(emptyMap())
        }

        private fun where(predicates: Map<String, String>): List<String> {
            return queryBuilder.where(resource = RESOURCE_NAME, cls = Keywords::class.java, predicates = predicates) { keywords, keywordList ->
                keywordList.addAll(keywords?.keywords ?: emptyList())
            }
        }
    }

}

data class Keywords(
    val keywords: List<String>,

    @JsonProperty("_pageSize")
    override val pageSize: Int,
    @JsonProperty("_totalCount")
    override val totalCount: Int
) : ResultCounters(pageSize, totalCount)