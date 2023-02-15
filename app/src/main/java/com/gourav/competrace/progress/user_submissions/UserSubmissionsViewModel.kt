package com.gourav.competrace.progress.user_submissions

import androidx.lifecycle.ViewModel
import com.gourav.competrace.utils.UserSubmissionFilter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class UserSubmissionsViewModel: ViewModel() {

    private val _currentSelection = MutableStateFlow(UserSubmissionFilter.ALL)
    val currentSelection = _currentSelection.asStateFlow()

    fun updateCurrentSelection(value: String){
        _currentSelection.update { value }
    }

//    private val _searchQuery = MutableStateFlow("")
//    val searchQuery = _searchQuery.asStateFlow()
//
//    fun updateSearchQuery(value: String){
//        _searchQuery.update { value }
//    }
}