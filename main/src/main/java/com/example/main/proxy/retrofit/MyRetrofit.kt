package com.example.main.proxy.retrofit

import okhttp3.Call
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.concurrent.ConcurrentHashMap


/**
 * 描述:
 *
 * author zys
 * create by 2020/08/16
 */
class MyRetrofit private constructor(val callFactory: okhttp3.Call.Factory, val baseUrl: HttpUrl) {
    private val serviceMethodCache = ConcurrentHashMap<Method, ServiceMethod>()

    fun <T> create(service: Class<T>): T {
        return Proxy.newProxyInstance(service.classLoader, arrayOf(service)) { o, method, args ->
            val serviceMethod = method?.let { loadServiceMethod(it) }
            serviceMethod?.invoke(args)
        } as T
    }

    private fun loadServiceMethod(method: Method): ServiceMethod {
        //先不上鎖，避免synchronized性能損失
        serviceMethodCache[method]?.apply {
            return this
        }
        //多线程下，避免重复解析
        val result: ServiceMethod
        synchronized(serviceMethodCache) {
            result = serviceMethodCache[method].let {
                it ?: ServiceMethod.Builder(this, method).build().apply {
                    serviceMethodCache[method] = this
                }
            }
        }
        return result
    }

    /**
     * 构建者模式，将一个复杂对象的构建和它的表示分离，可以使使用者不必知道内部组成的细节。
     */
    class Builder {
        private lateinit var baseUrl: HttpUrl

        //Okhttp->OkhttClient
        private var callFactory: Call.Factory? = null

        fun callFactory(factory: Call.Factory?): Builder {
            callFactory = factory
            return this
        }

        fun baseUrl(baseUrl: String): Builder {
            this.baseUrl = baseUrl.toHttpUrl()
            return this
        }

        fun build(): MyRetrofit {
            var callFactory = callFactory
            if (callFactory == null) {
                callFactory = OkHttpClient()
            }
            return MyRetrofit(callFactory, baseUrl)
        }
    }
}