package com.example.main.proxy.retrofit.annotation

/**
 * 描述:
 *
 * author zys
 * create by 2020/08/16
 */

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class MyQuery(val value: String)