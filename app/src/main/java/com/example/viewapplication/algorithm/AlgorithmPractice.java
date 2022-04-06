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
     *
     * @param a
     */
    public static void SelectionSort(int[] a) {
        int length = a.length;
        for (int i = 0; i < length; i++) {
            int min = i;
            // 每一轮找出最小值，移到最右边
            for (int j = i + 1; j < length; j++) {
                if (a[min] < a[j]) {
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
     *
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
     *
     * @param astr
     * @return
     */
    public boolean isUnique(String astr) {
        if (astr == null || astr.isEmpty()) {
            return true;
        }
        Set<Character> set = new HashSet<>();
        for (int i = 0; i < astr.length(); i++) {
            char c = astr.charAt(i);
            if (!set.add(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 给你一个整数 x ，如果 x 是一个回文整数，返回 true ；否则，返回 false 。
     * <p>
     * 回文数是指正序（从左向右）和倒序（从右向左）读都是一样的整数。
     * <p>
     * 例如，121 是回文，而 123 不是。
     *
     * @param x
     * @return
     */
    public boolean isPalindrome(int x) {
        // 先需要排除负数
        if (x < 0) {
            return false;
        }
        // 再排除个位数是0的情况，这种情况只能0才符合条件
        if (x % 10 == 0 && x != 0){
            return false;
        }
        int reversedX = 0;
        while (x > reversedX) {
            reversedX = reversedX * 10 + x / 10;
            x = x / 10;
        }
        // 当数字长度为奇数时，我们可以通过 revertedNumber/10 去除处于中位的数字。
        // 例如，当输入为 12321 时，在 while 循环的末尾我们可以得到 x = 12，revertedNumber = 123，
        // 由于处于中位的数字不影响回文（它总是与自己相等），所以我们可以简单地将其去除。
        return reversedX == x || reversedX / 10 == x;
    }
}
