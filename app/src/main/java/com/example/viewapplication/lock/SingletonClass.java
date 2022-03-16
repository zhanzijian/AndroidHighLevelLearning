package com.example.viewapplication.lock;

/**
 * @author zhanzijian
 * @description 单例模式
 * @date 2022/03/06 22:55
 */
public class SingletonClass {
    private static volatile SingletonClass instance;

    private SingletonClass() {
    }

    /*
     普通的单例模式会这样写
     1、这里想想，如果线程1和线程2同时访问该方法，这样就会同时通过空判断，进入初始化代码块，线程1拿到的对象是A
     线程B拿到的对象是B，这样A和B就会有一个是失效的，这样我们就需要把初始化的方法加上同步锁，但是如果这样做的话
        public synchronized static SingletonClass getInstance(){
            if (instance == null){
                instance = new SingletonClass();
            }
            return instance;
        }
     这个方法就会很重，每次拿单例对象的时候都需要排队等着，即使对象已经完成初始化了
     2、所以我们需要把synchronized放到instance = new SingletonClass()外面
        public static SingletonClass getInstance(){
            if (instance == null){
                synchronized(SingletonClass.class){
                    instance = new SingletonClass();
                }
            }
            return instance;
        }
        这样的话又会出现另一个问题，就是如果线程A和线程B都进入到 synchronized(SingletonClass.class) 方法时，
        还是会初始化两次，因为 synchronized 只是对 instance = new SingletonClass() 初始化方法加了锁，并不是对
        单例对象加锁啊
      3、所以这里我们还需要在 synchronized 代码块里再进行一次空判断，避免第一次初始化两个线程同时执行的时候还是会初始化
         两次的问题
         static SingletonClass getInstance() {
            if (instance == null) {
                synchronized (SingletonClass.class) {
                    if (instance == null) {
                        instance = new SingletonClass();
                    }
                }
            }
            return instance;
        }
       4、好了，到这里为止，看上去已经没什么问题了，这样外部通过调用getInstance()获取的对象一定是单例的
         但是，会存在一种极端情况，jvm虚拟机会对对象的初始化做指令重排，线程A调getInstance()初始化了SingletonClass，
         然而虚拟机先把对象的内存创建出来，并且也指向了SingletonClass，但是构造函数还在进行中呢，里面的内容还没初始化好呢，
         如果此时线程B调getInstance()方法，走instance == null判断，程序告诉他，已经有一个对象了，你用把，然后B用的
         时候就出问题了，因为我这个对象还没完全初始化好呢，内容还没进行初始化呢，这样B拿到的对象就可以能产生问题了，如何解决？
         简单，在instance成员变量加个volatile，保证instance成员变量的原子性，这样就会取消jvm虚拟机的指令重排
       5、这样我们的单例模式才是完美的
    */
    static SingletonClass getInstance() {
        if (instance == null) {
            synchronized (SingletonClass.class) {
                if (instance == null) {
                    instance = new SingletonClass();
                }
            }
        }
        return instance;
    }
}
