package com.abueltaweel.presentation.screen.AzkarDetails

import com.abueltaweel.domain.repository.azkar.AzkarRepository
import com.abueltaweel.presentation.base.BaseViewModel

class AzkarDetailViewModel(
    private val repository: AzkarRepository
) : BaseViewModel<AzkarDetailUiState, AzkarDetailEffect>(
    AzkarDetailUiState()
), AzkarDetailInteractionListener {

    fun loadAzkar(title: String) {
        updateState { it.copy(isLoading = true, title = title) }

        tryToCall(
            block = {
                repository.getAzkarCategories()
                    .firstOrNull { it.title == title }
                    ?.items
                    .orEmpty()
            },
            onSuccess = { items ->
                updateState {
                    it.copy(
                        isLoading = false,
                        items = items
                    )
                }
            },
            onError = {
                sendEffect(AzkarDetailEffect.ShowError(it.message ?: "Error"))
            }
        )
    }

    override fun onClickBack() {
        sendEffect(AzkarDetailEffect.NavigateBack)
    }
}