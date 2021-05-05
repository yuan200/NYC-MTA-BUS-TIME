package com.wen.android.mtabuscomparison.testhelper.extensions

import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.mockwebserver.MockResponse

fun String.toComparableJson() = try {
    Gson().fromJson(this, JsonObject::class.java).toString()
} catch (e: Exception) {
    throw IllegalArgumentException("'$this' wasn't valid JSON")
}

fun String.toJsonResponse(): MockResponse = MockResponse().setBody(this.toComparableJson())

inline fun <reified T>String.toObject(): T = Gson().fromJson(this, T::class.java)