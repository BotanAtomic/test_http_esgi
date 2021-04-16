package io.deepn.exchange.security


import org.apache.commons.codec.binary.Hex
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec


fun signHmacSHA256(message: String, secret: String): String {
    return runCatching {
        val sha256HMAC = Mac.getInstance("HmacSHA256")
        val secretKeySpec = SecretKeySpec(secret.toByteArray(), "HmacSHA256")
        sha256HMAC.init(secretKeySpec)
        String(Hex.encodeHex(sha256HMAC.doFinal(message.toByteArray())))
    }.getOrElse { "" }
}