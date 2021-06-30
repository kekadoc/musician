package com.kekadoc.project.musicant.core.serialization

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

inline fun <reified T> T.serializeToStringOrNull(): String? {
    return try {
        Json.encodeToString(this)
    } catch (e: Exception) {
        null
    }
}
inline fun <reified T> String.deserializeFromStringOrNull(): T? {
    return try {
        Json.decodeFromString(this)
    } catch (e: Exception) {
        null
    }
}

inline fun <reified T> T.serializeToString(): String {
    return Json.encodeToString(this)
}
inline fun <reified T> String.deserializeFromString(): T {
    return Json.decodeFromString(this)
}