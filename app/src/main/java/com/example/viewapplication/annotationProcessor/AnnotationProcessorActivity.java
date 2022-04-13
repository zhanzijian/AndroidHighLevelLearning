package com.example.viewapplication.annotationProcessor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.viewapplication.BindView;
import com.example.viewapplication.R;
/**
 * @description 注解处理器
 * @author zhanzijian
 * @date 2022/4/11 14:38
 */
public class AnnotationProcessorActivity extends AppCompatActivity {
    @BindView(R.id.textView) TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annotation_processor);
        InnerBinding.bind(this);
        textView.setText("萝卜哥");
    }
}