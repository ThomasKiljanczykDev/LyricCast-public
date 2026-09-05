package dev.thomas_kiljanczyk.lyriccast.application.testing

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import dev.thomas_kiljanczyk.lyriccast.BuildConfig
import dev.thomas_kiljanczyk.lyriccast.common.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking

@EntryPoint
@InstallIn(SingletonComponent::class)
internal interface SeedingEntryPoint {
    fun appSeeder(): AppSeeder

    @ApplicationScope
    fun applicationScope(): CoroutineScope
}

internal class SeedingContentProvider : ContentProvider() {
    override fun onCreate(): Boolean = true

    override fun call(method: String, arg: String?, extras: Bundle?): Bundle? {
        if (!BuildConfig.ENABLE_SEEDING) return null
        val appContext = context?.applicationContext ?: return null
        val entryPoint =
            EntryPointAccessors.fromApplication(appContext, SeedingEntryPoint::class.java)
        return runBlocking(entryPoint.applicationScope().coroutineContext) {
            entryPoint.appSeeder().seed()
            Bundle()
        }
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? = null

    override fun getType(uri: Uri): String? = null

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int = 0

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int = 0
}
