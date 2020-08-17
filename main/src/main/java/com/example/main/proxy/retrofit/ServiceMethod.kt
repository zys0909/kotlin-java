package com.example.main.proxy.retrofit

import com.example.main.proxy.retrofit.annotation.MyField
import com.example.main.proxy.retrofit.annotation.MyGET
import com.example.main.proxy.retrofit.annotation.MyPOST
import com.example.main.proxy.retrofit.annotation.MyQuery
import okhttp3.FormBody
import okhttp3.HttpUrl
import okhttp3.Request
import java.lang.reflect.Method
import kotlin.properties.Delegates

/**
 * 描述:
 *
 * author zys
 * create by 2020/08/16
 */
class ServiceMethod private constructor() {

    lateinit var callFactory: okhttp3.Call.Factory

    lateinit var baseUrl: HttpUrl

    lateinit var baseHttpUrl: HttpUrl

    lateinit var httpMethod: String

    lateinit var relativeUrl: String

    var hasBody by Delegates.notNull<Boolean>()

    lateinit var parameterHandlerArray: MutableList<ParameterHandler>

    private var urlBuilder: HttpUrl.Builder? = null
    private var formBuilder: FormBody.Builder? = null

    constructor(builder: Builder) : this() {
        this.callFactory = builder.myRetrofit.callFactory
        this.baseHttpUrl = builder.myRetrofit.baseUrl
        this.httpMethod = builder.httpMethod
        this.relativeUrl = builder.relativeUrl
        this.hasBody = builder.hasBody
        this.parameterHandlerArray = builder.parameterHandlerArray

        if (this.hasBody) {
            formBuilder = FormBody.Builder()
        }
    }

    fun invoke(args: Array<Any>): Any? {
        parameterHandlerArray.forEachIndexed { index, parameterHandler ->
            parameterHandler.apply(this, args[index].toString())
        }

        //
        if (urlBuilder == null) {
            urlBuilder = baseHttpUrl.newBuilder(relativeUrl)
        }
        val url: HttpUrl = urlBuilder.let {
            it?.build() ?: (baseHttpUrl.newBuilder(relativeUrl)?.build() ?: baseHttpUrl)
        }
        val formBody: FormBody? = formBuilder?.build()

        val build = Request.Builder().url(url).method(this.httpMethod, formBody).build()
        return callFactory.newCall(build)
    }

    fun addQueryParameter(key: String, value: String) {
        if (urlBuilder == null) {
            urlBuilder = baseHttpUrl.newBuilder(relativeUrl)
        }
        urlBuilder?.addQueryParameter(key, value)
    }

    fun addFiledParameter(key: String, value: String) {
        formBuilder?.add(key, value)
    }

    class Builder(val myRetrofit: MyRetrofit, val method: Method) {
        //获取所有方法上的注解
        private val methodAnnotations: Array<Annotation> = method.annotations

        //获得方法参数的注解，（一个方法有多个参数，一个参数可以有多个注解）
        private val parameterAnnotations: Array<Array<Annotation>> = method.parameterAnnotations

        lateinit var parameterHandlerArray: MutableList<ParameterHandler>

        var httpMethod: String = ""

        var relativeUrl: String = ""

        var hasBody: Boolean = false


        fun build(): ServiceMethod {
            /**
             * 1，解析方法上的注解
             */
            methodAnnotations.forEach {
                if (it is MyPOST) {
                    //记录请求方法与Path
                    this.httpMethod = "POST"
                    this.relativeUrl = it.value
                    this.hasBody = true
                } else if (it is MyGET) {
                    this.httpMethod = "GET"
                    this.relativeUrl = it.value
                    this.hasBody = false
                }
            }
            /**
             * 2，解析方法参数的注解
             */
            parameterHandlerArray = mutableListOf()
            parameterAnnotations.forEachIndexed { i, annotations ->
                annotations.forEach {
                    if (it is MyField) {
                        if (this.httpMethod == "GET") {
                            throw IllegalArgumentException("GET method not has MyField annotation")
                        }
                        parameterHandlerArray.add(ParameterHandler.MyFiledParameterHandler(it.value))
                    } else if (it is MyQuery) {
                        if (this.httpMethod == "POST") {
                            throw IllegalArgumentException("POST method not has MyQuery annotation")
                        }
                        parameterHandlerArray.add(ParameterHandler.MyQueryParameterHandler(it.value))
                    }
                }
            }
            return ServiceMethod(this)
        }
    }
}