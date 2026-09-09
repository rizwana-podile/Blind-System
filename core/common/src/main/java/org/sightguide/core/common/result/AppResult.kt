package org.sightguide.core.common.result

/**
 * Robust result wrapper for asynchronous operations, hardware queries, and external APIs.
 * Guarantees that failure states always include a localized, human-friendly spoken message
 * suited for text-to-speech announcement.
 */
sealed interface AppResult<out T> {

    data class Success<out T>(val data: T) : AppResult<T>

    data class Error(
        val throwable: Throwable? = null,
        val userFriendlyMessage: String,
        val code: ErrorCode = ErrorCode.UNKNOWN
    ) : AppResult<Nothing>

    data object Loading : AppResult<Nothing>

    val isSuccess: Boolean
        get() = this is Success

    val isError: Boolean
        get() = this is Error

    val isLoading: Boolean
        get() = this is Loading

    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }

    fun getOrDefault(default: @UnsafeVariance T): T = when (this) {
        is Success -> data
        else -> default
    }

    inline fun onSuccess(action: (T) -> Unit): AppResult<T> {
        if (this is Success) action(data)
        return this
    }

    inline fun onError(action: (Error) -> Unit): AppResult<T> {
        if (this is Error) action(this)
        return this
    }

    inline fun <R> map(transform: (T) -> R): AppResult<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> this
        is Loading -> Loading
    }
}

enum class ErrorCode {
    PERMISSION_DENIED,
    HARDWARE_UNAVAILABLE,
    NETWORK_UNAVAILABLE,
    TIMEOUT,
    SENSOR_ACCURACY_LOW,
    AUDIO_ENGINE_BUSY,
    CAMERA_INITIALIZATION_FAILED,
    UNKNOWN
}
