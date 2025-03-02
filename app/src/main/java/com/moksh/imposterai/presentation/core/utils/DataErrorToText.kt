package com.moksh.imposterai.presentation.core.utils

import com.moksh.imposterai.R
import com.moksh.imposterai.domain.utils.DataError

fun DataError.asUiText(): UiText {
    return when (this) {
        DataError.Network.ACCOUNT_NOT_VERIFIED -> UiText.StringResource(
            R.string.account_nout_verified
        )

        else -> UiText.StringResource(R.string.error_unknown)
    }
}
