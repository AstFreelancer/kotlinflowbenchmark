package com.astfreelancer.glossarylotr.repository

import com.astfreelancer.glossarylotr.data.dao.WordDao
import com.astfreelancer.glossarylotr.data.model.WordEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WordRepository @Inject constructor(
    private val wordDao: WordDao
) {
    suspend fun countNewWordsWithoutFlow(repNum: Int): Int =
        wordDao.countByRepNumWithoutFlow(repNum)

    suspend fun countLearnedWordsWithoutFlow(repNum: Int): Int =
        wordDao.countLearnedWordsWithoutFlow(repNum)


    fun countNewWordsFlow(): Flow<Int> =
        wordDao.countByRepNum(-1)
            .distinctUntilChanged()
            .flowOn(Dispatchers.IO)

    fun countLearnedWordsFlow(maxRep: Int): Flow<Int> =
        wordDao.countLearnedWords(maxRep)
            .distinctUntilChanged()
            .flowOn(Dispatchers.IO)

   }
