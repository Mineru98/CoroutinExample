package com.mineru.coroutinkotlin

import android.content.ContentUris
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.mineru.coroutinkotlin.CoroutinJobs.Companion.log
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.reflect.typeOf
import kotlin.system.measureTimeMillis

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
            CoroutinJobs.AsyncFunction()
                .forEach { value -> CoroutinJobs.printlog("OK, Google", value) }
        }

        btn5.setOnClickListener { view ->
            runBlocking {
                CoroutinJobs.AsyncFunction2()
                    .forEach { value -> CoroutinJobs.printlog("OK, Google", value) }
            }
        }

        btn6.setOnClickListener { view ->
            runBlocking {
                // #start 1
                CoroutinJobs.printlog("Start Routin", 0)
                CoroutinJobs.AsyncFunction3()
                    .collect { value -> CoroutinJobs.printlog("I'm Not Blocked(1)", value) }

                // #start 2
                launch {
                    for (k in 1..3) {
                        CoroutinJobs.printlog("I'm Not Blocked(1-1)", k)
                        delay(100L)
                    }
                }

                launch {
                    for (k in 1..3) {
                        CoroutinJobs.printlog("I'm Not Blocked(1-2)", k)
                        delay(100L)
                    }
                }
                CoroutinJobs.AsyncFunction3()
                    .collect { value -> CoroutinJobs.printlog("I'm Not Blocked(2)", value) }

                // #start 3
                CoroutinJobs.printlog("End Routin", 0)
            }
        }

        btn7.setOnClickListener { view ->
            runBlocking {
                (1..3).asFlow()
                    .map { request -> CoroutinJobs.transferValue(request) }
                    .collect { value -> CoroutinJobs.printlog(value, 0) }
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
                    .collect { value -> CoroutinJobs.printlog(value, 0) }
            }
        }

        btn9.setOnClickListener { view ->
            runBlocking {
                val sum = (1..5).asFlow()
                    .map { it * it }
                    .reduce { a, b -> a + b }
                Log.i("Mineru Sum", sum.toString())
            }
        }

        btn10.setOnClickListener { view ->
            runBlocking {
                (1..5).asFlow()
                    .filter {
                        Log.i("Mineru Filter", it.toString())
                        it % 2 == 0
                    }
                    .map {
                        Log.i("Mineru Map", it.toString())
                        "string $it"
                    }
                    .collect {
                        Log.i("Mineru Collect", it)
                    }
            }
        }

        btn11.setOnClickListener { view ->
            runBlocking {
                CoroutinJobs.namedRoutine()
                    .collect { value -> log("Collected $value") }
            }
        }

        btn12.setOnClickListener { view ->
            runBlocking {
                Log.i("Mineru", "Main Start")
                CoroutinJobs.switching().collect { log("Collected $it") }
            }
        }

        btn13.setOnClickListener { view ->
            // old way
            // 생산시간 * 생산 갯수 + 소비시간 * 소비 갯수
            runBlocking {
                val time = measureTimeMillis {
                    CoroutinJobs.produce_consume().collect { value ->
                        delay(3000L)
                        Log.i("Mineru consume", value.toString())
                    }
                }
                Log.i("Mineru", "Collected in $time ms")
            }

        }

        btn14.setOnClickListener { view ->
            // new way - processing piplining
            // 데이터 생산을 multi 쓰레딩 방식처럼 동작한다.
            // 생산시간 * 1 + 소비시간 * 소비 갯수
            runBlocking {
                val time = measureTimeMillis {
                    CoroutinJobs.produce_consume()
                        .buffer()
                        .collect { value ->
                            delay(3000L)
                            Log.i("Mineru consume", value.toString())
                        }
                }
                Log.i("Mineru", "Collected in $time ms")
            }
        }

        btn15.setOnClickListener { view ->
            //다운로드같이 상태를 업데이트 하는경우 background에서 다운로드를 하고 UI thread로 progress를 넘겨줄때 사용하면 유용해 보입니다.
            runBlocking {
                val time = measureTimeMillis {
                    CoroutinJobs.middleProcess()
                        .conflate()
                        .collect {
                            try {
                                delay(300L)
                                Log.i("Mineru", "Done $it")
                            } catch (ce: CancellationException) {
                                Log.i("Mineru", "Cancelled $it")
                            }
                        }
                }
                Log.i("Mineru", "Collected in $time ms")
            }
        }

        btn16.setOnClickListener {
            //collectLatest() operator를 사용하면 collector 동작중 새로운 값이 emit되어 전달받으면
            // 기존 collect() 동작을 취소하고 새로운 값을 위한 collector를 재시작 시킵니다.
            // 이러한 방식으로 동작하는 경우는 언제가 있을까?

            runBlocking {
                Log.i("Mineru", "Main start")
                val time = measureTimeMillis {
                    CoroutinJobs.latestEmit().collectLatest {
                        try {
                            Log.i("Mineru", "Collect $it")
                            delay(2000L)
                            Log.i("Mineru", "Done $it")
                        } catch (ce: CancellationException) {
                            Log.i("Mineru", "Cancelled $it")
                        }
                    }
                }
                Log.i("Mineru", "Collected in $time ms")
                Log.i("Mineru", "Main End")
            }
        }

        btn17.setOnClickListener {
            // 두개의 Flow를 병합하는 예시
            runBlocking {
                val nums = (1..3).asFlow()
                val strs = flowOf("One", "two", "three")
                nums.zip(strs) { a, b -> "$a -> $b" }.collect { Log.i("Mineru", "$it") }
            }
        }

        btn18.setOnClickListener {
            // 두개의 Flow를 병합을 할때 서로 처리하는 시간이 다르면
            // 최종적으로 처리되는 값의 시간에 맞춰서 최종적으로 처리하는 방식
            runBlocking {
                println("main start!")
                val nums = (1..3).asFlow().onEach { delay(300L) }
                val strs = flowOf("One", "two", "three").onEach { delay(400L) }
                val startTime = System.currentTimeMillis()
                nums.zip(strs) { a, b -> "$a -> $b" }
                    .collect {
                        println("$it at ${System.currentTimeMillis() - startTime} ms from start")
                    }
                println("main end")
            }

            // combine은 두개의 값이 모두 존재해야만 동작한다. 따라서 처음 nums가 1을 emit 하여도
            // 아무런 처리가 되지 않게 된다.
            // 처음 생성되는 작업이 되지 않고 나머지 작업부터는 각각이 새로운 값을 생성하게 되면 combine이 호출 된다.
            runBlocking {
                println("main2 start!")
                val nums = (1..3).asFlow().onEach {
                    delay(100L)
                    println("nums emit $it")
                }
                val strs = flowOf("One", "two", "three", "four").onEach { delay(400L)
                    println("strs emit $it")
                }
                val startTime = System.currentTimeMillis()
                nums.combine(strs) { a, b -> "$a -> $b" }
                    .collect {
                        println("$it at ${System.currentTimeMillis() - startTime} ms from start")
                    }
                println("main2 end")
            }
        }

        btn19.setOnClickListener {
            // flatMapConcat이 감싸고 있는 내부 flow가 완전히 처리가 되어야지만 다음 flow를 처리할 수 있게 하는 방안
            // 순차적으로 동작해야만 하는 작업일 경우 반드시 사용해야함.

            println("main Start")
            var startTime = System.currentTimeMillis()
            runBlocking {
                (1..3).asFlow().onEach {delay(100L)}
                    .flatMapConcat { CoroutinJobs.requestFlow(it) }
                    .collect {
                        println("$it at ${System.currentTimeMillis() - startTime} ms from start")
                    }
            }
            println("main end")

            // 각각의 flow를 빠르게 생산하고 처리는 각자 하되 최신 상태를 가져오면서 처리해야하는 경우에 사용해야함.
            println("main2 Start")
            startTime = System.currentTimeMillis()
            runBlocking {
                (1..3).asFlow().onEach {delay(1000L)}
                    .flatMapMerge { CoroutinJobs.requestFlow(it) }
                    .collect {
                        println("$it at ${System.currentTimeMillis() - startTime} ms from start")
                    }
            }
            println("main2 end")
        }
    }
}
