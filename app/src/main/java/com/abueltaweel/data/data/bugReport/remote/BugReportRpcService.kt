package com.abueltaweel.data.bugReport.remote

import com.abueltaweel.data.bugReport.remote.dto.DailyReportCountDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc

class BugReportRpcService(
    private val supabase: SupabaseClient
) {

    suspend fun getDailyCount(
        deviceId: String,
        dayStamp: Long
    ): Long {
        return supabase.postgrest
            .rpc(
                "get_daily_reports_count",
                DailyReportCountDto(
                    uid = deviceId,
                    dayStamp = dayStamp
                )
            )
            .decodeAs<Long>()
    }
}