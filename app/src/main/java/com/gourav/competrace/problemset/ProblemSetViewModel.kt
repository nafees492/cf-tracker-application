package com.gourav.competrace.problemset

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ProblemSetViewModel : ViewModel() {
    init {
        Log.d(TAG, "Initialised")
    }

    private val _startRatingValue = MutableStateFlow(800)
    var startRatingValue =_startRatingValue.asStateFlow()

    private val _endRatingValue = MutableStateFlow(3500)
    var endRatingValue =_endRatingValue.asStateFlow()

    fun updateStartAndEnd(start: Int, end: Int){
        _startRatingValue.update { start }
        _endRatingValue.update { end }
    }

//    private val _searchQuery = MutableStateFlow("")
//    val searchQuery = _searchQuery.asStateFlow()
//
//    fun updateSearchQuery(value: String){
//        _searchQuery.update { value }
//    }

    companion object {
        const val TAG = "Problem Set ViewModel"
    }
}