package edu.niit.android.criminalintent;

/**
 * 静态内部类方式
 */
public class Singleton {
    private Singleton() {}

    //静态内部类，当使用内部类是才会进行加载
    private static class SingletonHolder{
        public static Singleton instance = new Singleton();
    }

    public static Singleton getInstance() {
        return SingletonHolder.instance;
    }

    public void doSomething() {
        // do something......
    }
}
