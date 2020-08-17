package com.example.main.api

import com.example.main.proxy.retrofit.annotation.MyField
import com.example.main.proxy.retrofit.annotation.MyGET
import com.example.main.proxy.retrofit.annotation.MyPOST
import com.example.main.proxy.retrofit.annotation.MyQuery
import okhttp3.Call
import retrofit2.http.FormUrlEncoded

/**
 * 描述:
 *
 * author zys
 * create by 2020/08/16
 */
interface MyAMapApi {

    @MyPOST("/v3/weather/weatherInfo")
    @FormUrlEncoded
     fun postWeather(@MyField("city") city: String, @MyField("key") key: String): Call

    @MyGET("/v3/weather/weatherInfo")
     fun getWeather(@MyQuery("city") city: String, @MyQuery("key") key: String): Call
}