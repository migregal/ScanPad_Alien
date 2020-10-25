package com.finnflare.scanpad.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scan_results")
data class CEntityScanResults(
    @ColumnInfo(name = "_scan_res") @PrimaryKey var mScanRes: String,
    @ColumnInfo(name = "_count") var mCount: Int
)