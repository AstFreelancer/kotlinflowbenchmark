package com.astfreelancer.glossarylotr.data.dao

import androidx.room.*
import com.astfreelancer.glossarylotr.data.model.ChapterEntity
import com.astfreelancer.glossarylotr.repository.ChapterUiModel
import kotlinx.coroutines.flow.Flow

@Dao
interface ChapterDao {
     @Query("""
  SELECT
    c.id                                               AS id,
    c.name                                             AS name,
    CASE WHEN COUNT(e.idWord)=0 THEN 0
         ELSE SUM(CASE WHEN w.repNum >= :mCycles THEN 1 ELSE 0 END)*100/COUNT(e.idWord)
    END                                                AS percent,
    CASE WHEN SUM(CASE WHEN w.repNum >= :mCycles THEN 1 ELSE 0 END) >= COUNT(e.idWord)
         THEN 1 ELSE 0
    END                                                AS isLearned,
    (COUNT(e.idWord) - SUM(CASE WHEN w.repNum >= :mCycles THEN 1 ELSE 0 END))
                                                       AS newWords,
    SUM(CASE WHEN w.repNum < 0 THEN 1 ELSE 0 END)      AS nNeverSeen,
    c.nWords AS nWords
  FROM chapters AS c
  LEFT JOIN entries AS e ON c.id = e.idChapter
  LEFT JOIN words   AS w ON e.idWord = w.id
  GROUP BY c.id
""")
        fun getChapters(mCycles: Int): Flow<List<ChapterUiModel>>

    @Query("""
  SELECT
    c.id                                               AS id,
    c.name                                             AS name,
    CASE WHEN COUNT(e.idWord)=0 THEN 0
         ELSE SUM(CASE WHEN w.repNum >= :mCycles THEN 1 ELSE 0 END)*100/COUNT(e.idWord)
    END                                                AS percent,
    CASE WHEN SUM(CASE WHEN w.repNum >= :mCycles THEN 1 ELSE 0 END) >= COUNT(e.idWord)
         THEN 1 ELSE 0
    END                                                AS isLearned,
    (COUNT(e.idWord) - SUM(CASE WHEN w.repNum >= :mCycles THEN 1 ELSE 0 END))
                                                       AS newWords,
    SUM(CASE WHEN w.repNum < 0 THEN 1 ELSE 0 END)      AS nNeverSeen,
    c.nWords AS nWords
  FROM chapters AS c
  LEFT JOIN entries AS e ON c.id = e.idChapter
  LEFT JOIN words   AS w ON e.idWord = w.id
  GROUP BY c.id
""")
    suspend fun getChaptersWithoutFlow(mCycles: Int): List<ChapterUiModel>

}
