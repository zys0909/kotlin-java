package com.example.main.proxy;

import com.example.main.proxy.test.AMessage;
import com.example.main.proxy.test.Message;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 描述:
 * <p>
 * author zys
 * create by 2020/08/16
 */
public class MainProxyJava {
    public static void main(String[] args) {
        Object o = proxyTest(new AMessage());
        ((Message) o).message();

    }

    private static Object proxyTest(Object obj) {
        return Proxy.newProxyInstance(MainProxyJava.class.getClassLoader(), new Class[]{Message.class}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return method.invoke(obj, args);
            }
        });
    }
}
