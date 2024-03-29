package com.example.viewapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.test.espresso.core.internal.deps.guava.util.concurrent.ThreadFactoryBuilder
import com.example.viewapplication.databinding.ActivityMainBinding
import com.example.viewapplication.retrofit.GithubService
import com.example.viewapplication.retrofit.Repo
import io.reactivex.rxjava3.core.SingleObserver
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Function
import io.reactivex.rxjava3.internal.operators.single.SingleJust
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.internal.notify
import okhttp3.internal.threadFactory
import okhttp3.internal.toHexString
import okhttp3.internal.wait
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.ThreadFactory
import java.util.concurrent.TimeUnit

private const val COMMA = "."
private const val COORDINATE_MIN_SCALE = 6
private const val COORDINATE_MAX_SCALE = 18

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

//        val namedThreadFactory: ThreadFactory = threadFactory("gl-pool-%d",false)
//        val mScheduledExecutorService = ScheduledThreadPoolExecutor(1, namedThreadFactory)
//        var scheduledFuture = mScheduledExecutorService.scheduleWithFixedDelay(
//            Runnable {
//                Log.d("MainActivity", "scheduleWithFixedDelay: ")
//            },
//            1,
//            1,
//            TimeUnit.SECONDS
//        )

        binding.apply {
            esPath.setOnClickListener {
                startActivity(Intent(this@MainActivity, EsPathActivity::class.java))
            }
            acCouplePath.setOnClickListener {
                startActivity(Intent(this@MainActivity, AcCoupleEsAnimationActivity::class.java))
            }
//            pauseService.setOnClickListener {
//                scheduledFuture.cancel(true)
//            }
//            continueService.setOnClickListener {
//                scheduledFuture = mScheduledExecutorService.scheduleWithFixedDelay(
//                    Runnable {
//                        Log.d("MainActivity", "scheduleWithFixedDelay: ")
//                    },
//                    1,
//                    1,
//                    TimeUnit.SECONDS
//                )
//            }
        }


//        binding.seekBar.isEnabled = false
//        binding.seekBar.apply {
//            viewTreeObserver.addOnGlobalLayoutListener {
//                setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
//                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
////                        binding.image.translationX = (progress / 100f) * (width - thumbOffset)
//                        binding.image.translationX = (progress / 100f) * (250.dp)
//                    }
//
//                    override fun onStartTrackingTouch(seekBar: SeekBar?) {
//                        binding.image.translationX = 0f
//                    }
//
//                    override fun onStopTrackingTouch(seekBar: SeekBar?) {
//                        binding.image.translationX = 0f
//                        seekBar?.progress = 0
//                    }
//                })
//            }
//        }


    }

    private fun glideSourceAnalysis() {
//        Glide.with(this)
//            .load(R.mipmap.ic_launcher_round)
//            .placeholder(R.mipmap.ic_launcher)
//            .error(R.mipmap.ic_launcher)
//            .into(findViewById(R.id.imageView))
    }

    /**
     * Rxjava source analysis
     */
    private fun rxjavaSourceAnalysis() {
        val singleJust = SingleJust(1)
        val singleString = singleJust.map(object : Function<Int, String> {
            override fun apply(t: Int): String {
                return t.toString()
            }
        })
        singleString.subscribe(object : SingleObserver<String> {
            override fun onSubscribe(d: Disposable) {
                TODO("Not yet implemented")
            }

            override fun onSuccess(t: String) {
                TODO("Not yet implemented")
            }

            override fun onError(e: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    /**
     * Okhttp source analysis
     */
    private fun okHttpSourceAnalysis() {
        val url = "https://api.github.com/users/rengwuxian/repos"
        val hostname = "api.github.com"

        val client = OkHttpClient.Builder()
            .build()
        val request: Request = Request.Builder()
            .url(url)
            .build()
        client.newCall(request)
            .enqueue(object : okhttp3.Callback {
                override fun onFailure(call: okhttp3.Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                    println("Response status code: ${response.code}")
                }
            })
    }

    /**
     * Retrofit source analysis
     */
    private fun retrofitSourceAnalysis() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
        val service = GithubService::class.java
        val githubService = retrofit.create(service)
        val repos: Call<List<Repo>> = githubService.listRepos("octocat")
        repos.enqueue(object : Callback<List<Repo>?> {
            override fun onFailure(call: Call<List<Repo>?>, t: Throwable) {
//                findViewById<TextView>(R.id.textview).text = t.message
            }

            override fun onResponse(call: Call<List<Repo>?>, response: Response<List<Repo>?>) {
//                findViewById<TextView>(R.id.show).text = response.body()!![0].name
            }
        })


//        Proxy.newProxyInstance(
//            service.classLoader, arrayOf<Class<*>>(service),
//            object : InvocationHandler {
//                private val platform = Platform.get()
//                private val emptyArgs = arrayOfNulls<Any>(0)
//
//                @Throws(Throwable::class)
//                override fun invoke(proxy: Any, method: Method, args: Array<Any>?): Any? {
//                    // If the method is a method from Object then defer to normal invocation.
//                    var args = args
//                    if (method.declaringClass == Any::class.java) {
//                        return method.invoke(this, *args)
//                    }
//                    args = args ?: emptyArgs
//                    return if (platform.isDefaultMethod(method)) platform.invokeDefaultMethod(
//                        method,
//                        service,
//                        proxy,
//                        *args
//                    ) else loadServiceMethod(method).invoke(args)
//                }
//            })

    }

    /**
     * Sub coordinate if not standard
     * 截取坐标字符串，如果小数点后少于6位，补0，大于18位，切掉
     * @return
     */
    fun String.subCoordinateIfNotStandard(): String {
        //没有小数点，自动补6位
        if (isNullOrEmpty()) {
            return this
        }
        if (!contains(COMMA)) {
            return this + COMMA + "000000"
        }

        val commaIndex = indexOf(".")
        val lengthAfterComma = length - commaIndex - 1
        //小数点后小于6位的自动补0至6位
        if (lengthAfterComma < COORDINATE_MIN_SCALE) {
            val standardBuilder = StringBuilder(this)
            repeat(COORDINATE_MIN_SCALE - lengthAfterComma) {
                standardBuilder.append("0")
            }
            return standardBuilder.toString()
        }
        //小数点后大于18位的，只保留18位小数点
        if (lengthAfterComma > COORDINATE_MAX_SCALE) {
            val totalLength = commaIndex + COORDINATE_MAX_SCALE
            return substring(0, totalLength)
        }
        return this
    }
}