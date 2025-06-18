package com.astfreelancer.benchmark

import android.content.Context
import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.room.*
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@Entity(tableName = "words")
data class WordEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val repNum: Int
)

@Dao
interface WordDao {
    @Insert
    suspend fun insert(word: WordEntity)

    @Query("DELETE FROM words")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM words WHERE repNum = :repNum")
    suspend fun countByRepNumWithoutFlow(repNum: Int): Int

    @Query("SELECT COUNT(*) FROM words WHERE repNum = :repNum")
    fun countByRepNum(repNum: Int): Flow<Int>
}

@Database(entities = [WordEntity::class], version = 1)
abstract class WordDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
}

class WordRepository(private val dao: WordDao) {
    suspend fun insertWord(word: WordEntity) = dao.insert(word)
    suspend fun countByRepNumWithoutFlow(repNum: Int) = dao.countByRepNumWithoutFlow(repNum)
    fun countByRepNum(repNum: Int): Flow<Int> = dao.countByRepNum(repNum)
}

@RunWith(AndroidJUnit4::class)
class FlowVsSuspendBenchmark {

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    private lateinit var db: WordDatabase
    private lateinit var repo: WordRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, WordDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repo = WordRepository(db.wordDao())
    }

    @Test
    fun benchmarkSuspend() = benchmarkRule.measureRepeated {
        runBlocking {
            db.wordDao().clearAll()
            val N = 10
            val results = mutableListOf<Int>()

            repeat(N) {
                repo.insertWord(WordEntity(repNum = 5))
                val count = repo.countByRepNumWithoutFlow(5)
                results.add(count)
            }
        }
    }

    @Test
    fun benchmarkFlow() = benchmarkRule.measureRepeated {
        runBlocking {
            db.wordDao().clearAll()
            val N = 10
            val results = mutableListOf<Int>()
            val emissionChannel = Channel<Int>(Channel.UNLIMITED)

            val collectJob = launch {
                repo.countByRepNum(5).collect { count ->
                    emissionChannel.trySend(count)
                }
            }

            emissionChannel.receive()

            repeat(N) {
                repo.insertWord(WordEntity(repNum = 5))
                val count = emissionChannel.receive()
                results.add(count)
            }

            collectJob.cancel()
            emissionChannel.close()
        }
    }
}