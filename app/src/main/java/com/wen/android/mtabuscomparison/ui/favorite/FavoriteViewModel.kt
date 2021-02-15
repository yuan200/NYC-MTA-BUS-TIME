package com.wen.android.mtabuscomparison.ui.favorite

import androidx.lifecycle.*
import com.wen.android.mtabuscomparison.feature.favorite.storage.repo.DefaultFavoriteRepository
import kotlinx.coroutines.Dispatchers
import timber.log.Timber

class FavoriteViewModel : ViewModel() {

    private val favoriteRepository = DefaultFavoriteRepository()

    private val _backdropOpened: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>().apply {
            value = false
        }
    }

    val backdropOpened: LiveData<Boolean> = _backdropOpened

    fun setBackdropOpened(isOpen: Boolean) {
        _backdropOpened.value = isOpen
    }

    val favoriteLiveData = favoriteRepository.favorites
        .asLiveData(Dispatchers.IO)

    val showEmptyFavorite: LiveData<Boolean> = MediatorLiveData<Boolean>().apply {
        addSource(favoriteLiveData) {
            Timber.i(if (it.isNullOrEmpty()) "true" else "false")
            this.value = it.isNullOrEmpty()
        }
    }

}