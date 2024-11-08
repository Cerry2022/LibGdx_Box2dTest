package com.cerry.game.box2dtest.Physics;
import com.badlogic.gdx.utils.Timer;
import com.cerry.game.box2dtest.tool.SharedObject;
import com.cerry.game.box2dtest.tool.log;


import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;

public class PhysicsThreadManager implements Runnable {
    public static SharedObject sharedObject;
    private final PhysicsWorld physicsWorld; // the Box2D world
    private boolean running; // the flag to control the loop
    private static boolean stepWorldRuning;
    private int showFps = 0;
    private int fpsCount = 0;
    private int newFps;
    private final Timer.Task timerTask;
    public PhysicsThreadManager(PhysicsWorld physicsWorld) {
        this.physicsWorld = physicsWorld; // create the world with gravity
        running = true; // set the flag to true
        stepWorldRuning = true;

        Timer timer = new Timer();
        timerTask = new Timer.Task() {
            @Override
            public void run() {
                showFps = (int)(fpsCount/0.5f);
                fpsCount = 0;

                OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

                // 获取程序内存占比
                long totalMemory = Runtime.getRuntime().totalMemory();
                long freeMemory = Runtime.getRuntime().freeMemory();
                long usedMemory = totalMemory - freeMemory;
                double memoryUsage = (double) usedMemory / totalMemory * 100;

                // 获取CPU占比
                double cpuUsage = osBean.getSystemCpuLoad() * 100;

                System.out.println("内存占比: " + memoryUsage + "%");
                System.out.println("CPU占比: " + cpuUsage + "%");

            }
        };
        timer.scheduleTask(timerTask, 0, 0.5f);


        sharedObject = new SharedObject(); // 创建共享对象,以便线程间数据传输
    }

    @Override
    public void run() {
        while (running) {
            long old_time  = System.nanoTime();;


            // 更新物理世界
            if(stepWorldRuning) {
                physicsWorld.stepWorld();
            }
            sharedObject.executeMethod(); // 执行共享对象的方法

            long deltaTime0 = System.nanoTime() - old_time;//物理世界
            //log.logInfo("PhysicsThread","stepWorldTime:"+deltaTime0);
            // 休眠一段时间，控制物理世界的更新速率
            try {
                if (1000f/120f > deltaTime0/1000000f) {
                    Thread.sleep((long)(1000f/120f - deltaTime0/1000000f - (newFps < 120?1:0)) );
                    //log.logInfo("PhysicsThread", String.valueOf(deltaTime0));
                }
            } catch (InterruptedException e) {
                throw new RuntimeException (e);
            }
            long deltaTime1 = System.nanoTime() - old_time;//同步画面
            //log.logInfo("PhysicsThread","sleepTime:"+ (deltaTime1 - deltaTime0));
            newFps = (int) (1000000000 / deltaTime1);
            //log.logInfo("PhysicsFps:"+ newFps);
            fpsCount ++;
        }
    }

    public void dispose() {

        log.logInfo("PhysicsThread", "------------------------------");
        physicsWorld.dispose();
        timerTask.cancel();
        running = false; // set the flag to false
    }

    public int GetPhysicsFps() {
        return showFps;
    }

    public static void setStepWorldRunning(boolean bool) {
        stepWorldRuning = bool;
    }


}