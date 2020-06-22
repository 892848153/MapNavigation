package com.lj.mapnavigation

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.amap.api.maps.AMap
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.MarkerOptions
import com.amap.api.maps.model.Poi
import com.amap.api.navi.AmapNaviPage
import com.amap.api.navi.AmapNaviParams
import com.amap.api.navi.AmapNaviType
import com.lj.mapnavigation.databinding.ActivityMainBinding
import com.lj.mapnavigation.manager.AutoLocationManager
import com.lj.mapnavigation.manager.GeoSearchManager
import kotlinx.android.synthetic.main.activity_main.*


private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity(), AMap.OnMapLongClickListener {

    private lateinit var viewModel: MapViewModel

    private lateinit var aMap: AMap

    private lateinit var mAutoLocationManager: AutoLocationManager

    private val mGeoSearchManager: GeoSearchManager by lazy { GeoSearchManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val databinding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        viewModel = ViewModelProviders.of(this).get(MapViewModel::class.java)
        databinding.lifecycleOwner = this
        databinding.viewModel = viewModel

        map_view.onCreate(savedInstanceState)
        aMap = map_view.map
        aMap.setOnMapLongClickListener(this)

        mAutoLocationManager = AutoLocationManager(this, aMap)
        mAutoLocationManager.startAutoLocation()

        observerData()
    }

    private fun observerData() {
        viewModel.dest.observe(this, Observer {
            it?.let {
                aMap.addMarker(MarkerOptions().position(it.latLng).snippet(it.formatAddress)).let {marker ->
                    viewModel.dest.value?.marker = marker
                }
            }
        })
    }

    override fun onMapLongClick(latLng: LatLng) {
        mGeoSearchManager.searchLatLng(latLng) { result, rCode ->
            Log.d(TAG, "geo search result callback invoked: rCode:$rCode")
            removePreMarkerIfNeeded()

            viewModel.dest.value =
                MapViewModel.Destination(latLng, result.regeocodeAddress.formatAddress, null)
        }
    }

    fun startNav(view: View) {
        viewModel.dest.value?.let {
            val end = Poi(it.formatAddress, it.latLng, "")
            AmapNaviPage.getInstance().showRouteActivity(
                this,
                AmapNaviParams(null, null, end, AmapNaviType.DRIVER), null
            )

            removePreMarkerIfNeeded()
            viewModel.dest.value = null
        }
    }

    private fun removePreMarkerIfNeeded() {
        viewModel.dest.value?.let {
            it.marker?.let { marker ->
                if (marker.isRemoved) {
                    marker.remove()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        mAutoLocationManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    override fun onResume() {
        super.onResume()
        map_view.onResume()
    }

    override fun onPause() {
        super.onPause()
        map_view.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        map_view.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        map_view.onDestroy()
    }

}