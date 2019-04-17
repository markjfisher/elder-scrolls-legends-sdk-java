package io.elderscrollslegends

import com.natpryce.konfig.*

open class Config {
    val legendsAPIUri = Key("legends.uri", stringType)
    val legendsAPIVersion = Key("legends.version", stringType)

    val maxCacheEntries = Key("httpclient.cache.max-cache-entries", intType)
    val maxObjectsSize = Key("httpclient.cache.max-objects-size", longType)
    val sharedCache = Key("httpclient.cache.shared-cache", booleanType)
    val connectTimeout = Key("httpclient.request.connect-timeout", intType)
    val socketTimeout = Key("httpclient.request.socket-timeout", intType)

    val config = ConfigurationProperties.systemProperties() overriding
            EnvironmentVariables() overriding
            ConfigurationProperties.fromResource("defaults.properties")

}