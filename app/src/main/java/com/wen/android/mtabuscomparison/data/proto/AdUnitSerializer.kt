package com.wen.android.mtabuscomparison.data.proto

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException
import com.wen.android.mtabuscomparison.data.remote.ad.AdUnitProtos
import java.io.InputStream
import java.io.OutputStream

object AdUnitSerializer : Serializer<AdUnitProtos.AdUnits> {
    override val defaultValue: AdUnitProtos.AdUnits
        get() = AdUnitProtos.AdUnits.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): AdUnitProtos.AdUnits {
        try {
            return AdUnitProtos.AdUnits.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto. $exception")
        }
    }

    override suspend fun writeTo(t: AdUnitProtos.AdUnits, output: OutputStream) {
        t.writeTo(output)
    }

    val Context.adUnitDataStore: DataStore<AdUnitProtos.AdUnits> by dataStore(
        fileName = "adUnit.pb",
        serializer = AdUnitSerializer
    )
}