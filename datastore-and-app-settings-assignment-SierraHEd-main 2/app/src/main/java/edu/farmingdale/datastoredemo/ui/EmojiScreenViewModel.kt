package edu.farmingdale.datastoredemo.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import edu.farmingdale.datastoredemo.R
import edu.farmingdale.datastoredemo.EmojiReleaseApplication
import edu.farmingdale.datastoredemo.data.local.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class EmojiScreenViewModel(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    // combined both linearLayout and DarkTheme within uiState so both can be accessed
    val uiState: StateFlow<EmojiReleaseUiState> =
        combine(
            userPreferencesRepository.isLinearLayout,
            userPreferencesRepository.isDarkTheme
        ) { isLinearLayout, isDarkTheme ->
            EmojiReleaseUiState(isLinearLayout = isLinearLayout, isDark = isDarkTheme)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = EmojiReleaseUiState()
        )

    /*
     * [selectLayout] change the layout and icons accordingly and
     * save the selection in DataStore through [userPreferencesRepository]
     */
    fun selectLayout(isLinearLayout: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.saveLayoutPreference(isLinearLayout)
        }
    }

    //Added function to save current background theme
    fun changeColor(isDark: Boolean){
        viewModelScope.launch{
            userPreferencesRepository.saveThemePreference(isDark)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as EmojiReleaseApplication)
                EmojiScreenViewModel(application.userPreferencesRepository)
            }
        }
    }
}

/*
 * Data class containing various UI States for Emoji Release screens
 */
data class EmojiReleaseUiState(
    val isLinearLayout: Boolean = true,
    val toggleContentDescription: Int =
        if (isLinearLayout) R.string.grid_layout_toggle else R.string.linear_layout_toggle,
    val toggleIcon: Int =
        if (isLinearLayout) R.drawable.ic_grid_layout else R.drawable.ic_linear_layout,
    val isDark: Boolean = false
)
