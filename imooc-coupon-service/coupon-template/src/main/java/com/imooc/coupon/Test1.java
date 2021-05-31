package com.imooc.coupon;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author zzxstart
 * @date 2020/5/27 - 21:37
 */
public class Test1 implements Runnable {
    private int index = 1;
    private boolean change = true;
    private final static int MAX = 50;
    private final static Object MUTEX = new Object();

    @Override
    public void run() {
        synchronized (MUTEX) {
            while (index <= MAX && change) {
                System.out.println(Thread.currentThread().
                        getName() + "的号码是:" + (index++));
                try {
                    TimeUnit.MILLISECONDS.sleep(new Random().nextInt(1000));
                    TimeUnit.MILLISECONDS.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                change = false;
            }
            change = true;
        }
    }

    public static void main(String[] args) {
        final Test1 test = new Test1();
        for (int i = 0; i < MAX / 4 + 1; i++) {
          Thread windowThread1 = new Thread(test, "一号窗口");
          Thread windowThread2 = new Thread(test, "二号窗口");
          Thread windowThread3 = new Thread(test, "三号窗口");
          Thread windowThread4 = new Thread(test, "四号窗口");
          windowThread1.start();
          windowThread2.start();
          windowThread3.start();
          windowThread4.start();
        }
    }
}
