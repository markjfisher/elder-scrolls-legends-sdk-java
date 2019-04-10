package io.elderscrollslegends

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.natpryce.konfig.*
import com.natpryce.konfig.ConfigurationProperties.Companion.systemProperties
import kong.unirest.Client
import kong.unirest.ObjectMapper
import kong.unirest.Unirest
import java.io.IOException

open class UnirestClient(client: Client = Unirest.config().client) {
    private val uri = Key("legends.uri", stringType)
    private val version = Key("legends.version", stringType)

    private val config = systemProperties() overriding
            EnvironmentVariables() overriding
            ConfigurationProperties.fromResource("defaults.properties")

    init {
        Unirest.config().httpClient(client)
        UnirestInitializer.init()
    }

    fun <T> get(resource: String, cls: Class<T>, queryParams: Map<String, String> = emptyMap()): T? {
        val url = "${config[uri]}/${config[version]}/$resource"
        val data = Unirest.get(url)
            .queryString(queryParams)
            .asJson()

        return if (data.isSuccess) UnirestInitializer.objectMapper.readValue(data.body.toString(), cls) else null
    }

    fun <T> find(resource: String, id: String, cls: Class<T>, queryParams: Map<String, String> = emptyMap()): T? {
        return get("$resource/$id", cls, queryParams)
    }

}

object UnirestInitializer {
    private val jacksonObjectMapper: com.fasterxml.jackson.databind.ObjectMapper = jacksonObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    lateinit var objectMapper: ObjectMapper

    fun init() {
        if (!::objectMapper.isInitialized) {
            objectMapper = createObjectMapper()
            Unirest.config().objectMapper = objectMapper
        }
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