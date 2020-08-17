package com.example.main.proxy.test

/**
 * 描述:
 *
 * author zys
 * create by 2020/08/16
 */
class ProxyMessage(val msg: Message) : Message {
    override fun message() {
        println("代理前")
        msg.message()
        println("代理后")

    }
}