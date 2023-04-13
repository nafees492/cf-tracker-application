package com.gourav.competrace.app_core.util

import com.gourav.competrace.R
import retrofit2.HttpException
import java.io.IOException
import java.net.HttpURLConnection

enum class ErrorEntity(val messageId: Int) {
    Network(R.string.no_internet__connection),
    NotFound(R.string.not_found_error),
    AccessDenied(R.string.access_denied_error),
    ServiceUnavailable(R.string.service_unavailable_error),
    BadRequest(R.string.user_not_found),
    Unknown(R.string.something_went_wrong);

    companion object {
        fun getError(throwable: Throwable): ErrorEntity {
            return when(throwable) {
                is IOException -> Network
                is HttpException -> {
                    when(throwable.code()) {
                        HttpURLConnection.HTTP_NOT_FOUND -> NotFound
                        HttpURLConnection.HTTP_FORBIDDEN -> AccessDenied
                        HttpURLConnection.HTTP_UNAVAILABLE -> ServiceUnavailable
                        HttpURLConnection.HTTP_BAD_REQUEST -> BadRequest
                        else -> Unknown
                    }
                }
                else -> Unknown
            }
        }
    }
}