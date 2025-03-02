package com.moksh.imposterai.domain.utils

sealed interface DataError : Error {
    enum class Network : DataError {
        REQUEST_TIMEOUT,
        UNAUTHORIZED,
        CONFLICT,
        TOO_MANY_REQUESTS,
        NO_INTERNET,
        PAYLOAD_TOO_LARGE,
        SERVER_ERROR,
        SERIALIZATION,
        UNKNOWN,
        ACCOUNT_NOT_VERIFIED,
        EMPTY_RESPONSE
    }

    enum class Local : DataError {
        DISK_FULL,
        UNKNOWN,
        DUPLICATE_DATA,
        DATABASE_FULL,
        SQL_ERROR
    }
}