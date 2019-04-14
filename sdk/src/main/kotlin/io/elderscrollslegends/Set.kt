package io.elderscrollslegends

import com.fasterxml.jackson.annotation.JsonProperty

data class Set (
    val id: String,
    val name: String = "",
    val releaseDate: String = "",
    val totalCards: Int = 0
) {
    companion object {
        private val queryBuilder = QueryBuilder()
        private const val RESOURCE_NAME = "sets"

        @JvmStatic
        fun all(): List<Set> {
            return where(emptyMap())
        }

        @JvmStatic
        fun find(id: String): Set? {
            return queryBuilder.find(resource = RESOURCE_NAME, id = id, cls = SetSingle::class.java)?.set
        }

        @JvmStatic
        fun where(predicates: Map<String, String>): List<Set> {
            return queryBuilder.where(resource = RESOURCE_NAME, cls = Sets::class.java, predicates = predicates) { sets, setList ->
                setList.addAll(sets?.sets ?: emptyList())
            }
        }
    }
}

data class Sets (
    val sets: List<Set>,

    @JsonProperty("_pageSize")
    override val pageSize: Int,
    @JsonProperty("_totalCount")
    override val totalCount: Int
) : ResultCounters(pageSize, totalCount)

data class SetSingle(
    val set: Set
)
