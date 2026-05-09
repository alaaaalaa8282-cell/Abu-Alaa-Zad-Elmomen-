package com.abueltaweel.presentation.screen.azkar

import com.abueltaweel.domain.repository.azkar.AzkarRepository
import com.abueltaweel.presentation.base.BaseViewModel

class AzkarViewModel(
    private val repository: AzkarRepository,
    private val analyticsHelper: AnalyticsHelper
) : BaseViewModel<AzkarUiState, AzkarEffect>(
    AzkarUiState(isLoading = true)
), AzkarInteractionListener {

    init {
        loadAzkar()
    }
    fun onScreenOpened() {
        analyticsHelper.logScreen("azkar")
    }
        private fun loadAzkar() {
        tryToCall(
            block = { repository.getAzkarCategories() },
            onSuccess = { list ->
                updateState {
                    it.copy(
                        isLoading = false,
                        categories = list.map { it.toUiModel() }
                    )
                }
            },
            onError = {
                updateState { it.copy(isLoading = false) }
                sendEffect(AzkarEffect.ShowError(it.message ?: "Error"))
            }
        )
    }

    override fun onClickCategory(type: AzkarType) {
        analyticsHelper.logEvent(
            name = "azkar",
            params = mapOf(
                "type" to type.name.lowercase(),
            )
        )
        sendEffect(AzkarEffect.NavigateToDetails(type))
    }

    override fun onClickBack() {
      sendEffect(AzkarEffect.NavigateToBack)
    }
}