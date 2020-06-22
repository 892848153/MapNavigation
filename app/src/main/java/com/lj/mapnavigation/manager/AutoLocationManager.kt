package com.lj.mapnavigation.manager

import android.Manifest
import android.content.Context
import android.location.LocationManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.LocationSource
import com.amap.api.maps.model.MyLocationStyle
import com.lj.mapnavigation.util.LocationLogger
import com.lj.mapnavigation.util.Toaster
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

/**
 * Created by liujie_gyh on 2020/6/20.
 */
private const val TAG = "AutoLocationManager"
class AutoLocationManager(val activity: AppCompatActivity, aMap: AMap): LifecycleObserver,
    EasyPermissions.PermissionCallbacks, LocationSource, AMapLocationListener {


    private var mAMap: AMap

    private var mLocationClient: AMapLocationClient? = null
    private var locationOption: AMapLocationClientOption? = null
    private var mStartAutoLocation: Boolean = false

    var mListener: LocationSource.OnLocationChangedListener? = null


    private val locationPerms = arrayOf<String>(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    init {
        activity.lifecycle.addObserver(this)
        mAMap = aMap
    }

    /**
     * 开始自定定位
     */
    fun startAutoLocation() {
        Log.d(TAG, "startAutoLocation() called")
        if (mStartAutoLocation) {
            return
        }

        mStartAutoLocation = true

        val isGpsEnable = (activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager).isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (!isGpsEnable) {
            Toaster.showLong(activity, "您尚未开启GPS, 开启GPS定位更精准哦")
        }

        startLocationRequiredTwoPermission()
    }

    fun stopAutoLocation() {
        if (mStartAutoLocation) {
            mStartAutoLocation = false
            deactivate()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        Log.d(TAG, "onCreate() called")
    }

    @AfterPermissionGranted(REQUEST_PERMISSION_LOCATION)
    private fun startLocationRequiredTwoPermission() {
        val perms = arrayOf<String>(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (EasyPermissions.hasPermissions(activity, *perms)) {
            Log.i(TAG, "has location permissions , we can request to location now ")
            // Already have permission, do the thing
            startLocation()
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(
                activity, "app无定位权限无法正常使用", REQUEST_PERMISSION_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        Toaster.showShort(activity, "app无定位权限无法正常使用, 请不要拒绝授予权限")
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        startLocation()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults, this
        )
    }

    private fun startLocation() {
        val myLocationStyle: MyLocationStyle = MyLocationStyle()
        myLocationStyle.interval(5000) //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        // 第一次只定位一次，并把视角移到当前位置
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE)
        myLocationStyle.showMyLocation(true)

        mAMap.moveCamera(CameraUpdateFactory.zoomTo(17f))

        mAMap.myLocationStyle = myLocationStyle //设置定位蓝点的Style

        //设置默认定位按钮是否显示，非必需设置。
        mAMap.uiSettings.isMyLocationButtonEnabled  = true

        // 设置定位器是否启用的监听
        mAMap.setLocationSource(this)
        // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        mAMap.isMyLocationEnabled = true
    }

    override fun activate(listener: LocationSource.OnLocationChangedListener?) {
        Log.i(TAG, "activate: location source is active, so we can start location")
        mListener = listener

        if (mLocationClient == null) {
            //初始化定位
            mLocationClient = AMapLocationClient(activity)
            //初始化定位参数
            locationOption = AMapLocationClientOption()
            //设置定位回调监听
            mLocationClient?.setLocationListener(this)
            //设置为高精度定位模式
            locationOption?.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy)
            //设置定位参数
            mLocationClient?.setLocationOption(locationOption)
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        }

        mLocationClient?.startLocation() //启动定位
    }

    private var mLocation: AMapLocation? = null

    override fun onLocationChanged(location: AMapLocation?) {
        if (location != null && location.errorCode == 0) {
            mListener?.onLocationChanged(location);// 显示系统小蓝点
        }
        if (mLocation == null) {
            val myLocationStyle: MyLocationStyle = MyLocationStyle()
            myLocationStyle.interval(5000) //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
            // 第一次只定位一次，并把视角移到当前位置
            myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER)
            myLocationStyle.showMyLocation(true)
            mAMap.myLocationStyle = myLocationStyle
//            aMap.myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER);
            mAMap.isMyLocationEnabled = true
        }

        mLocation = location
        LocationLogger.logLocation(location)
    }

    override fun deactivate() {
        Log.i(TAG, "deactivate: ")
        mListener = null
        mLocationClient?.stopLocation()
        mLocationClient?.onDestroy()
        mLocationClient = null
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        Log.i(TAG, "onStart: ")
        if (mStartAutoLocation && EasyPermissions.hasPermissions(activity, *locationPerms)) {
            Log.i(TAG, "change location style and start location: ")
            // Already have permission, do the thing

            mLocationClient?.startLocation()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
     fun onResume() {
        Log.i(TAG, "onResume: ")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
     fun onPause() {
        Log.i(TAG, "onPause: ")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
     fun onStop() {
        Log.i(TAG, "onStop: ")
        mLocationClient?.stopLocation()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
     fun onDestroy() {
        Log.i(TAG, "onDestroy: ")
        /**
         * 如果AMapLocationClient是在当前Activity实例化的，
         * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
         */
        mLocationClient?.onDestroy()
    }


    companion object {
        const val REQUEST_PERMISSION_LOCATION = 123
    }


}