package com.example.main.rxjava

/**
 * 描述:
 *
 * author zys
 * create by 2020/08/24
 */
interface Observer<T> {

    fun onNext(t: T)
}