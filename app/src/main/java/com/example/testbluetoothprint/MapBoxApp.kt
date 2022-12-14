package com.example.testbluetoothprint

import android.app.Application
import com.mapbox.android.core.location.LocationEngineProvider
import com.mapbox.search.MapboxSearchSdk

class MapBoxApp : Application() {
    override fun onCreate() {
        super.onCreate()
        MapboxSearchSdk.initialize(
            application = this,
            accessToken = getString(R.string.mapbox_access_token),
            locationEngine = LocationEngineProvider.getBestLocationEngine(this)
        )
    }
}