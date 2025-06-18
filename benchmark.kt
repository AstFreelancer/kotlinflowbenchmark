package benchmark

import kotlinx.benchmark.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

// FlowVsSuspendBenchmark.flowApproach     thrpt    5  833879,277 ▒ 1050031,622  ops/s
// FlowVsSuspendBenchmark.suspendApproach  thrpt    5  999429,077 ▒ 1250484,983  ops/s
@State(Scope.Benchmark)
open class FlowVsSuspendBenchmark {

    private var counter = 0

    @Setup
    fun setup() {
        counter = 0
    }

    private suspend fun incrementAndGet(): Int {
        counter++
        return counter
    }

    private fun incrementAndGetFlow(): Flow<Int> = flow {
        val N = 100
        repeat(N) {
            counter++
            emit(counter)
        }
    }

    @Benchmark
    fun suspendApproach(): Int = runBlocking {
        val N = 100
        var result = 0

        repeat(N) {
            result = incrementAndGet()
        }

        result
    }

    @Benchmark
    fun flowApproach(): Int = runBlocking {
        var result = 0

        incrementAndGetFlow().collect { value ->
            result = value
        }

        result
    }
}