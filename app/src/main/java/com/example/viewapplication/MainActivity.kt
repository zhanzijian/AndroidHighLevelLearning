package com.example.viewapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.viewapplication.retrofit.GithubService
import com.example.viewapplication.retrofit.Repo
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.locks.ReentrantLock

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
        val service = retrofit.create(GithubService::class.java)
        val repos: Call<List<Repo>> = service.listRepos("octocat")

        repos.enqueue(object : Callback<List<Repo>?> {
            override fun onFailure(call: Call<List<Repo>?>, t: Throwable) {
                findViewById<TextView>(R.id.textview).text = t.message
            }

            override fun onResponse(call: Call<List<Repo>?>, response: Response<List<Repo>?>) {
                findViewById<TextView>(R.id.textview).text = response.body()!![0].name
            }
        })
    }
}