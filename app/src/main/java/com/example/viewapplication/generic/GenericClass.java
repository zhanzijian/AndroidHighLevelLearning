package com.example.viewapplication.generic;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhanzijian
 * @description 泛型测试
 * @date 2022/04/13 23:37
 */
public class GenericClass {
    public void main() {
        List<? extends Fruit> fruitList = new ArrayList<Apple>(); // 上界 协变
        List<? super Apple> appleList = new ArrayList<Fruit>(); // 下界 逆变
    }
}
