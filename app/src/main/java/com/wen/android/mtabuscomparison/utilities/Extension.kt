package com.wen.android.mtabuscomparison.utilities

import android.database.Cursor
fun Cursor?.getStringOrEmpty(columnName: String): String {
    val columnIndex = this?.getColumnIndex(columnName) ?: -1
    if (columnIndex > -1)
        return this?.getString(columnIndex) ?: ""
    return ""
}
fun Cursor?.getLongOrEmpty(columnName: String): Long {
    val columnIndex = this?.getColumnIndex(columnName) ?: -1
    if (columnIndex > -1) {
        return this?.getLong(columnIndex) ?: 0
    }
    return 0
}