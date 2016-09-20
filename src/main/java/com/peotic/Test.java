package com.peotic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Test {
    
    public static <T> List<T> removeSame(List<T> list, Comparator<T> comparator){
        List<T> result = new ArrayList<T>();
        out:for (T t : list){
            for (T obj : result){
                if (comparator.compare(t, obj) == 0){
                    continue out;
                }
            }
            result.add(t);
        }
        return result;
    }
    
    public static void main(String[] args) {
        List<Integer> list = Arrays.asList(new Integer[]{1,2,3,1,4,2});
        List<Integer> result = removeSame(list, new Comparator<Integer>(){
            public int compare(Integer o1, Integer o2) {
                return o1 - o2;
            }
        });
        System.out.println(result.size());
    }

}
