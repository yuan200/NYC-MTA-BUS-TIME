package com.wen.android.mtabuscomparison.feature.favorite.storage.repo

import com.wen.android.mtabuscomparison.BusApplication
import com.wen.android.mtabuscomparison.feature.favorite.FavoriteStop
import com.wen.android.mtabuscomparison.feature.stopmonitoring.BusDatabase
import kotlinx.coroutines.flow.Flow

class DefaultFavoriteRepository : FavoriteRepository {
    //todo why not just reuse Favorite
    override val favorites: Flow<List<FavoriteStop>> by lazy {
        BusDatabase.getInstance(BusApplication.instance)
            .favoriteStopDao()
            .getAll()
    }

    override suspend fun addFavorite(stop: FavoriteStop) {
        TODO("Not yet implemented")
    }
}