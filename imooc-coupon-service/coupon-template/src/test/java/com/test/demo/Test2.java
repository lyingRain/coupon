package com.test.demo;




import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author zzxstart
 * @date 2020/5/30 - 16:25
 */
public class Test2 {
    public static void main(String[] args) {
        String s = "absihdsibfdsfvas";
        ArrayList list = new ArrayList();
        for (int i = 0; i <s.length() ; i++) {
            char c = s.charAt(i);
            if (s.indexOf(c,i+1)>-1){
                Character ch = new Character(c);
                if (! list.contains(ch)){
                    list.add(ch);
                }
            }
            for (int j = 0; j <list.size() ; j++) {
                System.out.println(list.get(i));
            }
        }
    }
}
