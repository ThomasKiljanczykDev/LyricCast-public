package dev.thomas_kiljanczyk.lyriccast.shared.extensions

import android.content.Context
import android.content.ContextWrapper
import androidx.fragment.app.FragmentActivity

fun Context.findParentFragmentActivity(): FragmentActivity? = this as? FragmentActivity
    ?: (this as? ContextWrapper)?.baseContext?.findParentFragmentActivity()
