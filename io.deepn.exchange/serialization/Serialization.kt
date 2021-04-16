package io.deepn.exchange.serialization

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type
import java.time.Instant

class UnixInstantDeserializer : JsonDeserializer<Instant?> {

    override fun deserialize(json: JsonElement?, type: Type?, context: JsonDeserializationContext?): Instant? {
        return json?.asJsonPrimitive?.asLong?.let { Instant.ofEpochMilli(it) }
    }

}