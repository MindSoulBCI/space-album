package com.example.spatialalbum.p20261115.platform

import android.app.Application
import com.pico.spatial.ui.foundation.dsl.launch
import com.example.spatialalbum.p20261115.mainApp

class SpatialApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        launch(::mainApp)
    }
}
