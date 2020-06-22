package com.lj.mapnavigation.manager

import android.content.Context
import android.util.Log
import com.amap.api.maps.model.LatLng
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.geocoder.GeocodeResult
import com.amap.api.services.geocoder.GeocodeSearch
import com.amap.api.services.geocoder.RegeocodeQuery
import com.amap.api.services.geocoder.RegeocodeResult

/**
 * Created by liujie_gyh on 2020/6/21.
 */
private const val TAG = "GeoSearchManager"
class GeoSearchManager(context: Context): GeocodeSearch.OnGeocodeSearchListener {

    private var mGeocodeSearch: GeocodeSearch

    private var mSearchResult: ((RegeocodeResult, Int) -> Unit)? = null


    init {
        mGeocodeSearch = GeocodeSearch(context)
        mGeocodeSearch.setOnGeocodeSearchListener(this)
    }

    fun searchLatLng(latLng: LatLng, searchResult: ((RegeocodeResult, Int) -> Unit)?) {
        mSearchResult = searchResult
        // 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        val query = RegeocodeQuery(
            LatLonPoint(latLng.latitude, latLng.longitude),
            10.0f,
            GeocodeSearch.AMAP
        )
        mGeocodeSearch.getFromLocationAsyn(query)
    }

    override fun onRegeocodeSearched(result: RegeocodeResult, rCode: Int) {
        Log.d(TAG, "onRegeocodeSearched() called with: result = $result, rCode = $rCode")
        mSearchResult?.let { it.invoke(result, rCode) }
    }


    override fun onGeocodeSearched(result: GeocodeResult, rCode: Int) {
    }

}