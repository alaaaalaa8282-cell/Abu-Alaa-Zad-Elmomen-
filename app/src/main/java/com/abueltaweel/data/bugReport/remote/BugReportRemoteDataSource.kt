package com.abueltaweel.data.bugReport.remote

import com.abueltaweel.domain.model.BugReportRequest

interface BugReportRemoteDataSource {
    suspend fun submit(
        report: BugReportRequest
    )
}