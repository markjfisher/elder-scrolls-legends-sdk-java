package io.elderscrollslegends

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kong.unirest.Client
import kong.unirest.ObjectMapper
import kong.unirest.Unirest
import org.apache.http.client.HttpClient
import org.apache.http.client.config.RequestConfig
import org.apache.http.impl.client.cache.CacheConfig
import org.apache.http.impl.client.cache.CachingHttpClients
import java.io.IOException

open class UnirestClient(val uriPath: String = "") {
    init {
        UnirestInitializer.init()
    }

    fun <T> get(resource: String, cls: Class<T>, queryParams: Map<String, String> = emptyMap()): T? {
        val url = if (resource.isNullOrBlank()) uriPath else "$uriPath/$resource"
        val data = Unirest.get(url)
            .queryString(queryParams)
            .asJson()

        return if (data.isSuccess) UnirestInitializer.objectMapper.readValue(data.body.toString(), cls) else null
    }

    fun <T> post(
        resource: String = "",
        cls: Class<T>,
        queryParams: Map<String, String> = emptyMap(),
        fields: Map<String, Any> = emptyMap(),
        headers: Map<String, String> = emptyMap()
    ): T? {
        val url = if (resource.isNullOrBlank()) uriPath else "$uriPath/$resource"
        val data = Unirest.post(url)
            .fields(fields)
            .headers(headers)
            .queryString(queryParams)
            .asJson()

        return if (data.isSuccess) UnirestInitializer.objectMapper.readValue(data.body.toString(), cls) else null
    }

    fun <T> find(resource: String, id: String, cls: Class<T>, queryParams: Map<String, String> = emptyMap()): T? {
        return get("$resource/$id", cls, queryParams)
    }

}

object UnirestInitializer : Config() {
    private val jacksonObjectMapper: com.fasterxml.jackson.databind.ObjectMapper = jacksonObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    lateinit var objectMapper: ObjectMapper
    lateinit var configuredClient: Client

    fun setClient(client: Client) {
        configuredClient = client
        Unirest.config().httpClient(configuredClient)
    }

    fun init() {
        if (!::objectMapper.isInitialized) {
            objectMapper = createObjectMapper()
            Unirest.config().objectMapper = objectMapper
        }

        if (!::configuredClient.isInitialized) {
            Unirest.config().httpClient(createCachingClient())
            configuredClient = Unirest.config().client
        }
    }

    private fun createCachingClient(): HttpClient {
        val cacheConfig = CacheConfig.custom()
            .setMaxCacheEntries(config[maxCacheEntries])
            .setMaxObjectSize(config[maxObjectsSize])
            .setSharedCache(config[sharedCache])
            .build()

        val requestConfig = RequestConfig.custom()
            .setConnectTimeout(config[connectTimeout])
            .setSocketTimeout(config[socketTimeout])
            .build()

        return CachingHttpClients.custom()
            .setCacheConfig(cacheConfig)
            .setDefaultRequestConfig(requestConfig)
            .build()
    }

    private fun createObjectMapper(): ObjectMapper {
        return object : ObjectMapper {
            override fun <T> readValue(value: String, valueType: Class<T>): T {
                try {
                    return jacksonObjectMapper.readValue(value, valueType)
                } catch (e: IOException) {
                    throw RuntimeException(e)
                }

            }

            override fun writeValue(value: Any): String {
                try {
                    return jacksonObjectMapper.writeValueAsString(value)
                } catch (e: JsonProcessingException) {
                    throw RuntimeException(e)
                }

            }
        }
    }

}