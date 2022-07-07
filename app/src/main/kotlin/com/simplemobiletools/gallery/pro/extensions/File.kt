package com.simplemobiletools.gallery.pro.extensions

import java.io.File
import java.security.MessageDigest

fun File.md5(): String {
    val md = MessageDigest.getInstance("MD5")
    return this.inputStream().use { fis ->
        val buffer = ByteArray(8192)
        generateSequence {
            when (val bytesRead = fis.read(buffer)) {
                -1 -> null
                else -> bytesRead
            }
        }.forEach { bytesRead -> md.update(buffer, 0, bytesRead) }
        md.digest().joinToString("") { "%02x".format(it) }
    }
}


