package com.abueltaweel.domain.repository.bugReport

import com.abueltaweel.domain.model.BugReportRequest

interface BugReportRepository {
    suspend fun submitBugReport(request: BugReportRequest)
}