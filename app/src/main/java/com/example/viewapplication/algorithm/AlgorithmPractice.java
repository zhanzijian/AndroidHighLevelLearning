package com.example.viewapplication.algorithm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author zhanzijian
 * @description 算法练习
 * @date 2022/03/09 00:05
 */
public class AlgorithmPractice {

    /**
     * 选择排序
     * @param a
     */
    public static void SelectionSort(int[] a){
        int length = a.length;
        for (int i = 0; i < length; i++){
            int min = i;
            // 每一轮找出最小值，移到最右边
            for (int j = i+1; j < length; j++){
                if (a[min] < a[j]){
                    min = j;
                    // 把小值往后移
                    int temp = a[min];
                    a[min] = a[j];
                    a[j] = temp;
                }
            }
        }
    }

    /**
     * 数组里是否有重复数字
     * @param nums
     * @return
     */
    public boolean containsDuplicate(int[] nums) {
        Set<Integer> set = new HashSet<>();
        for (int num : nums) {
            if (!set.add(num)) {
                continue;
            }
            return true;
        }
        return false;
    }

    /**
     * 字符串里是否有重复字符
     * @param astr
     * @return
     */
    public boolean isUnique(String astr) {
        if(astr == null || astr.isEmpty()){
            return true;
        }
        Set<Character> set = new HashSet<>();
        for (int i = 0;i < astr.length();i++){
            char c = astr.charAt(i);
            if (!set.add(c)){
                return false;
            }
        }
        return true;
    }
}
