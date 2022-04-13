package com.example.viewapplication.annotationProcessor;

import android.app.Activity;

import com.example.viewapplication.BindView;
import com.example.viewapplication.R;

import java.lang.reflect.Field;

/**
 * @author zhanzijian
 * @description
 * @date 2022/04/07 17:56
 */
public class InnerBinding {
    public static void bind(Activity activity){
//        activity.textView = activity.findViewById(R.id.textView);
        for (Field field : activity.getClass().getDeclaredFields()) {
            final BindView bindView = field.getAnnotation(BindView.class);
            if (bindView != null){
                try {
                    field.set(activity,activity.findViewById(bindView.value()));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
