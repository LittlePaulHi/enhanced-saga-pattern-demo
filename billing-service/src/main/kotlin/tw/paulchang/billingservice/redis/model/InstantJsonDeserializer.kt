package tw.paulchang.billingservice.redis.model

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class InstantJsonDeserializer : JsonDeserializer<Instant>() {
    private val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault())

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Instant {
        return Instant.from(fmt.parse(p.text))
    }
}
