package com.example.main.proxy.retrofit

/**
 * 描述:
 *
 * author zys
 * create by 2020/08/16
 */
abstract class ParameterHandler {

    abstract fun apply(serviceMethod: ServiceMethod, value: String)

    class MyQueryParameterHandler(private val key: String) : ParameterHandler() {
        override fun apply(serviceMethod: ServiceMethod, value: String) {
            serviceMethod.addQueryParameter(key,value)
        }
    }

    class MyFiledParameterHandler(private val key: String) : ParameterHandler() {
        override fun apply(serviceMethod: ServiceMethod, value: String) {
            serviceMethod.addFiledParameter(key,value)
        }
    }
}