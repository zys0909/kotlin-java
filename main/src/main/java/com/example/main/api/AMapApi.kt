package com.example.main.api

import com.example.main.proxy.retrofit.annotation.MyField
import com.example.main.proxy.retrofit.annotation.MyGET
import com.example.main.proxy.retrofit.annotation.MyPOST
import com.example.main.proxy.retrofit.annotation.MyQuery
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

/**
 * 描述:
 *
 * author zys
 * create by 2020/08/16
 */
interface AMapApi {

    @POST("/v3/weather/weatherInfo")
    @FormUrlEncoded
    suspend fun postWeather(@Field("city") city: String, @Field("key") key: String): Response<ResponseBody>

    @GET("/v3/weather/weatherInfo")
    suspend fun getWeather(@Query("city") city: String, @Query("key") key: String): Response<ResponseBody>
}