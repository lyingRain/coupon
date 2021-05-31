package com.imooc.coupon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author zzxstart
 * @date 2020/5/27 - 19:01
 */


    public class Test implements Runnable {
    private  static List<Integer> list = new ArrayList<>(Arrays.asList(1,2,3,4,5,6,7));
     private boolean change = true;
    @Override
        public void run() {
             synchronized (this) {
                 while (list.size() > 0 && change ) {
                     for (int i = 0; i <list.size(); i++) {

                         list.remove(i);
                         System.out.println(Thread.currentThread().getName() +":"+list);

                         try {
                             TimeUnit.MILLISECONDS.sleep(100);
                         } catch (InterruptedException e) {
                             e.printStackTrace();
                         }
                     }
                     change = false;
                 }
                 change = true;
             }
    }

        public static void main(String[] args) {
            Test print = new Test();
            for (int i = 0; i <list.size()/2+1; i++) {


                Thread thread1 = new Thread(print, "A");
                Thread thread2 = new Thread(print, "B");
                thread1.start();
                thread2.start();
            }
        }
}