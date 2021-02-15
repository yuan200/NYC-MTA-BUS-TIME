package com.wen.android.mtabuscomparison.feature.favorite.storage.repo

import com.wen.android.mtabuscomparison.feature.favorite.FavoriteStop
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {

    val favorites: Flow<List<FavoriteStop>>

    suspend fun addFavorite(stop: FavoriteStop)

}