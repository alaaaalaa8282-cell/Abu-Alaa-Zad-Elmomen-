package com.abueltaweel.data.bugReport.repository

import com.abueltaweel.data.bugReport.remote.BugReportRemoteDataSource
import com.abueltaweel.domain.model.BugReportRequest
import com.abueltaweel.domain.repository.bugReport.BugReportRepository

class BugReportRepositoryImpl(
    private val remoteDataSource: BugReportRemoteDataSource
) : BugReportRepository {

    override suspend fun submitBugReport(request: BugReportRequest) {
        remoteDataSource.submit(request)
    }
}