package com.example.main.api;

import okio.Buffer
import java.io.EOFException

/**
 * 描述:
 * <p>
 * author zys
 * create by 2020/09/06
 */
fun Buffer.isProbablyUtf8(): Boolean {
    return try {
        val prefix = Buffer()
        val byteCount = this.size.coerceAtMost(64L)
        this.copyTo(prefix, 0L, byteCount)
        var i = 0
        while (i < 16 && !prefix.exhausted()) {
            val codePoint = prefix.readUtf8CodePoint()
            if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                return false
            }
            ++i
        }
        true
    } catch (var7: EOFException) {
        false
    }
}

