package com.cerry.game.box2dtest.tool;

// 定义一个共享对象，用于存储和执行方法
public class SharedObject {
    // 一个标志位，表示是否有方法需要执行
    private boolean hasMethod = false;
    // 一个同步方法，用于在第二个线程中添加方法
    public synchronized void addMethod(Runnable method) {
        // 如果已经有方法了，就等待
        while (hasMethod) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // 没有方法了，就添加方法，并通知第一个线程
        System.out.println("Adding method...");
        this.method = method;
        hasMethod = true;
        notify();
    }
    // 一个同步方法，用于在第一个线程中执行方法
    public synchronized void executeMethod() {
        // 如果没有方法了，就等待
        if (hasMethod) {
            System.out.println("Executing method...");
            this.method.run();
            hasMethod = false;
            notify();
        }
    }
    // 一个私有变量，用于存储要执行的方法
    private Runnable method;
}// 定义一个共享对象，用于存储和执行方法