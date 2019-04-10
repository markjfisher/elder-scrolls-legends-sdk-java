package io.elderscrollslegends

import com.fasterxml.jackson.annotation.JsonProperty

open class ResultCounters (
    @JsonProperty("_pageSize")
    open val pageSize: Int,
    @JsonProperty("_totalCount")
    open val totalCount: Int
)