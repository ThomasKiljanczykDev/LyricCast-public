/*
 * Created by Tomasz Kiljanczyk on 9/8/25, 12:15 AM
 * Copyright (c) 2025 . All rights reserved.
 * Last modified 9/7/25, 11:08 PM
 */

package dev.thomas_kiljanczyk.lyriccast.domain.models

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

sealed class UiText {
    data class DynamicString(val value: String) : UiText()

    class StringResource(@param:StringRes @field:StringRes val resId: Int, vararg val args: Any) :
        UiText()

    fun toString(context: Context): String {
        return when (this) {
            is DynamicString -> value
            is StringResource -> context.getString(resId, *args)
        }
    }

    @Composable
    fun asString(): String {
        return when (this) {
            is DynamicString -> value
            is StringResource -> {
                stringResource(resId, *args)
            }
        }
    }
}