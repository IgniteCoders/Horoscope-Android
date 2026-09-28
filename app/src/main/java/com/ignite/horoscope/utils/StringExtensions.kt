package com.ignite.horoscope.utils

import java.text.Normalizer

fun String.normalize(): String {
    val normalized = Normalizer.normalize(this, Normalizer.Form.NFD)
    return normalized.replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
}

fun String.search(other: String): Boolean {
    return this.normalize().contains(other.normalize(), true)
}