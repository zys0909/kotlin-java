package com.example.main.fanxing;

class Food {
}

class Fruit extends Food {
}

class Apple extends Fruit {
}

class Plates<T> {
    private T t;

    public T getT() {
        return t;
    }

    public void setT(T t) {
        this.t = t;
    }
}

/**
 * 泛型相关测试
 */
public class JavaTest {
    public static void main(String[] args) {
        //1,
        Plates<Fruit> p0 = new Plates<>();
        p0.setT(new Apple());
        Fruit t0 = p0.getT();
        //2,下界
        Plates<? super Fruit> p1 = new Plates<>();
        p1.setT(new Apple());
//        Fruit t1 = p1.getT();
        //3，上界
        Plates<? extends Fruit> p2 = new Plates<>();
//        p2.setT(new Apple());
        Fruit t2 = p2.getT();
        //4，通配符
        Plates<?> p3 = new Plates<>();
//        p3.setT(new Object());
        Object t = p3.getT();
    }
}
