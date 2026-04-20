package com.softcat.adventuremaker.screens.search.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.softcat.adventuremaker.screens.search.model.MapState
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

@Composable
fun OpenStreetMapView(
    modifier: Modifier = Modifier,
    state: MapState
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var previousCenter by remember { mutableStateOf<GeoPoint?>(null) }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            MapView(context).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setBuiltInZoomControls(true)
                setMultiTouchControls(true)
                isTilesScaledToDpi = true
                controller.setZoom(state.zoom)
                state.center.let { controller.setCenter(it) }

                val locationOverlay = MyLocationNewOverlay(
                    GpsMyLocationProvider(context),
                    this
                ).apply { enableMyLocation() }
                overlays.add(locationOverlay)
                mapView = this
            }
        },
        update = { view ->
            view.apply {
                if (previousCenter == null || state.center != previousCenter) {
                    view.controller.animateTo(state.center)
                    previousCenter = state.center
                    view.postInvalidate()
                }
                if (zoomLevelDouble != state.zoom) {
                    controller.setZoom(state.zoom)
                }
                view.overlays.removeIf { it is Marker }
                state.center.let { controller.setCenter(it) }
                val markers = state.places.map { placePin ->
                    Marker(this).apply {
                        position = placePin.point
                        title = placePin.title
                    }
                }
                overlays.addAll(markers)
            }
        }
    )

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView?.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView?.onPause()
                Lifecycle.Event.ON_DESTROY -> {
                    mapView?.onDetach()
                    mapView = null
                }
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}