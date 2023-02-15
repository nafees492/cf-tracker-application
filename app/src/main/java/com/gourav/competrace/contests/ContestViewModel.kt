package com.gourav.competrace.contests

import android.util.Log
import androidx.lifecycle.ViewModel
import com.gourav.competrace.utils.FinishedContestFilter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ContestViewModel : ViewModel() {

    init {
        Log.d(TAG, "Initialised")
    }

    private val _currentSelection = MutableStateFlow(FinishedContestFilter.PARTICIPATED)
    val currentSelection = _currentSelection.asStateFlow()

    fun updateCurrentSelection(value: String){
        _currentSelection.update { value }
    }

    companion object {
        const val TAG = "Contest ViewModel"
    }
}