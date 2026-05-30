package com.abueltaweel.data.util.helpers

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import java.net.UnknownHostException
import java.net.SocketTimeoutException

suspend fun <T> safeCall(block: suspend () -> T): T? {
    return try {
        block()
    } catch (e: UnknownHostException) {
        null  // مفيش نت
    } catch (e: SocketTimeoutException) {
        null  // timeout
    } catch (e: Throwable) {
        null
    }
}

fun <T> Flow<T>.safeFlow(): Flow<T> =
    catch { e ->
        // مش بنرمي — بنتجاهل الخطأ
    }
