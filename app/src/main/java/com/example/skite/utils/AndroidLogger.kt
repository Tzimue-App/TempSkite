package com.example.skite.utils

import android.util.Log

class AndroidLogger : Logger {

    companion object {
        @Volatile
        private var instance: AndroidLogger? = null

        fun getInstance(): AndroidLogger {
            return instance ?: synchronized(this) {
                instance ?: AndroidLogger().also { instance = it }
            }
        }
    }

    override fun debug(tag: String, message: String) {
        Log.d(tag, message)
    }

    override fun info(tag: String, message: String) {
        Log.i(tag, message)
    }

    override fun warn(tag: String, message: String) {
        Log.w(tag, message)
    }

    override fun error(tag: String, message: String) {
        Log.e(tag, message)
    }

    override fun error(tag: String, message: String, throwable: Throwable) {
        Log.e(tag, message, throwable)
    }
}
