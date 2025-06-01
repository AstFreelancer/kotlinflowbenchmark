package com.astfreelancer.glossarylotr.repository

import androidx.room.Transaction
import com.astfreelancer.glossarylotr.SettingsManager
import com.astfreelancer.glossarylotr.data.dao.ChapterDao
import com.astfreelancer.glossarylotr.data.model.ChapterEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChapterRepository @Inject constructor(
    private val chapterDao: ChapterDao,
    private val settings: SettingsManager
) {
    suspend fun getChaptersWithoutFlow(): List<ChapterUiModel> =
        chapterDao.getChaptersWithoutFlow(settings.maxRep)

    fun getChapters(): Flow<List<ChapterUiModel>> =
        chapterDao.getChapters(settings.maxRep)
}

data class ChapterUiModel(
    val id: Int,
    val name: String,
    val percent: Int,      // 0–100 для bar-progress
    val isLearned: Boolean,
    val newWords: Int,   // число слов с nextReviewDate = null
    val nNeverSeen: Int, // число еще не показанных слов
    val nWords: Int
)