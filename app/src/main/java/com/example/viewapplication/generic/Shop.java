package com.example.viewapplication.generic;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhanzijian
 * @description
 * @date 2022/04/13 23:35
 */
public interface Shop<T extends String> {
    T buy();
    void refund(T item);
    List<String> list = new ArrayList<>();
}
