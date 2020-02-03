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
            Log.i("Mineru", "Task From runBlocking#4")
        }

        fun coroutineTest2() {
            runBlocking {
                val jobs = List(10) {
                    launch {
                        delay(1000L)
                        Log.i("Mineru:", "Start Block")
                    }
                }
                jobs.forEach { it.join() }
                Log.i("Mineru:", "End RunBlock")
            }

            Log.i("Mineru:", "End Function")

            GlobalScope.launch {
                delay(1000L)
                Log.i("Mineru: ", "World!")
            }

            Log.i("Mineru: ", "Hello,")
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

        fun namedRoutine(): Flow<Int> = flow {
            log("Started named Routin flow")
            for (i in 1..3)
                emit(i)
        }

        fun switching(): Flow<Int> = flow {
            // main 쓰레드가 아닌 디스패처 워커 쓰레드로 실행이 된다.
            // main 쓰레드는 UI 작업을 하고 데이터 통신을 위한 코드를
            // 디스패처 워커 쓰레드로 작업을 해주면 문제 없이 될것 같다.
            for (i in 1..3) {
                Thread.sleep(100)
                log("Emitting $i")
                emit(i)
            }
        }.flowOn(Dispatchers.Default)

        fun produce_consume(): Flow<Int> = flow {
            for(i in 1..3) {
                delay(2000L)
                Log.i("Mineru Emit", i.toString())
                emit(i)
            }
        }

        fun middleProcess(): Flow<Int> = flow {
            for(i in 1..3) {
                delay(100L)
                emit(i)
                Log.i("Mineru", "emit $i")
            }
        }

        fun latestEmit(): Flow<Int> = flow {
            for(i in 1..3) {
                delay(1000L)
                emit(i)
            }
        }

        fun requestFlow(i: Int): Flow<String> = flow {
            emit("$i: First")
            delay(2000L)
            emit("$i: Second")
        }

        suspend fun transferValue(request: Int): String {
            delay(1000L)
            return "response $request"
        }

        suspend fun jobs(time: Long?, count: Int) {
            delay(time!!)
            Log.i("Mineru", "Task From runBlocking#$count")
        }

        fun printlog(str: String?, count: Int) {
            Log.i("Mineru", str!!+"$count")
        }

        fun log(msg: String) = println("[${Thread.currentThread().name}] $msg")
    }
}