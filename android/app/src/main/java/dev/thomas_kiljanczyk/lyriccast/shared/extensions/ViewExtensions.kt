package dev.thomas_kiljanczyk.lyriccast.shared.extensions

import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat

fun View.hideKeyboard() {
    val inputMethodManager: InputMethodManager? =
        ContextCompat.getSystemService(this.context, InputMethodManager::class.java)
    inputMethodManager?.hideSoftInputFromWindow(
        this.applicationWindowToken,
        InputMethodManager.HIDE_NOT_ALWAYS
    )
}
