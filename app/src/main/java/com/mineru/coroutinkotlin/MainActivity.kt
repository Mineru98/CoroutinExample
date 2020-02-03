package com.mineru.coroutinkotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn1.setOnClickListener { view ->
            CoroutinJobs.coroutineTest()
        }

        btn2.setOnClickListener { view ->
            CoroutinJobs.coroutineTest2()
        }

        btn3.setOnClickListener { view ->
            CoroutinJobs.coroutineTest3()
        }

        btn4.setOnClickListener { view ->
            CoroutinJobs.AsyncFunction().forEach { value-> CoroutinJobs.printlog("OK, Google", value) }
        }

        btn5.setOnClickListener { view ->
            runBlocking {
                CoroutinJobs.AsyncFunction2().forEach { value -> CoroutinJobs.printlog("OK, Google", value) }
            }
        }

        btn6.setOnClickListener { view ->
            runBlocking {
                // #start 1
                CoroutinJobs.printlog("Start Routin",0)
                CoroutinJobs.AsyncFunction3().collect { value -> CoroutinJobs.printlog("I'm Not Blocked(1)", value)}

                // #start 2
                launch {
                    for(k in 1..3) {
                        CoroutinJobs.printlog("I'm Not Blocked(1-1)", k)
                        delay(100L)
                    }
                }

                launch {
                    for(k in 1..3) {
                        CoroutinJobs.printlog("I'm Not Blocked(1-2)", k)
                        delay(100L)
                    }
                }
                CoroutinJobs.AsyncFunction3().collect { value -> CoroutinJobs.printlog("I'm Not Blocked(2)", value)}

                // #start 3
                CoroutinJobs.printlog("End Routin",0)
            }
        }

        btn7.setOnClickListener { view ->
            runBlocking {
                (1..3).asFlow()
                    .map { request -> CoroutinJobs.transferValue(request)}
                    .collect { value -> CoroutinJobs.printlog(value,0)}
            }
        }

        btn8.setOnClickListener { view ->
            runBlocking {
                (1..3).asFlow()
                    .transform { request ->
                        delay(1000L)
                        emit("Making request $request")
                        emit(CoroutinJobs.transferValue(request))
                    }
                    .collect { value -> CoroutinJobs.printlog(value,0) }
            }
        }

        btn9.setOnClickListener { view ->
            runBlocking {
                val sum = (1..5).asFlow()
                    .map { it * it }
                    .reduce { a, b -> a + b}
                Log.d("Mineru Sum", sum.toString())
            }
        }

        btn10.setOnClickListener { view ->
            runBlocking {
                (1..5).asFlow()
                    .filter {
                        Log.d("Mineru Filter", it.toString())
                        it % 2 == 0
                    }
                    .map {
                        Log.d("Mineru Map", it.toString())
                        "string $it"
                    }
                    .collect {
                        Log.d("Mineru Collect", it)
                    }
            }
        }
    }
}
