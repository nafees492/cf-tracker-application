package com.gourav.competrace.progress.user_submissions.util

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavBackStackEntry

//extensions
@Composable
inline fun <reified T : ViewModel> NavBackStackEntry?.viewModel(): T? = this?.let {
    viewModel(viewModelStoreOwner = it)
}

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.viewModel(
    viewModelStoreOwner: ViewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
        "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
    }
): T {
    return androidx.lifecycle.viewmodel.compose.viewModel(
        viewModelStoreOwner = viewModelStoreOwner, key = T::class.java.name
    )
}