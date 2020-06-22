package com.lj.mapnavigation.util

import android.util.Log
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationQualityReport
import com.lj.mapnavigation.BuildConfig


/**
 * Created by liujie_gyh on 2020/6/20.
 */

object LocationLogger {

    private const val TAG = "LocationLogger"

    fun logLocation(location: AMapLocation?) {
        if (!BuildConfig.DEBUG) {
            return
        }


        if (null != location) {
            val sb = StringBuffer()
            //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
            if (location.getErrorCode() == 0) {
                sb.append(
                    """
                        定位成功
                        
                        """.trimIndent()
                )
                sb.append(
                    """
                        定位类型: ${location.getLocationType()}
                        
                        """.trimIndent()
                )
                sb.append(
                    """
                        经    度    : ${location.getLongitude()}
                        
                        """.trimIndent()
                )
                sb.append(
                    """
                        纬    度    : ${location.getLatitude()}
                        
                        """.trimIndent()
                )
                sb.append(
                    """
                        精    度    : ${location.getAccuracy()}米
                        
                        """.trimIndent()
                )
                sb.append(
                    """
                        提供者    : ${location.getProvider()}
                        
                        """.trimIndent()
                )
                sb.append(
                    """
                        速    度    : ${location.getSpeed()}米/秒
                        
                        """.trimIndent()
                )
                sb.append(
                    """
                        角    度    : ${location.getBearing()}
                        
                        """.trimIndent()
                )
                // 获取当前提供定位服务的卫星个数
                sb.append(
                    """
                        星    数    : ${location.getSatellites()}
                        
                        """.trimIndent()
                )
                sb.append(
                    """
                        国    家    : ${location.getCountry()}
                        
                        """.trimIndent()
                )
                sb.append(
                    """
                        省            : ${location.getProvince()}
                        
                        """.trimIndent()
                )
                sb.append(
                    """
                        市            : ${location.getCity()}
                        
                        """.trimIndent()
                )
                sb.append(
                    """
                        城市编码 : ${location.getCityCode()}
                        
                        """.trimIndent()
                )
                sb.append(
                    """
                        区            : ${location.getDistrict()}
                        
                        """.trimIndent()
                )
                sb.append(
                    """
                        区域 码   : ${location.getAdCode()}
                        
                        """.trimIndent()
                )
                sb.append(
                    """
                        地    址    : ${location.getAddress()}
                        
                        """.trimIndent()
                )
                sb.append(
                    """
                        兴趣点    : ${location.getPoiName()}
                        
                        """.trimIndent()
                )
                //定位完成的时间
                sb.append(
                    "定位时间: " + Utils.formatUTC(location.getTime(), "yyyy-MM-dd HH:mm:ss")
                        .toString() + "\n"
                )
            } else {
                //定位失败
                sb.append(
                    """
                        定位失败
                        
                        """.trimIndent()
                )
                sb.append(
                    """
                        错误码:${location.getErrorCode()}
                        
                        """.trimIndent()
                )
                sb.append(
                    """
                        错误信息:${location.getErrorInfo()}
                        
                        """.trimIndent()
                )
                sb.append(
                    """
                        错误描述:${location.getLocationDetail()}
                        
                        """.trimIndent()
                )
            }
            sb.append("***定位质量报告***").append("\n")
            sb.append("* WIFI开关：")
                .append(if (location.getLocationQualityReport().isWifiAble()) "开启" else "关闭")
                .append("\n")
            sb.append("* GPS状态：")
                .append(getGPSStatusString(location.getLocationQualityReport().getGPSStatus()))
                .append("\n")
            sb.append("* GPS星数：").append(location.getLocationQualityReport().getGPSSatellites())
                .append("\n")
            sb.append("* 网络类型：" + location.getLocationQualityReport().getNetworkType()).append("\n")
            sb.append("* 网络耗时：" + location.getLocationQualityReport().getNetUseTime()).append("\n")
            sb.append("****************").append("\n")
            //定位之后的回调时间
            sb.append(
                "回调时间: " + Utils.formatUTC(
                    System.currentTimeMillis(),
                    "yyyy-MM-dd HH:mm:ss"
                ).toString() + "\n"
            )

            //解析定位结果，
            val result = sb.toString()
            Log.d(TAG, "onLocationChanged: $result")
        } else {
            Log.w(TAG, "onLocationChanged:定位失败，loc is null ")
        }
    }

    /**
     * 获取GPS状态的字符串
     * @param statusCode GPS状态码
     * @return
     */
    private fun getGPSStatusString(statusCode: Int): String? {
        var str = ""
        when (statusCode) {
            AMapLocationQualityReport.GPS_STATUS_OK -> str = "GPS状态正常"
            AMapLocationQualityReport.GPS_STATUS_NOGPSPROVIDER -> str =
                "手机中没有GPS Provider，无法进行GPS定位"
            AMapLocationQualityReport.GPS_STATUS_OFF -> str = "GPS关闭，建议开启GPS，提高定位质量"
            AMapLocationQualityReport.GPS_STATUS_MODE_SAVING -> str =
                "选择的定位模式中不包含GPS定位，建议选择包含GPS定位的模式，提高定位质量"
            AMapLocationQualityReport.GPS_STATUS_NOGPSPERMISSION -> str = "没有GPS定位权限，建议开启gps定位权限"
        }
        return str
    }
}