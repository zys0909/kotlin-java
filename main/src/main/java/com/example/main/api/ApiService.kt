package com.example.main.api

import com.example.main.proxy.retrofit.MyRetrofit
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * 描述:
 *
 * author zys
 * create by 2020/08/16
 */
class ApiService {
    companion object {
        private val retrofit by lazy {
            Retrofit.Builder()
                    .baseUrl("https://restapi.amap.com")
                    .addConverterFactory(MoshiConverterFactory.create())
                    .build()
        }

        val aMapApi: AMapApi by lazy {
            retrofit.create(AMapApi::class.java)
        }

        private val aMapRetrofit by lazy {
            MyRetrofit.Builder()
                    .baseUrl("https://restapi.amap.com")
                    .build()
        }

        val myAMapApi: MyAMapApi by lazy {
            aMapRetrofit.create(MyAMapApi::class.java)
        }
    }
}