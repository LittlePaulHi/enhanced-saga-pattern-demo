package tw.paulchang.billingservice.redis.model

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class InstantJsonSerializer : JsonSerializer<Instant?>() {
    private val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault())

    override fun serialize(value: Instant?, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeString(fmt.format(value))
    }
}
