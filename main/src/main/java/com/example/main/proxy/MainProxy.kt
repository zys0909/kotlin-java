package com.example.main.proxy

import com.example.main.api.ApiService
import com.example.main.proxy.code.ProxyGenerator
import com.example.main.proxy.test.Message
import kotlinx.coroutines.*
import okhttp3.Call
import okhttp3.Response
import java.io.FileOutputStream
import java.io.IOException
import java.lang.reflect.Proxy

/**
 * 描述:
 *
 * author zys
 * create by 2020/08/16
 */
object MainProxy {
    @JvmStatic
    fun main(args: Array<String>) {
//        val proxyTest = proxyTest(AMessage())
//        (proxyTest as Message).message()
        runApi()
//        runMyApi()

    }

    private fun runMyApi() {
        val weather:okhttp3.Call = ApiService.myAMapApi.getWeather("110101", "ae6c53e2186f33bbf240a12d80672d1b")
        weather.enqueue(object :okhttp3.Callback{
            override fun onFailure(call: Call, e: IOException) {

            }

            override fun onResponse(call: Call, response: Response) {
               print(response.body?.string())
            }
        })
    }

    private fun runApi() = runBlocking {
        val weather = ApiService.aMapApi.getWeather("110101", "ae6c53e2186f33bbf240a12d80672d1b")
        println(weather.body()?.string())
    }

    private fun proxyTest(t: Any): Any {
        return Proxy.newProxyInstance(MainProxy.javaClass.classLoader, arrayOf(Message::class.java)) { _, m, args ->
            m?.invoke(t, *(args ?: arrayOfNulls<Any>(0)))
        }
    }

    private fun proxyWrite() {
        val name = Message::class.java.name + "\$Proxy0"
        val byte = ProxyGenerator.generateProxyClass(name, arrayOf(Message::class.java))


        val fos = FileOutputStream("main/src/main/java/com/example/main/proxy/test/$name.class")
        fos.write(byte)
        fos.close()
    }
}