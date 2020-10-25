package com.finnflare.scanpad.database.dao

import androidx.room.*
import com.finnflare.scanpad.database.entity.CEntityScanResults

@Dao
interface CScanResultDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun appendScanResults(scanResults: List<CEntityScanResults>)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun appendScanResults(vararg scanResult: CEntityScanResults)

    @Query("SELECT * FROM scan_results ORDER BY _scan_res")
    fun getScanResults(): List<CEntityScanResults>

    @Query("SELECT COUNT(_scan_res) FROM scan_results WHERE _scan_res == :scanResult")
    fun findScanResult(scanResult: String): Int

    @Query(
        """
        UPDATE scan_results
        SET _count = _count + :count 
        WHERE _scan_res = :scanResult
        """
    )
    fun increaseScanResultCount(scanResult: String, count: Int)

    @Transaction
    fun refillTable(newValues: List<CEntityScanResults>) {
        truncateTable()
        appendScanResults(newValues)
    }

    @Query("DELETE FROM scan_results")
    fun truncateTable()
}