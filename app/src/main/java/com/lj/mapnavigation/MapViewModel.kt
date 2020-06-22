package com.lj.mapnavigation

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.Marker

/**
 * Created by liujie_gyh on 2020/6/20.
 */
class MapViewModel : ViewModel() {


    val dest: MutableLiveData<Destination> by lazy { MutableLiveData<Destination>() }


    val startNavBtnEnabled: MediatorLiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        addSource(dest) {
            value = it != null
        }
    }

   data class Destination(
       val latLng: LatLng,
       val formatAddress: String,
       var marker: Marker?
   )

}