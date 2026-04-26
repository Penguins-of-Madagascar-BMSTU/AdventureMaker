package com.softcat.adventuremaker

import android.app.Application
import android.os.Build
import org.osmdroid.config.Configuration
import androidx.datastore.preferences.preferencesDataStore
import com.example.data.BuildConfig
import com.google.firebase.FirebaseApp
import com.softcat.adventuremaker.di.dataModule
import com.softcat.adventuremaker.di.repositoryModule
import com.softcat.adventuremaker.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import java.io.File

class AdventureMakerApplication: Application() {

    val dataStore by preferencesDataStore(
        name = DATASTORE_NAME
    )

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@AdventureMakerApplication)
            modules(viewModelModule, repositoryModule, dataModule)
        }
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(applicationContext)
        }
        initOsmConfig()
    }

    private fun initOsmConfig() {
        val versionName = try {
            packageManager.getPackageInfo(packageName, 0).versionName
        } catch (_: Exception) { "1.0" } ?: "1.0"

        Configuration.getInstance().load(
            applicationContext,
            getSharedPreferences("osmdroid", MODE_PRIVATE)
        )

        Configuration.getInstance().apply {
            userAgentValue = "AdventureMaker/$versionName " +
                    "(Android ${Build.VERSION.RELEASE}; contact@softcat.com)"

            val externalDir = applicationContext.getExternalFilesDir(null)
            osmdroidTileCache = if (externalDir != null && externalDir.canWrite()) {
                File(externalDir, "osmdroid").apply { if (!exists()) mkdirs() }
            } else {
                File(applicationContext.cacheDir, "osmdroid").apply { if (!exists()) mkdirs() }
            }

            tileFileSystemCacheMaxBytes = 100L * 1024 * 1024

            if (BuildConfig.DEBUG) {
                setDebugMode(true)
            }
        }
    }

    companion object {
        const val DATASTORE_NAME = "adventure_maker_datastore"
    }
}