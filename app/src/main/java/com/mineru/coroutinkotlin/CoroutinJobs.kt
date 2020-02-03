package com.mineru.coroutinkotlin

import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class CoroutinJobs {
    companion object {
        fun coroutineTest() = runBlocking {
            launch {
                jobs(2000L, 2)
            }

            coroutineScope {
                launch {
                    jobs(3000L, 3)
                }
                jobs(1000L, 1)
            }
            Log.d("Mineru", "Task From runBlocking#4")
        }

        fun coroutineTest2() {
            runBlocking {
                val jobs = List(10) {
                    launch {
                        delay(1000L)
                        Log.d("Mineru:", "Start Block")
                    }
                }
                jobs.forEach { it.join() }
                Log.d("Mineru:", "End RunBlock")
            }

            Log.d("Mineru:", "End Function")

            GlobalScope.launch {
                delay(1000L)
                Log.d("Mineru: ", "World!")
            }

            Log.d("Mineru: ", "Hello,")
        }

        fun coroutineTest3() = runBlocking {
            repeat(100_000) {
                jobs(1L, it)
            }
        }

        fun AsyncFunction(): Sequence<Int> = sequence {
            for(i in 1..3) {
                Thread.sleep(1000L)
                yield(i)
            }
        }

        suspend fun AsyncFunction2(): List<Int> {
            delay(1000L)
            return listOf(1,2,3)
        }

        fun AsyncFunction3(): Flow<Int> = flow {
            for(i in 11..13) {
                delay(100L)
                emit(i)
            }
        }

        suspend fun transferValue(request: Int): String {
            delay(1000L)
            return "response $request"
        }

        suspend fun jobs(time: Long?, count: Int) {
            delay(time!!)
            Log.d("Mineru", "Task From runBlocking#$count")
        }

        fun printlog(str: String?, count: Int) {
            Log.d("Mineru", str!!+"$count")
        }
    }
}