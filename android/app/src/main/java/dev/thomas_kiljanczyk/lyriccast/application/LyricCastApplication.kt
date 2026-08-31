package dev.thomas_kiljanczyk.lyriccast.application

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.pm.ApplicationInfo
import android.os.Build
import android.os.StrictMode
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import com.google.android.gms.cast.framework.CastContext
import com.google.android.material.color.DynamicColors
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent
import dev.thomas_kiljanczyk.lyriccast.common.di.ApplicationScope
import dev.thomas_kiljanczyk.lyriccast.common.di.Dispatcher
import dev.thomas_kiljanczyk.lyriccast.common.di.LyricCastDispatchers
import dev.thomas_kiljanczyk.lyriccast.core.cast.CastSessionListener
import dev.thomas_kiljanczyk.lyriccast.core.cast.MessageTransport
import dev.thomas_kiljanczyk.lyriccast.core.model.settings.ControlButtonHeightOption
import dev.thomas_kiljanczyk.lyriccast.data.LocaleManager
import dev.thomas_kiljanczyk.lyriccast.datastore.proto.AppSettings
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@EntryPoint
@InstallIn(SingletonComponent::class)
interface ApplicationScopeEntryPoint {
    @ApplicationScope
    fun applicationScope(): CoroutineScope

    @Dispatcher(LyricCastDispatchers.IO)
    fun ioDispatcher(): CoroutineDispatcher

    @Dispatcher(LyricCastDispatchers.Main)
    fun mainDispatcher(): CoroutineDispatcher
}

@HiltAndroidApp
class LyricCastApplication : Application() {

    private val coroutineEntryPoint: ApplicationScopeEntryPoint by lazy {
        EntryPointAccessors.fromApplication(this, ApplicationScopeEntryPoint::class.java)
    }

    private val applicationScope: CoroutineScope by lazy { coroutineEntryPoint.applicationScope() }
    private val ioDispatcher: CoroutineDispatcher by lazy { coroutineEntryPoint.ioDispatcher() }
    private val mainDispatcher: CoroutineDispatcher by lazy { coroutineEntryPoint.mainDispatcher() }

    companion object {
        val PERMISSIONS = preparePermissionArray()

        private fun preparePermissionArray(): Array<String> {
            val result = mutableListOf(
                Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CHANGE_WIFI_STATE
            )

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
                result.add(Manifest.permission.BLUETOOTH)
                result.add(Manifest.permission.BLUETOOTH_ADMIN)
            }

            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                result.add(Manifest.permission.ACCESS_COARSE_LOCATION)
                result.add(Manifest.permission.ACCESS_FINE_LOCATION)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                result.add(Manifest.permission.BLUETOOTH_ADVERTISE)
                result.add(Manifest.permission.BLUETOOTH_CONNECT)
                result.add(Manifest.permission.BLUETOOTH_SCAN)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                result.add(Manifest.permission.NEARBY_WIFI_DEVICES)
            }

            return result.toTypedArray()
        }
    }

    @Inject
    lateinit var dataStore: DataStore<AppSettings>

    @Inject
    @JvmField
    var messageTransport: MessageTransport? = null

    @Inject
    @JvmField
    var castContext: CastContext? = null

    @Inject
    lateinit var localeManager: LocaleManager

    @SuppressLint("WrongConstant")
    override fun onCreate() {
        super.onCreate()

        // Applied before StrictMode is armed below: on API < 33 this is a real disk read that
        // can't move off the main thread, since the result has to land before the first Activity
        // resolves its resources.
        localeManager.applyLocaleOnStartup()

        castContext?.let { context ->
            messageTransport?.let { messaging ->
                context.sessionManager.addSessionManagerListener(CastSessionListener(onStarted = {
                    messaging.onSessionStarted()
                    applicationScope.launch(ioDispatcher) {
                        val blankOnStart = dataStore.data.first().blankOnStart
                        messaging.sendBlank(blankOnStart)
                    }
                }, onEnded = { messaging.onSessionEnded() },
                    onResumed = { messaging.onSessionStarted() },
                    onSuspended = { messaging.onSessionEnded() }))
            }
        }

        DynamicColors.applyToActivitiesIfAvailable(this)

        applicationScope.launch(ioDispatcher) {
            dataStore.updateData { currentSettings ->
                if (currentSettings == AppSettings.getDefaultInstance()) {
                    AppSettings.newBuilder()
                        .setAppTheme(-1) // System default
                        .setControlButtonsHeight(ControlButtonHeightOption.DEFAULT.value)
                        .setBlankOnStart(false)
                        .setBackgroundColor("Black")
                        .setFontColor("White")
                        .setMaxFontSize(DEFAULT_MAX_FONT_SIZE)
                        .build()
                } else {
                    currentSettings
                }
            }
        }

        dataStore.data.onEach {
            var appTheme: Int? = it.appTheme
            appTheme = if (appTheme == 0) null else appTheme
            if (appTheme != null) {
                withContext(mainDispatcher) {
                    AppCompatDelegate.setDefaultNightMode(appTheme)
                }
            }
        }.flowOn(ioDispatcher).launchIn(applicationScope)

        val isDebuggable = applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
        if (isDebuggable) {
            setupStrictMode()
        }
    }

    private fun setupStrictMode() {
        val threadPolicy =
            StrictMode.ThreadPolicy.Builder().detectAll().permitCustomSlowCalls().penaltyLog()
                .penaltyDialog().build()

        StrictMode.setThreadPolicy(threadPolicy)

        val vmPolicy =
            StrictMode.VmPolicy.Builder().detectActivityLeaks().detectFileUriExposure().penaltyLog()
                .build()

        StrictMode.setVmPolicy(vmPolicy)
    }
}
