package com.finnflare.scanpad.alien

import androidx.lifecycle.MutableLiveData
import com.finnflare.scanpad.alien.adapter.RecyclerItem
import com.finnflare.scanpad.database.CDatabasePresenter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.io.File
import java.util.regex.Pattern

@ObsoleteCoroutinesApi
class UIViewModel : KoinComponent {
    private val database by inject<CDatabasePresenter>()

    val resultsFileSaved = MutableLiveData<Boolean>(false)

    val scanResultCount = MutableLiveData(0)
    val scanResults = MutableLiveData<MutableList<RecyclerItem>>(mutableListOf())

    fun getScanResults() {
        CoroutineScope(database.dbDispatcher).launch {
            var count = 0
            scanResults.value?.let {
                it.addAll(database.getScanResults().map { res ->
                    count += res.value
                    RecyclerItem(res.key, res.value)
                })
            }
            scanResultCount.postValue(count)
            scanResults.postValue(scanResults.value)
        }
    }

    fun processScanResult(scanRes: String) {
        scanResults.value?.let {
            var result = ""

            if (Pattern.matches("^\\d{13}$", scanRes)) {
                result = scanRes
            }

            if (Pattern.matches("^.?01\\d{14}21.{13}" + 29.toChar() + "?.*$", scanRes)) {
                val startIndex: Int = scanRes.indexOf("01") + 2
                result = "01" + scanRes.substring(startIndex, startIndex + 14) +
                        "21" + scanRes.substring(startIndex + 16, startIndex + 29)
            }

            CoroutineScope(database.dbDispatcher).launch {
                when (database.processScanResult(result)) {
                    1 -> it.add(RecyclerItem(result, 1))
                    2 -> it.find { item -> item.scanResult == result }!!.scanCount += 1
                    else -> return@launch
                }

                scanResults.value?.sort()
                scanResults.postValue(scanResults.value)
                scanResultCount.postValue(scanResultCount.value?.plus(1))
            }
        }
    }

    fun clearScanResults() {
        CoroutineScope(database.dbDispatcher).launch {
            database.clearScanResults()

            scanResults.value?.clear()
            scanResults.postValue(scanResults.value)

            scanResultCount.postValue(0)
        }
    }

    private fun controlNumberGTIN(str: String): String? {
        var ch = 0
        var nch = 0
        for (i in str.indices step 2)
            ch += Character.digit(str[i], 10)

        for (i in 1 until str.length step 2)
            nch += Character.digit(str[i], 10)

        return ((10 - (ch + 3 * nch) % 10) % 10).toString()
    }

    fun uploadResultsFromFile(pathFile: File) {
        CoroutineScope(database.dbDispatcher).launch {
            database.uploadFromFile(pathFile)
            getScanResults()
        }
    }

    fun saveResultsToFile(saveDir: String) {
        CoroutineScope(database.dbDispatcher).launch {
            database.saveToFile(saveDir)
            resultsFileSaved.postValue(true)
        }
    }
}