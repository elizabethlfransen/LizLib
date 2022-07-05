package io.github.elizabethlfransen.lizlib.util

import org.slf4j.Logger

inline fun <reified T: Throwable> Logger.runLogging(message: String, task: () -> Unit) {
    try {
        task()
    } catch (e: Throwable) {
        if(e is T) {
          error(message, e)
        }
        throw e
    }
}