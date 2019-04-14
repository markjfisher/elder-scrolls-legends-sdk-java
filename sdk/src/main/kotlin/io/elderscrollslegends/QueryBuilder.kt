package io.elderscrollslegends

import com.natpryce.konfig.*

class QueryBuilder {
    private val uri = Key("legends.uri", stringType)
    private val version = Key("legends.version", stringType)

    private val config = ConfigurationProperties.systemProperties() overriding
            EnvironmentVariables() overriding
            ConfigurationProperties.fromResource("defaults.properties")

    private val unirestClient = UnirestClient(uriPath = "${config[uri]}/${config[version]}")

    fun <T> find(resource: String, id: String, cls: Class<T>, queryParams: Map<String, String> = emptyMap()): T? {
        return unirestClient.find(resource, id, cls, queryParams)
    }

    fun <S : ResultCounters, T> where(resource: String, cls: Class<S>, predicates: Map<String, String> = emptyMap(), adder: (S?, MutableList<T>) -> Unit): List<T> {
        val adjustedPredicates = predicates.toMutableMap()

        val singlePageOnly = predicates.containsKey("page")

        val page = adjustedPredicates.getOrDefault("page", "1")
        adjustedPredicates["page"] = page

        val items = unirestClient.get(resource = resource, cls = cls, queryParams = adjustedPredicates) ?: return emptyList()

        val results = mutableListOf<T>()
        adder(items, results)

        if (singlePageOnly) return results

        val totalPageCount = items.totalCount / items.pageSize + if (items.totalCount % items.pageSize == 0) 0 else 1
        for (nextPage in (page.toInt() + 1)..totalPageCount) {
            adjustedPredicates["page"] = nextPage.toString()
            val newItems = unirestClient.get(resource = resource, cls = cls, queryParams = adjustedPredicates)
            adder(newItems, results)
        }

        return results
    }
}