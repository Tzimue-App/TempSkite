package com.example.skite.data.entities.enums

import androidx.annotation.StringRes
import com.example.skite.R

enum class AppLanguage(val code: String, @StringRes val nameRes: Int, val flagEmoji: String) {
    FRENCH("fr", R.string.lang_fr, "🇫🇷"),
    ENGLISH("en", R.string.lang_en, "🇬🇧"),
    DUTCH("nl", R.string.lang_nl, "🇳🇱");

    companion object {
        fun fromCode(code: String): AppLanguage =
            entries.find { it.code == code } ?: ENGLISH
    }
}