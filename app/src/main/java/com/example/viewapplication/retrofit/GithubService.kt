package com.example.viewapplication.retrofit

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

/**
 *
 * @description
 * @author zhanzijian
 * @date 2022/01/19 22:11
 */
interface GithubService {
    @GET("users/{user}/repos")
    fun listRepos(@Path("user") user: String?): Call<List<Repo>>
}