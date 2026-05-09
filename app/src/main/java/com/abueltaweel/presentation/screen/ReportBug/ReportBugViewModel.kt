package com.abueltaweel.presentation.screen.ReportBug

import android.util.Log
import com.abueltaweel.R
import com.abueltaweel.data.bugReport.remote.DailyLimitExceededException
import com.abueltaweel.domain.model.BugReportRequest
import com.abueltaweel.domain.repository.bugReport.BugReportRepository
import com.abueltaweel.presentation.base.BaseViewModel

class ReportBugViewModel(
    private val bugReportRepository: BugReportRepository
) : BaseViewModel<ReportBugUiState, ReportBugEffect>(ReportBugUiState()),
    ReportBugInteractionListener {

    override fun onTitleChange(value: String) {
        updateState { it.copy(title = value) }
    }

    override fun onDescriptionChange(value: String) {
        updateState { it.copy(description = value) }
    }

    override fun onFeatureSelected(value: FeatureArea) {
        updateState { it.copy(feature = value) }
    }

    override fun onImageSelected(url: String) {
        updateState { it.copy(imageUrl = url) }
    }

    override fun onSubmitClick() {
        val state = screenState.value

        if (state.title.isBlank() || state.description.isBlank()) {
            sendEffect(ReportBugEffect.InvalidInput)
            return
        }

        tryToCall(
            block = {
                bugReportRepository
                    .submitBugReport(
                        BugReportRequest(
                            title = state.title,
                            description = state.description,
                            imageUrl = state.imageUrl,
                            featureArea = state.feature.name
                        )
                    )

            },
            onStart = {
                updateState { it.copy(isLoading = true) }
            },
            onSuccess = {
                updateState { it.copy(isLoading = false) }
                sendEffect(ReportBugEffect.Success)
                updateState { ReportBugUiState() }
            },
            onError = { throwable ->
                updateState { it.copy(isLoading = false) }
                Log.e(
                    "ReportBugViewModel",
                    "Error submitting bug report ${throwable.message.toString()}"
                )
                when (throwable) {
                    is DailyLimitExceededException -> sendEffect(ReportBugEffect.LimitReached)
                    else -> {
                        Log.e("ReportBugViewModel", "Error submitting bug report ${throwable.message}")
                        sendEffect(ReportBugEffect.Error(R.string.failed_to_send_report))
                    }
                }
            }
        )
    }
}