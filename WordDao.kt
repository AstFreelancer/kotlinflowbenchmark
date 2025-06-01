package com.astfreelancer.glossarylotr.data.dao

import androidx.room.*
import com.astfreelancer.glossarylotr.data.model.WordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    companion object {
        private const val MS_PER_DAY = 86_400_000L
    }
    @Query("SELECT COUNT(*) FROM words WHERE repNum = :repNum")
    suspend fun countByRepNumWithoutFlow(repNum: Int): Int

    @Query("SELECT COUNT(*) FROM words WHERE repNum >= :repNum")
    suspend fun countLearnedWordsWithoutFlow(repNum: Int): Int

    @Query("SELECT COUNT(*) FROM words WHERE repNum = :repNum")
    fun countByRepNum(repNum: Int): Flow<Int>

    @Query("SELECT COUNT(*) FROM words WHERE repNum >= :repNum")
    fun countLearnedWords(repNum: Int): Flow<Int>

}
