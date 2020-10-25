package com.finnflare.scanpad.database

import com.finnflare.scanpad.database.entity.CEntityScanResults
import kotlinx.coroutines.*
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

@ObsoleteCoroutinesApi
class CDatabasePresenter : KoinComponent {
    private val database by inject<CAppDatabase>()

    val dbDispatcher: CoroutineDispatcher = newSingleThreadContext("DatabaseCoroutine")

    suspend fun getScanResults(): Map<String, Int> {
        val result = mutableMapOf<String, Int>()

        CoroutineScope(dbDispatcher).launch {
            result.apply {
                database.scanResults().getScanResults().forEach {
                    this[it.mScanRes] = it.mCount
                }
            }
        }.join()

        return result
    }

    suspend fun processScanResult(scanRes: String, scanCount: Int = 1): Int {
        var result = 0

        CoroutineScope(dbDispatcher).launch {
            if (Pattern.matches("^\\d{13}$", scanRes)) {
                result = if (database.scanResults().findScanResult(scanRes) == 0) {
                    database.scanResults().appendScanResults(CEntityScanResults(scanRes, 1))
                    1
                } else {
                    database.scanResults().increaseScanResultCount(scanRes, scanCount)
                    2
                }
                return@launch
            }

            if (database.scanResults().findScanResult(scanRes) != 0)
                return@launch

            database.scanResults().appendScanResults(CEntityScanResults(scanRes, 1))
            result = 1

        }.join()

        return result
    }

    suspend fun clearScanResults() {
        CoroutineScope(dbDispatcher).launch {
            database.scanResults().truncateTable()
        }.join()
    }

    suspend fun saveToFile(saveDir: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val fileName =
                SimpleDateFormat("dd-MM-yyyy_HH-mm-ss", Locale("RU")).format(Date()) + ".txt"

            val writer = File(saveDir + File.separator + fileName).writer()

            database.scanResults().getScanResults().forEach {
                for (i in 1..it.mCount)
                    writer.write(it.mScanRes + "\r\n")
            }

            writer.close()
        }.join()
    }

    suspend fun uploadFromFile(pathFile: File) {
        CoroutineScope(Dispatchers.IO).launch {
            val savedRes = mutableListOf<CEntityScanResults>()

            pathFile.forEachLine {
                if (savedRes.size == 0 || it != savedRes.last().mScanRes)
                    savedRes.add(CEntityScanResults(it, 1))
                else
                    savedRes.last().mCount += 1
            }

            database.scanResults().refillTable(savedRes)
        }.join()
    }
}