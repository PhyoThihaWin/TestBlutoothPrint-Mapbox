package com.example.testbluetoothprint.mapbox

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.example.testbluetoothprint.databinding.ActivityPlacePickerBinding
import com.example.testbluetoothprint.toast
import com.example.testbluetoothprint.utils.LocationPermissionHelper
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.plugin.animation.MapAnimationOptions.Companion.mapAnimationOptions
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.attribution.attribution
import com.mapbox.maps.plugin.delegates.listeners.OnCameraChangeListener
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.logo.logo
import com.mapbox.maps.plugin.scalebar.scalebar
import com.mapbox.search.*
import com.mapbox.search.result.SearchResult
import java.lang.ref.WeakReference


class PlacePickerActivity : AppCompatActivity() {

    private lateinit var locationPermissionHelper: LocationPermissionHelper
    private var userPoint: Point? = null
    private var initialMapState = true

    private val onIndicatorPositionChangedListener = OnIndicatorPositionChangedListener {
        if (initialMapState) {
            mapView.getMapboxMap().setCamera(CameraOptions.Builder().center(it).build())
            mapView.gestures.focalPoint = mapView.getMapboxMap().pixelForCoordinate(it)
        }
        userPoint = it
    }

    private val onMoveListener = object : OnMoveListener {
        override fun onMoveBegin(detector: MoveGestureDetector) {
            //--remove tracking camera after initial state
            initialMapState = false

            if (binding?.mapLayout?.mapboxPluginsImageViewMarker?.translationY == 0f) {
                binding?.mapLayout?.mapboxPluginsImageViewMarker?.animate()?.translationY(-75f)
                    ?.setInterpolator(OvershootInterpolator())?.setDuration(250)?.start()
                binding?.mapLayout?.tvPlaceName?.visibility = View.GONE
            }
        }

        override fun onMove(detector: MoveGestureDetector): Boolean {
            return false
        }

        override fun onMoveEnd(detector: MoveGestureDetector) {
            binding?.mapLayout?.mapboxPluginsImageViewMarker?.animate()?.translationY(0f)
                ?.setInterpolator(OvershootInterpolator())?.setDuration(250)?.start()
            searchPlaceFromPoint(mapView.getMapboxMap().cameraState.center)
        }
    }


//    private val onCameraChangeListener = OnCameraChangeListener { cameraChangedEventData ->
//        toast(
//            mapView.getMapboxMap().cameraState.center.latitude()
//                .toString() + " : " + mapView.getMapboxMap().cameraState.center.longitude()
//                .toString()
//        )
//    }


    private lateinit var mapView: MapView


    private var binding: ActivityPlacePickerBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlacePickerBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        mapView = binding?.mapLayout?.mapView!!
        mapView.logo.updateSettings {
            enabled = false
        }
        mapView.attribution.updateSettings {
            enabled = false
        }
        mapView.scalebar.updateSettings {
            enabled = false
        }



        locationPermissionHelper = LocationPermissionHelper(WeakReference(this))
        locationPermissionHelper.checkPermissions {
            onMapReady()
        }


        binding?.userLocationButton?.setOnClickListener {
            userPoint?.let {
                toast(
                    it.latitude()
                        .toString() + " : " + it.longitude()
                        .toString()
                )
                mapView.getMapboxMap().flyTo(
                    cameraOptions {
                        center(it) // Sets the new camera position on click point
                        zoom(16.0) // Sets the zoom
                    },
                    mapAnimationOptions {
                        duration(1000)
                    }
                )
            }
        }

    }

    private fun onMapReady() {
        mapView.getMapboxMap().setCamera(
            CameraOptions.Builder()
                .zoom(16.0)
                .build()
        )
        mapView.getMapboxMap().loadStyleUri(
            Style.MAPBOX_STREETS
        ) {
            initLocationComponent()
            setupGesturesListener()
        }

    }

    private fun setupGesturesListener() {
        mapView.gestures.addOnMoveListener(onMoveListener)
//        mapView.getMapboxMap().addOnCameraChangeListener(onCameraChangeListener)
    }

    private fun initLocationComponent() {
        val locationComponentPlugin = mapView.location
        locationComponentPlugin.updateSettings {
            this.enabled = true
            this.pulsingEnabled = true
//            this.locationPuck = LocationPuck2D(
//                bearingImage = AppCompatResources.getDrawable(
//                    this@PlacePickerActivity,
//                    com.mapbox.maps.R.drawable.mapbox_user_puck_icon,
//                ),
//                shadowImage = AppCompatResources.getDrawable(
//                    this@PlacePickerActivity,
//                    com.mapbox.maps.R.drawable.mapbox_user_icon_shadow,
//                ),
//                scaleExpression = interpolate {
//                    linear()
//                    zoom()
//                    stop {
//                        literal(0.0)
//                        literal(0.6)
//                    }
//                    stop {
//                        literal(20.0)
//                        literal(1.0)
//                    }
//                }.toJson()
//            )
        }
        locationComponentPlugin.addOnIndicatorPositionChangedListener(
            onIndicatorPositionChangedListener
        )


    }

    private fun onCameraTrackingDismissed() {
        Log.i("tracking", "onCameraTrackingDismissed")
        mapView.location.removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        mapView.gestures.removeOnMoveListener(onMoveListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.location.removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        mapView.gestures.removeOnMoveListener(onMoveListener)
//        mapView.getMapboxMap().removeOnCameraChangeListener(onCameraChangeListener)
        searchRequestTask.cancel()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        locationPermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


//    override fun onCameraMoveStarted(reason: Int) {
//        Timber.v("Map camera has begun moving.")
//        if (binding?.mapLayout?.mapboxPluginsImageViewMarker?.translationY == 0f) {
//            binding?.mapLayout?.mapboxPluginsImageViewMarker?.animate()?.translationY(-75f)?.setInterpolator(OvershootInterpolator())?.setDuration(250)?.start()
//            if (includeReverseGeocode) {
//                toast("Showed bottomsheet.")
//            }
//        }
//    }
//
//    override fun onCameraIdle() {
//        Timber.v("Map camera is now idling.")
//        binding?.mapLayout?.mapboxPluginsImageViewMarker?.animate()?.translationY(0f)?.setInterpolator(OvershootInterpolator())?.setDuration(250)?.start()
//        if (includeReverseGeocode) {
//
//            // Initialize with the markers current location information.
//            makeReverseGeocodingSearch()
//        }
//    }


    fun searchPlaceFromPoint(point: Point) {
        reverseGeocoding = MapboxSearchSdk.getReverseGeocodingSearchEngine()

        val options = ReverseGeoOptions(
            center = point,
            limit = 1
        )
        searchRequestTask = reverseGeocoding.search(options, searchCallback)
    }

    private lateinit var reverseGeocoding: ReverseGeocodingSearchEngine
    private lateinit var searchRequestTask: SearchRequestTask

    private val searchCallback = object : SearchCallback {

        override fun onResults(results: List<SearchResult>, responseInfo: ResponseInfo) {
            if (results.isEmpty()) {
                Log.i("SearchApiExample", "No reverse geocoding results")
            } else {
                Log.i("SearchApiExample", "Reverse geocoding results: $results")
                binding?.mapLayout?.tvPlaceName?.let {
                    it.visibility = View.VISIBLE
                    it.text = "${results[0].name}, ${results[0].address?.let { it.region + ", " + it.country }}"
                }
            }
        }

        override fun onError(e: Exception) {
            Log.i("SearchApiExample", "Reverse geocoding error", e)
        }
    }



}