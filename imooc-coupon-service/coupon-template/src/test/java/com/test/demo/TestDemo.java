package com.test.demo;

import org.junit.Test;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * @author zzxstart
 * @date 2020/5/20 - 23:14
 */

public class TestDemo {
    @Test
    public void demo() {
        int a = 0;
        int c = 0;
        /*do {
            --c;
            a=a-1;
        }while (a>0);*/
        while (a>0){
            --c;
            a=a-1;
        }
        System.out.println(c);
    }
    @Test
    public void demo2() {
        Integer[] arr = {1, 23, 43, 2, 23, 53, 9};
        for (int i = 0; i <arr.length ; i++) {
            for (int j = 0; j <arr.length-i-1 ; j++) {
                if (arr[j]>arr[j+1]){
                    int temp = arr[j];
                    arr[j]=arr[j+1];
                    arr[j+1]=temp;
                }
            }
        }
        System.out.println(Arrays.toString(arr));
    }

    @Test
    public void demo1(){
        char[] c= new char[]{'a','b','c','d'};
        for (int i = 0; i <c.length ; i++) {
            System.out.println(c[i]);
            for (int j = 0; j <c.length ; j++) {
                System.out.println(""+c[i]+c[j]);
                for (int k = 0; k <c.length ; k++) {
                    System.out.println(""+c[i]+c[j]+c[k]);
                    for (int l = 0; l <c.length ; l++) {
                        System.out.println(""+c[i]+c[j]+c[k]+c[l]);

                    }
                }
            }
        }
    }
}
