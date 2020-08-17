package com.example.main.proxy.retrofit.annotation

/**
 * 描述:
 *
 * author zys
 * create by 2020/08/16
 */

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class MyGET(val value: String)