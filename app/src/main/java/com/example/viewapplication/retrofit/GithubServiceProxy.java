package com.example.viewapplication.retrofit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

import retrofit2.Call;

/**
 * @author zhanzijian
 * @description
 * @date 2022/01/20 22:13
 */
public class GithubServiceProxy implements GithubService{
//    InvocationHandler invocationHandler = new InvocationHandler() {
//        private final Platform platform = Platform.get();
//        private final Object[] emptyArgs = new Object[0];
//
//        @Override
//        public @Nullable Object invoke(Object proxy, Method method, @Nullable Object[] args)
//                  throws Throwable {
//            // If the method is a method from Object then defer to normal invocation.
//            if (method.getDeclaringClass() == Object.class) {
//                return method.invoke(this, args);
//            }
//            args = args != null ? args : emptyArgs;
//            return platform.isDefaultMethod(method)
//                    ? platform.invokeDefaultMethod(method, service, proxy, args)
//                    : loadServiceMethod(method).invoke(args);
//        }
//    };

    @NonNull
    @Override
    public Call<List<Repo>> listRepos(@Nullable String user) {
        return null;
//        Method method = GithubService.class.getDeclaredMethod("listRepos",String.class);
//        return invocationHandler.invoke(this,method,user);
    }
}
