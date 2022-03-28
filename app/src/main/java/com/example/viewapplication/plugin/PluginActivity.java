package com.example.viewapplication.plugin;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.viewapplication.R;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

public class PluginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plugin);

        // 插件化主要是为了实现动态加载或者动态部署的，可以减少apk体积
        // 插件化简单实现
        File apkFile = new File(getCacheDir() + "/plugin.apk");
        try (Source source = Okio.source(getAssets().open("apk/plugin-debug.apk"));
             BufferedSink buffer = Okio.buffer(Okio.sink(apkFile))){
            buffer.writeAll(source);
        } catch (IOException e) {
            e.printStackTrace();
        }
        DexClassLoader dexClassLoader = new DexClassLoader(apkFile.getPath(),
                getCacheDir().getPath(),
                null,
                null);

        try {
            Class<?> utilsClass = dexClassLoader.loadClass("com.example.plugin.Utils");
//        Class<Utils> utilsClass = Utils.class;
            Constructor<?> utilsConstructor = utilsClass.getDeclaredConstructors()[0];
            utilsConstructor.setAccessible(true);
            Object instance = utilsConstructor.newInstance();
            Method helloPlugin = utilsClass.getDeclaredMethod("HelloPlugin");
            helloPlugin.setAccessible(true);
            helloPlugin.invoke(instance);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}