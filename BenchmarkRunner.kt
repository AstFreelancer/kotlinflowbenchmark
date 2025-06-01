package com.astfreelancer.glossarylotr

import android.content.Context
import com.astfreelancer.glossarylotr.repository.ChapterRepository
import com.astfreelancer.glossarylotr.repository.WordRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.system.measureTimeMillis

object BenchmarkLogger {
    private lateinit var logFile: File

    fun init(context: Context) {
        val logDir = File(context.filesDir, "logs")
        if (!logDir.exists()) logDir.mkdirs()

        logFile = File(logDir, "benchmark_log.txt")
    }

    fun log(message: String) {
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            .format(Date())
        val fullMessage = "[$timestamp] $message\n"

        try {
            FileWriter(logFile, true).use {
                it.append(fullMessage)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

class BenchmarkRunner(
    private val chapterRepo: ChapterRepository,
    private val wordRepo: WordRepository,
    private val settings: SettingsManager
) {
    data class Result(
        val metric: String,      // "learnedWords" | "newWords" | "chaptersList"
        val approach: String,    // "flow" или "traditional"
        val timeMs: Long
    )

    suspend fun runOnce(): List<Result> {
        val maxRep = settings.maxRep
        val results = mutableListOf<Result>()

        val tFlowLearned = measureTimeMillis {
            wordRepo.countLearnedWordsFlow(maxRep).first()
        }
        BenchmarkLogger.log("learnedWords | flow        | ${tFlowLearned}ms")
        results += Result("learnedWords", "flow", tFlowLearned)

        val tTradLearned = measureTimeMillis {
            wordRepo.countLearnedWordsWithoutFlow(maxRep)
        }
        BenchmarkLogger.log("learnedWords | traditional | ${tTradLearned}ms")
        results += Result("learnedWords", "traditional", tTradLearned)

        val tFlowNew = measureTimeMillis {
            wordRepo.countNewWordsFlow().first()
        }
        BenchmarkLogger.log("newWords     | flow        | ${tFlowNew}ms")
        results += Result("newWords", "flow", tFlowNew)

        val tTradNew = measureTimeMillis {
            wordRepo.countNewWordsWithoutFlow(maxRep)
        }
        BenchmarkLogger.log("newWords     | traditional | ${tTradNew}ms")
        results += Result("newWords", "traditional", tTradNew)

        val tFlowChapters = measureTimeMillis {
            chapterRepo.getChapters().first()
        }
        BenchmarkLogger.log("chaptersList | flow        | ${tFlowChapters}ms")
        results += Result("chaptersList", "flow", tFlowChapters)

        val tTradChapters = measureTimeMillis {
            chapterRepo.getChaptersWithoutFlow()
        }
        BenchmarkLogger.log("chaptersList | traditional | ${tTradChapters}ms")
        results += Result("chaptersList", "traditional", tTradChapters)

        return results
    }

    suspend fun runFull(
        iterations: Int = 50,
        delayBetweenMs: Long = 50L
    ): List<Result> {
        val all = mutableListOf<Result>()
        repeat(iterations) {
            all += runOnce()
            delay(delayBetweenMs)
        }
        return all
    }
}
