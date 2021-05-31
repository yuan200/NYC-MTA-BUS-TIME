package com.wen.android.mtabuscomparison.ui.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wen.android.mtabuscomparison.BaseApp
import com.wen.android.mtabuscomparison.feature.favorite.FavoriteStop
import com.wen.android.mtabuscomparison.feature.stopmonitoring.BusDatabase
import java.util.*

class SaveFavoriteViewModel: ViewModel() {

    private val _stopId = MutableLiveData("")
    val stopId: LiveData<String> = _stopId

    fun onStopIdChange(newStopId: String) {
        _stopId.value = newStopId
    }


    private val _description = MutableLiveData("")
    val description: MutableLiveData<String> = _description

    fun onDescriptionChange(newDescription: String) {
        _description.value = newDescription
    }

    private val _saved = MutableLiveData(false)
    val isSaved: LiveData<Boolean> = _saved

    fun onSaveFavorite() {
        BusDatabase.getInstance(BaseApp.instance).favoriteStopDao().insert(
            FavoriteStop(
                _stopId.value!!,
                null,
                null,
                null,
                null,
                description.value,
                Date()
            )
        )
        _saved.value = true
    }
}